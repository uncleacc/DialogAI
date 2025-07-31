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
        :class="{ active: conversation.id === currentConversationId && !isNewConversationMode }"
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
        
        <!-- 操作按钮 -->
        <div class="conversation-actions">
          <button 
            class="action-btn"
            @click.stop="toggleActionMenu(conversation.id)"
            title="更多操作"
          >
            <MoreVertical size="14" />
          </button>
          
          <!-- 操作菜单 -->
          <div 
            v-if="activeMenuId === conversation.id"
            class="action-menu"
            @click.stop
          >
            <button 
              class="menu-item"
              @click="startRename(conversation)"
            >
              <Edit3 size="14" />
              重命名
            </button>
            <button 
              class="menu-item delete"
              @click="$emit('delete-conversation', conversation.id)"
            >
              <Trash2 size="14" />
              删除
            </button>
          </div>
        </div>
      </div>
      
      <!-- 新会话模式提示 -->
      <div v-if="isNewConversationMode" class="new-conversation-hint">
        <MessageCircle size="32" />
        <p>准备开始新对话</p>
        <p>发送消息开始聊天</p>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="conversations.length === 0" class="empty-state">
        <MessageCircle size="48" />
        <p>暂无对话记录</p>
        <p>开始您的第一次对话吧！</p>
      </div>
    </div>
    
    <!-- 重命名模态框 -->
    <div v-if="showRenameModal" class="modal-overlay" @click="cancelRename">
      <div class="modal-content" @click.stop>
        <h3>重命名对话</h3>
        <input 
          ref="renameInput"
          v-model="newTitle"
          type="text"
          class="rename-input"
          placeholder="请输入新的标题"
          @keyup.enter="confirmRename"
          @keyup.escape="cancelRename"
        />
        <div class="modal-actions">
          <button class="cancel-btn" @click="cancelRename">取消</button>
          <button class="confirm-btn" @click="confirmRename" :disabled="!newTitle.trim()">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { Plus, Trash2, MessageCircle, MoreVertical, Edit3 } from 'lucide-vue-next'
import { formatRelativeTime } from '../utils/date'

export default {
  name: 'Sidebar',
  components: {
    Plus,
    Trash2,
    MessageCircle,
    MoreVertical,
    Edit3
  },
  props: {
    conversations: {
      type: Array,
      default: () => []
    },
    currentConversationId: {
      type: [Number, String],
      default: null
    },
    isNewConversationMode: {
      type: Boolean,
      default: false
    }
  },
  emits: ['select-conversation', 'new-conversation', 'delete-conversation', 'rename-conversation'],
  setup(props, { emit }) {
    const activeMenuId = ref(null)
    const showRenameModal = ref(false)
    const renamingConversation = ref(null)
    const newTitle = ref('')
    const renameInput = ref(null)
    
    // 切换操作菜单
    const toggleActionMenu = (conversationId) => {
      if (activeMenuId.value === conversationId) {
        activeMenuId.value = null
      } else {
        activeMenuId.value = conversationId
      }
    }
    
    // 开始重命名
    const startRename = (conversation) => {
      renamingConversation.value = conversation
      newTitle.value = conversation.title || '新对话'
      showRenameModal.value = true
      activeMenuId.value = null
      
      nextTick(() => {
        if (renameInput.value) {
          renameInput.value.focus()
          renameInput.value.select()
        }
      })
    }
    
    // 确认重命名
    const confirmRename = () => {
      if (!newTitle.value.trim()) return
      
      emit('rename-conversation', renamingConversation.value.id, newTitle.value.trim())
      cancelRename()
    }
    
    // 取消重命名
    const cancelRename = () => {
      showRenameModal.value = false
      renamingConversation.value = null
      newTitle.value = ''
    }
    
    // 点击外部关闭菜单
    const handleClickOutside = (event) => {
      if (!event.target.closest('.conversation-actions')) {
        activeMenuId.value = null
      }
    }
    
    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })
    
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })
    
    return {
      activeMenuId,
      showRenameModal,
      newTitle,
      renameInput,
      toggleActionMenu,
      startRename,
      confirmRename,
      cancelRename,
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
  position: relative;
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

.conversation-actions {
  position: relative;
}

.action-btn {
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

.conversation-item:hover .action-btn {
  opacity: 1;
}

.action-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
  color: var(--text-color);
}

.conversation-item.active .action-btn {
  color: rgba(255, 255, 255, 0.8);
}

.conversation-item.active .action-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.action-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 120px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px 12px;
  background: none;
  border: none;
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: background-color 0.2s ease;
  text-align: left;
}

.menu-item:hover {
  background-color: var(--border-color);
}

.menu-item.delete {
  color: #ef4444;
}

.menu-item.delete:hover {
  background-color: rgba(239, 68, 68, 0.1);
}

.new-conversation-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: var(--primary-color);
  background-color: rgba(16, 163, 127, 0.05);
  border-radius: 8px;
  margin: 8px 0;
}

.new-conversation-hint svg {
  margin-bottom: 12px;
  opacity: 0.8;
}

.new-conversation-hint p {
  margin: 2px 0;
  font-size: 14px;
}

.new-conversation-hint p:first-of-type {
  font-weight: 500;
  color: var(--primary-color);
}

.new-conversation-hint p:last-of-type {
  font-size: 12px;
  color: var(--text-secondary);
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

/* 重命名模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background-color: var(--bg-color);
  border-radius: 12px;
  padding: 24px;
  min-width: 320px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.rename-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-color);
  background-color: var(--bg-secondary);
  margin-bottom: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}

.rename-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(16, 163, 127, 0.1);
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.cancel-btn,
.confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-btn {
  background-color: var(--bg-secondary);
  color: var(--text-color);
}

.cancel-btn:hover {
  background-color: var(--border-color);
}

.confirm-btn {
  background-color: var(--primary-color);
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background-color: var(--primary-hover);
}

.confirm-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  
  .modal-content {
    margin: 16px;
    min-width: auto;
  }
}
</style> 