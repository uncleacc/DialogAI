import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '../api/chat'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const conversations = ref([])
  const currentConversationId = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const isNewConversationMode = ref(false) // 新增：新会话模式状态
  
  // 计算属性
  const currentMessages = computed(() => {
    if (!currentConversationId.value && !isNewConversationMode.value) return []
    if (isNewConversationMode.value) {
      // 在新会话模式下，显示临时消息
      return messages.value.filter(msg => msg.conversationId === 'new-conversation-temp')
    }
    return messages.value.filter(msg => msg.conversationId === currentConversationId.value)
  })
  
  // 开始新会话模式（不立即创建会话）
  const startNewConversation = () => {
    console.log('[Chat Store] 开始新会话模式')
    currentConversationId.value = null
    isNewConversationMode.value = true
    // 清空当前显示的消息，但不影响其他会话的消息
    messages.value = messages.value.filter(msg => msg.conversationId !== 'new-conversation-temp')
  }
  
  // 发送消息
  const sendMessage = async (content, userId = 'default-user') => {
    try {
      isLoading.value = true
      error.value = null

      // 如果是新会话模式，先创建临时用户消息
      if (isNewConversationMode.value) {
        const tempUserMsg = {
          id: 'temp-user-' + Date.now(),
          content: content,
          role: 'USER',
          status: 'SENT',
          sequenceNumber: 1,
          conversationId: 'new-conversation-temp',
          createdAt: new Date().toISOString()
        }
        messages.value.push(tempUserMsg)
      }

      // 只在有会话ID时才传 conversationId
      const data = {
        message: content,
        userId: userId
      }
      if (currentConversationId.value) {
        data.conversationId = currentConversationId.value
      }

      const response = await chatApi.sendMessage(data)
      
      if (response.success) {
        // 如果是新会话模式，需要替换临时消息
        if (isNewConversationMode.value) {
          // 移除临时消息
          messages.value = messages.value.filter(msg => msg.conversationId !== 'new-conversation-temp')
          isNewConversationMode.value = false
          currentConversationId.value = response.conversationId
        }
        
        // 添加真实的用户消息
        messages.value.push({
          id: response.userMessage.id,
          content: content,
          role: 'USER',
          status: 'SENT',
          sequenceNumber: response.userMessage.sequenceNumber,
          conversationId: response.conversationId,
          createdAt: response.userMessage.createdAt
        })
        
        // 添加AI回复
        messages.value.push({
          id: response.aiMessage.id,
          content: response.aiMessage.content,
          role: 'ASSISTANT',
          status: 'SENT',
          sequenceNumber: response.aiMessage.sequenceNumber,
          conversationId: response.conversationId,
          createdAt: response.aiMessage.createdAt
        })
        
        // 更新当前会话ID
        if (!currentConversationId.value) {
          currentConversationId.value = response.conversationId
        }
        
        // 刷新会话列表
        await loadConversations(userId)
      }
    } catch (err) {
      error.value = err.message || '发送消息失败'
      console.error('发送消息错误:', err)
      
      // 发送失败时，如果是新会话模式，清理临时消息
      if (isNewConversationMode.value) {
        messages.value = messages.value.filter(msg => msg.conversationId !== 'new-conversation-temp')
      }
    } finally {
      isLoading.value = false
    }
  }
  
  // 流式发送消息（AI逐字回复）
  const sendMessageStream = async (content, userId = 'default-user') => {
    // 累积内容
    let accumulatedContent = '';
    console.log('[埋点] 开始流式发送消息:', { content, userId, currentConversationId: currentConversationId.value, isNewConversationMode: isNewConversationMode.value });
    isLoading.value = true
    error.value = null
    
    try {
      // 1. 先插入用户消息
      let userMsgConversationId = currentConversationId.value
      let aiMsgConversationId = currentConversationId.value
      
      // 如果是新会话模式，使用临时ID
      if (isNewConversationMode.value) {
        userMsgConversationId = 'new-conversation-temp'
        aiMsgConversationId = 'new-conversation-temp'
      }
      
      const userMsg = {
        id: Date.now() - 1,
        content: content,
        role: 'USER',
        status: 'SENT',
        sequenceNumber: messages.value.length + 1,
        conversationId: userMsgConversationId,
        createdAt: new Date().toISOString()
      };
      
      // 2. 创建空的AI消息气泡
      const aiMsgId = Date.now();
      const aiMsg = {
        id: aiMsgId,
        content: '',
        role: 'ASSISTANT',
        status: 'SENT',
        sequenceNumber: messages.value.length + 2,
        conversationId: aiMsgConversationId,
        createdAt: new Date().toISOString()
      }
      console.log('[埋点] 创建AI消息对象:', aiMsg);
      
      // 3. 添加消息到列表
      messages.value.push(userMsg);
      messages.value.push(aiMsg);
      console.log('[埋点] 消息已添加到列表，当前消息数量:', messages.value.length);

      // 2. 发起流式请求
      const data = {
        message: content,
        userId: userId
      }
      if (currentConversationId.value) {
        data.conversationId = currentConversationId.value
      }
      console.log('[埋点] 准备发送流式请求:', data);
      
      const response = await fetch('/api/chat/stream-send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      console.log('[埋点] 流式请求响应状态:', response.status, response.statusText);
      
      // 检查响应状态
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      if (!response.body) {
        throw new Error('无流式响应');
      }
      console.log('[埋点] 开始读取流式响应');
      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');
      let done = false;
      let buffer = '';
      let chunkCount = 0;
      
      // 添加原始数据调试
      console.log('[调试] 响应头信息:', {
        contentType: response.headers.get('content-type'),
        contentLength: response.headers.get('content-length'),
        transferEncoding: response.headers.get('transfer-encoding')
      });
      
      while (!done) {
        const { value, done: streamDone } = await reader.read();
        done = streamDone;
        chunkCount++;
        
        // 详细记录每个数据块
        console.log(`[调试] 读取第${chunkCount}个数据块:`, { 
          valueLength: value ? value.length : 0, 
          done: streamDone,
          valueBytes: value ? Array.from(value).map(b => b.toString(16).padStart(2, '0')).join(' ') : 'null'
        });
        
        if (value) {
          const chunk = decoder.decode(value, { stream: true });
          buffer += chunk;
          
          // 详细记录解码后的内容
          console.log('[调试] 解码后的数据块:', JSON.stringify(chunk));
          console.log('[调试] 当前buffer内容:', JSON.stringify(buffer));
          
          // 检查是否包含SSE格式
          if (chunk.includes('data:')) {
            console.log('[调试] 发现SSE格式数据');
          }
          
          // 改进的SSE数据处理逻辑
          const lines = buffer.split('\n');
          buffer = lines.pop() || ''; // 保留不完整的行
          console.log('[调试] 分割后的行数:', lines.length);
          
          for (const line of lines) {
            console.log('[调试] 处理行:', JSON.stringify(line));
            if (line.startsWith('data:')) {
              const data = line.substring(5).trim();
              console.log('[调试] 提取的data内容:', JSON.stringify(data));
              
              // 立即处理每个数据块，不等待完整事件
              if (data && data !== '[DONE]' && data.trim() !== '') {
                // 累积内容
                accumulatedContent += data;
                console.log('[调试] 累积内容长度:', accumulatedContent.length);
                
                // 立即更新UI，不等待
                const messageIndex = messages.value.findIndex(msg => msg.id === aiMsgId);
                if (messageIndex !== -1) {
                  // 创建新的消息对象以确保响应式更新
                  const currentMsg = messages.value[messageIndex];
                  const updatedMsg = { 
                    ...currentMsg, 
                    content: accumulatedContent,
                    updatedAt: new Date().toISOString() // 添加更新时间确保响应式
                  };
                  
                  // 使用splice确保数组引用变化
                  messages.value.splice(messageIndex, 1, updatedMsg);
                  console.log('[调试] 实时更新完成，内容长度:', updatedMsg.content.length);
                  
                  // 强制触发响应式更新
                  messages.value = [...messages.value];
                }
              }
            }
          }
        }
      }
      
      console.log('[埋点] 流式读取完成，总数据块数:', chunkCount);
      
      // 最终更新（确保内容完整）
      const messageIndex = messages.value.findIndex(msg => msg.id === aiMsgId);
      if (messageIndex !== -1) {
        const currentMsg = messages.value[messageIndex];
        const finalMsg = { ...currentMsg, content: accumulatedContent };
        messages.value.splice(messageIndex, 1, finalMsg);
        console.log('[埋点] 最终更新完成，内容长度:', finalMsg.content.length);
      }
      
      // 流式请求完成后，刷新会话列表以获取最新的conversationId
      console.log('[埋点] 开始刷新会话列表');
      await loadConversations(userId)
      console.log('[埋点] 会话列表刷新完成，会话数量:', conversations.value.length);
      
      // 处理新会话模式的ID更新
      if (isNewConversationMode.value && conversations.value.length > 0) {
        // 获取最新创建的会话ID（通常是列表第一个）
        const newConversationId = conversations.value[0].id;
        console.log('[埋点] 新会话创建，会话ID:', newConversationId);
        
        // 更新临时消息的conversationId为真实ID
        messages.value = messages.value.map(msg => {
          if (msg.conversationId === 'new-conversation-temp') {
            return { ...msg, conversationId: newConversationId };
          }
          return msg;
        });
        
        // 退出新会话模式，设置当前会话ID
        isNewConversationMode.value = false;
        currentConversationId.value = newConversationId;
        console.log('[埋点] 新会话模式结束，当前会话ID:', currentConversationId.value);
      } else if (!currentConversationId.value && conversations.value.length > 0) {
        currentConversationId.value = conversations.value[0].id;
        console.log('[埋点] 更新当前会话ID:', currentConversationId.value);
      }
    } catch (err) {
      console.error('[埋点] 流式发送消息异常:', err);
      error.value = err.message || '发送消息失败';
      console.error('流式发送消息错误:', err);
      
      // 发送失败时，如果是新会话模式，清理临时消息
      if (isNewConversationMode.value) {
        messages.value = messages.value.filter(msg => msg.conversationId !== 'new-conversation-temp')
        isNewConversationMode.value = false
      }
    } finally {
      console.log('[埋点] 流式发送消息完成，设置loading为false');
      isLoading.value = false;
    }
  }
  
  // 加载会话列表
  const loadConversations = async (userId = 'default-user') => {
    try {
      const response = await chatApi.getConversations(userId)
      conversations.value = response
    } catch (err) {
      console.error('加载会话列表错误:', err)
    }
  }
  
  // 加载会话详情
  const loadConversation = async (conversationId, userId = 'default-user') => {
    try {
      const response = await chatApi.getConversation(conversationId, userId)
      currentConversationId.value = conversationId
      isNewConversationMode.value = false // 退出新会话模式
      
      // 更新消息列表
      const conversationMessages = response.messages || []
      messages.value = messages.value.filter(msg => msg.conversationId !== conversationId)
      messages.value.push(...conversationMessages)
      
      return response
    } catch (err) {
      console.error('加载会话详情错误:', err)
      throw err
    }
  }
  
  // 创建新会话（保留原有方法，用于其他地方可能的调用）
  const createConversation = async (title, description = '', userId = 'default-user') => {
    try {
      const response = await chatApi.createConversation(title, description, userId)
      conversations.value.unshift(response)
      currentConversationId.value = response.id
      isNewConversationMode.value = false // 退出新会话模式
      return response
    } catch (err) {
      console.error('创建会话错误:', err)
      throw err
    }
  }
  
  // 清空当前会话
  const clearCurrentConversation = () => {
    currentConversationId.value = null
    isNewConversationMode.value = false
    messages.value = messages.value.filter(msg => msg.conversationId !== currentConversationId.value && msg.conversationId !== 'new-conversation-temp')
  }
  
  // 删除会话
  const deleteConversation = async (conversationId, userId = 'default-user') => {
    try {
      await chatApi.deleteConversation(conversationId, userId)
      conversations.value = conversations.value.filter(conv => conv.id !== conversationId)
      
      if (currentConversationId.value === conversationId) {
        clearCurrentConversation()
      }
    } catch (err) {
      console.error('删除会话错误:', err)
      throw err
    }
  }
  
  // 重命名会话
  const renameConversation = async (conversationId, newTitle, userId = 'default-user') => {
    try {
      await chatApi.updateConversationTitle(conversationId, newTitle, userId)
      
      // 更新本地会话列表中的标题
      const conversationIndex = conversations.value.findIndex(conv => conv.id === conversationId)
      if (conversationIndex !== -1) {
        conversations.value[conversationIndex] = {
          ...conversations.value[conversationIndex],
          title: newTitle
        }
      }
    } catch (err) {
      console.error('重命名会话错误:', err)
      throw err
    }
  }
  
  // 测试响应式更新
  const testReactiveUpdate = () => {
    console.log('[测试] 开始测试响应式更新');
    const testMsg = {
      id: Date.now(),
      content: '测试消息',
      role: 'ASSISTANT',
      status: 'SENT',
      sequenceNumber: messages.value.length + 1,
      conversationId: currentConversationId.value,
      createdAt: new Date().toISOString()
    };
    messages.value.push(testMsg);
    console.log('[测试] 添加测试消息，当前消息数量:', messages.value.length);
    
    // 3秒后更新消息内容
    setTimeout(() => {
      const index = messages.value.findIndex(msg => msg.id === testMsg.id);
      if (index !== -1) {
        const updatedMsg = { ...testMsg, content: '测试消息已更新' };
        messages.value.splice(index, 1, updatedMsg);
        console.log('[测试] 更新测试消息内容');
      }
    }, 3000);
  }
  
    // 测试流式请求
  const testStreamRequest = async () => {
    console.log('[测试] 开始测试流式请求');
    
    // 先创建一个空的AI消息
    const testMsgId = Date.now();
    const testMsg = {
      id: testMsgId,
      content: '',
      role: 'ASSISTANT',
      status: 'SENT',
      sequenceNumber: messages.value.length + 1,
      conversationId: currentConversationId.value,
      createdAt: new Date().toISOString()
    };
    messages.value.push(testMsg);
    console.log('[测试] 创建测试消息，当前消息数量:', messages.value.length);
    
    try {
      const response = await fetch('/api/chat/stream-send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          message: '测试消息',
          userId: 'default-user'
        })
      });
      
      console.log('[测试] 流式请求响应状态:', response.status, response.statusText);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      if (!response.body) {
        throw new Error('无流式响应');
      }
      
      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');
      let buffer = '';
      let chunkCount = 0;
      
      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        
        chunkCount++;
        const chunk = decoder.decode(value, { stream: true });
        buffer += chunk;
        console.log(`[测试] 第${chunkCount}个数据块:`, chunk);
        
        // 按行分割SSE数据
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';
        
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const data = line.substring(5).trim();
            if (data && data !== '[DONE]') {
              console.log('[测试] 收到数据:', data);
              
              // 实时更新测试消息
              const messageIndex = messages.value.findIndex(msg => msg.id === testMsgId);
              if (messageIndex !== -1) {
                const currentMsg = messages.value[messageIndex];
                const updatedMsg = { ...currentMsg, content: currentMsg.content + data };
                messages.value.splice(messageIndex, 1, updatedMsg);
                console.log('[测试] 实时更新测试消息，内容长度:', updatedMsg.content.length);
              }
            }
          }
        }
      }
      
      console.log('[测试] 流式请求测试完成');
      
      // 测试完成后清理测试消息
      const index = messages.value.findIndex(msg => msg.id === testMsgId);
      if (index !== -1) {
        messages.value.splice(index, 1);
        console.log('[测试] 清理测试消息');
      }
    } catch (error) {
      console.error('[测试] 流式请求测试失败:', error);
      
      // 出错时也清理测试消息
      const index = messages.value.findIndex(msg => msg.id === testMsgId);
      if (index !== -1) {
        messages.value.splice(index, 1);
        console.log('[测试] 清理测试消息');
      }
    }
  }
  
  return {
    messages,
    conversations,
    currentConversationId,
    isLoading,
    error,
    isNewConversationMode,
    currentMessages,
    sendMessage,
    loadConversations,
    loadConversation,
    createConversation,
    startNewConversation, // 新增方法
    clearCurrentConversation,
    deleteConversation,
    renameConversation, // 新增重命名方法
    sendMessageStream,
    testReactiveUpdate,
    testStreamRequest
  }
}) 