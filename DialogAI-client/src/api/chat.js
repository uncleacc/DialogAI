import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token等
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('API请求错误:', error)
    if (error.response) {
      // 服务器返回错误状态码
      const message = error.response.data?.message || `请求失败 (${error.response.status})`
      return Promise.reject(new Error(message))
    } else if (error.request) {
      // 网络错误
      return Promise.reject(new Error('网络连接失败，请检查网络设置'))
    } else {
      // 其他错误
      return Promise.reject(new Error(error.message || '请求失败'))
    }
  }
)

export const chatApi = {
  // 发送聊天消息
  sendMessage: (data) => {
    return api.post('/chat/send', data)
  },
  
  // 获取会话列表
  getConversations: (userId) => {
    return api.get('/chat/conversations', {
      params: { userId }
    })
  },
  
  // 分页获取会话列表
  getConversationsPage: (params) => {
    return api.get('/chat/conversations/page', { params })
  },
  
  // 获取会话详情
  getConversation: (conversationId, userId) => {
    return api.get(`/chat/conversations/${conversationId}`, {
      params: { userId }
    })
  },
  
  // 创建新会话
  createConversation: (title, description, userId) => {
    return api.post('/chat/conversations', null, {
      params: { title, description, userId }
    })
  },
  
  // 更新会话标题
  updateConversationTitle: (conversationId, title, userId) => {
    return api.put(`/chat/conversations/${conversationId}/title`, null, {
      params: { title, userId }
    })
  },
  
  // 归档会话
  archiveConversation: (conversationId, userId) => {
    return api.put(`/chat/conversations/${conversationId}/archive`, null, {
      params: { userId }
    })
  },
  
  // 删除会话
  deleteConversation: (conversationId, userId) => {
    return api.delete(`/chat/conversations/${conversationId}`, {
      params: { userId }
    })
  },
  
  // 搜索会话
  searchConversations: (keyword) => {
    return api.get('/chat/conversations/search', {
      params: { keyword }
    })
  },
  
  // 清空会话消息
  clearConversationMessages: (conversationId, userId) => {
    return api.delete(`/chat/conversations/${conversationId}/messages`, {
      params: { userId }
    })
  }
} 