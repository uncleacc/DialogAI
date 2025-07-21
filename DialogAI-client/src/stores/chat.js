import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '../api/chat'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const conversations = ref([])
  const currentConversationId = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  
  // 计算属性
  const currentMessages = computed(() => {
    if (!currentConversationId.value) return []
    return messages.value.filter(msg => msg.conversationId === currentConversationId.value)
  })
  
  // 发送消息
  const sendMessage = async (content, userId = 'default-user') => {
    try {
      isLoading.value = true
      error.value = null

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
        // 添加用户消息
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
    } finally {
      isLoading.value = false
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
  
  // 创建新会话
  const createConversation = async (title, description = '', userId = 'default-user') => {
    try {
      const response = await chatApi.createConversation(title, description, userId)
      conversations.value.unshift(response)
      currentConversationId.value = response.id
      return response
    } catch (err) {
      console.error('创建会话错误:', err)
      throw err
    }
  }
  
  // 清空当前会话
  const clearCurrentConversation = () => {
    currentConversationId.value = null
    messages.value = messages.value.filter(msg => msg.conversationId !== currentConversationId.value)
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
  
  return {
    messages,
    conversations,
    currentConversationId,
    isLoading,
    error,
    currentMessages,
    sendMessage,
    loadConversations,
    loadConversation,
    createConversation,
    clearCurrentConversation,
    deleteConversation
  }
}) 