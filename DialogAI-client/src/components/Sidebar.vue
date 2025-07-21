<template>
  <div class="sidebar">
    <!-- 新建对话按钮 -->
    <div class="sidebar-header">
      <button class="new-chat-btn" @click="$emit('new-conversation')">
        <Plus size="16" />
        <span>新建对话</span>
      </button>
    </div>
    
    <!-- 会话列表 -->
    <div class="conversations-list">
      <div 
        v-for="conversation in conversations" 
        :key="conversation.id"
        class="conversation-item"
        :class="{ active: conversation.id === currentConversationId }"
        @click="$emit('select-conversation', conversation.id)"
      >
        <div class="conversation-content">
          <div class="conversation-title">
            {{ conversation.title || '新对话' }}
          </div>
          <div class="conversation-preview">
            {{ conversation.description || '开始新的对话...' }}
          </div>
          <div class="conversation-meta">
            <span class="message-count">{{ conversation.messageCount || 0 }} 条消息</span>
            <span class="conversation-time">{{ formatRelativeTime(conversation.updatedAt) }}</span>
          </div>
        </div>
        
        <!-- 删除按钮 -->
        <button 
          class="delete-btn"
          @click.stop="$emit('delete-conversation', conversation.id)"
          title="删除对话"
        >
          <Trash2 size="14" />
        </button>
      </div>
      
      <!-- 空状态 -->
      <div v-if="conversations.length === 0" class="empty-state">
        <MessageCircle size="48" />
        <p>暂无对话记录</p>
        <p>开始您的第一次对话吧！</p>
      </div>
    </div>
  </div>
</template>

<script>
import { Plus, Trash2, MessageCircle } from 'lucide-vue-next'
import { formatRelativeTime } from '../utils/date'

export default {
  name: 'Sidebar',
  components: {
    Plus,
    Trash2,
    MessageCircle
  },
  props: {
    conversations: {
      type: Array,
      default: () => []
    },
    currentConversationId: {
      type: [Number, String],
      default: null
    }
  },
  emits: ['select-conversation', 'new-conversation', 'delete-conversation'],
  setup() {
    return {
      formatRelativeTime
    }
  }
}
</script>

<style scoped>
.sidebar {
  width: 300px;
  background-color: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.new-chat-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.new-chat-btn:hover {
  background-color: var(--primary-hover);
}

.conversations-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-bottom: 4px;
}

.conversation-item:hover {
  background-color: var(--border-color);
}

.conversation-item.active {
  background-color: var(--primary-color);
  color: white;
}

.conversation-item.active .conversation-preview {
  color: rgba(255, 255, 255, 0.8);
}

.conversation-item.active .conversation-meta {
  color: rgba(255, 255, 255, 0.6);
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-preview {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.conversation-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--text-secondary);
}

.message-count {
  font-weight: 500;
}

.delete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  border-radius: 4px;
  color: var(--text-secondary);
  cursor: pointer;
  opacity: 0;
  transition: all 0.2s ease;
}

.conversation-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background-color: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: var(--text-secondary);
}

.empty-state svg {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin: 4px 0;
  font-size: 14px;
}

.empty-state p:first-of-type {
  font-weight: 500;
  color: var(--text-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar.open {
    transform: translateX(0);
  }
}
</style> 