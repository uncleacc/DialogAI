<template>
  <div class="chat-container">
    <!-- 侧边栏 -->
    <Sidebar 
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      @select-conversation="selectConversation"
      @new-conversation="newConversation"
      @delete-conversation="deleteConversation"
    />
    
    <!-- 主聊天区域 -->
    <div class="main-chat-area">
      <!-- 顶部工具栏 -->
      <div class="chat-header">
        <div class="header-left">
          <h1 class="chat-title">DialogAI</h1>
          <span class="chat-subtitle">智能对话助手</span>
        </div>
        <div class="header-right">
          <button @click="testReactiveUpdate" class="test-btn">测试响应式</button>
          <button @click="testStreamRequest" class="test-btn">测试流式</button>
          <ThemeToggle />
        </div>
      </div>
      
      <!-- 消息列表 -->
      <MessageList 
        :messages="currentMessages"
        :is-loading="isLoading"
        @regenerate="regenerateMessage"
      />
      
      <!-- 输入区域 -->
      <MessageInput 
        :is-loading="isLoading"
        @send-message="sendMessage"
      />
    </div>
  </div>
</template>

<script>
import { onMounted, computed } from 'vue'
import { useChatStore } from '../stores/chat'
import Sidebar from '../components/Sidebar.vue'
import MessageList from '../components/MessageList.vue'
import MessageInput from '../components/MessageInput.vue'
import ThemeToggle from '../components/ThemeToggle.vue'

export default {
  name: 'ChatView',
  components: {
    Sidebar,
    MessageList,
    MessageInput,
    ThemeToggle
  },
  setup() {
    const chatStore = useChatStore()
    
    // 计算属性
    const conversations = computed(() => chatStore.conversations)
    const currentMessages = computed(() => chatStore.currentMessages)
    const currentConversationId = computed(() => chatStore.currentConversationId)
    const isLoading = computed(() => chatStore.isLoading)
    
    // 方法
    const sendMessage = async (content) => {
      console.log('[ChatView] 开始发送消息:', content);
      try {
        await chatStore.sendMessageStream(content);
        console.log('[ChatView] 消息发送完成');
      } catch (error) {
        console.error('[ChatView] 消息发送失败:', error);
      }
    }
    
    const selectConversation = async (conversationId) => {
      try {
        await chatStore.loadConversation(conversationId)
      } catch (error) {
        console.error('选择会话失败:', error)
      }
    }
    
    const newConversation = async () => {
      try {
        await chatStore.createConversation('新对话')
      } catch (error) {
        console.error('创建新会话失败:', error)
      }
    }
    
    const deleteConversation = async (conversationId) => {
      try {
        await chatStore.deleteConversation(conversationId)
      } catch (error) {
        console.error('删除会话失败:', error)
      }
    }
    
    const regenerateMessage = async (messageId) => {
      // 重新生成最后一条AI消息
      const lastAiMessage = currentMessages.value
        .filter(msg => msg.role === 'ASSISTANT')
        .pop()
      
      if (lastAiMessage) {
        // 删除最后一条AI消息，重新发送用户消息
        const userMessages = currentMessages.value.filter(msg => msg.role === 'USER')
        const lastUserMessage = userMessages[userMessages.length - 1]
        
        if (lastUserMessage) {
          // 移除最后一条AI消息
          chatStore.messages = chatStore.messages.filter(msg => msg.id !== lastAiMessage.id)
          // 重新发送用户消息
          await sendMessage(lastUserMessage.content)
        }
      }
    }
    
    const testReactiveUpdate = () => {
      chatStore.testReactiveUpdate()
    }
    
    const testStreamRequest = () => {
      chatStore.testStreamRequest()
    }
    
    // 初始化
    onMounted(async () => {
      await chatStore.loadConversations()
    })
    
    return {
      conversations,
      currentMessages,
      currentConversationId,
      isLoading,
      sendMessage,
      selectConversation,
      newConversation,
      deleteConversation,
      regenerateMessage,
      testReactiveUpdate,
      testStreamRequest
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background-color: var(--bg-color);
}

.main-chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--bg-color);
  z-index: 10;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.chat-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  margin: 0;
}

.chat-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.test-btn {
  padding: 8px 16px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.test-btn:hover {
  background-color: #0d9488;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 12px 16px;
  }
  
  .chat-title {
    font-size: 18px;
  }
  
  .chat-subtitle {
    font-size: 12px;
  }
}
</style> 