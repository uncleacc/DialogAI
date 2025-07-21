<template>
  <div class="message-item" :class="{ 'user-message': isUser, 'ai-message': !isUser }">
    <div class="message-avatar">
      <div v-if="isUser" class="user-avatar">
        <User size="16" />
      </div>
      <div v-else class="ai-avatar">
        <Bot size="16" />
      </div>
    </div>
    
    <div class="message-content">
      <div class="message-bubble">
        <div class="message-text" v-html="renderedContent"></div>
        
        <!-- 消息操作按钮 -->
        <div class="message-actions" v-if="!isUser">
          <button 
            class="action-btn"
            @click="copyMessage"
            title="复制回复"
          >
            <Copy size="14" />
          </button>
          <button 
            class="action-btn"
            @click="$emit('regenerate')"
            title="重新生成"
          >
            <RotateCcw size="14" />
          </button>
        </div>
      </div>
      
      <div class="message-time">
        {{ formatRelativeTime(message.createdAt) }}
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { User, Bot, Copy, RotateCcw } from 'lucide-vue-next'
import { renderMarkdown } from '../utils/markdown'
import { formatRelativeTime } from '../utils/date'

export default {
  name: 'MessageItem',
  components: {
    User,
    Bot,
    Copy,
    RotateCcw
  },
  props: {
    message: {
      type: Object,
      required: true
    }
  },
  emits: ['regenerate'],
  setup(props) {
    const isUser = computed(() => props.message.role === 'USER')
    
    const renderedContent = computed(() => {
      if (isUser.value) {
        return props.message.content
      } else {
        return renderMarkdown(props.message.content)
      }
    })
    
    const copyMessage = async () => {
      try {
        await navigator.clipboard.writeText(props.message.content)
        // 可以添加复制成功的提示
      } catch (err) {
        console.error('复制失败:', err)
      }
    }
    
    return {
      isUser,
      renderedContent,
      formatRelativeTime,
      copyMessage
    }
  }
}
</script>

<style scoped>
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.message-avatar {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar {
  background-color: var(--primary-color);
  color: white;
}

.ai-avatar {
  background-color: var(--bg-secondary);
  color: var(--text-color);
}

.message-content {
  flex: 1;
  max-width: calc(100% - 44px);
}

.message-bubble {
  position: relative;
  padding: 12px 16px;
  border-radius: 12px;
  max-width: 80%;
  word-wrap: break-word;
}

.user-message .message-bubble {
  background-color: var(--primary-color);
  color: white;
  margin-left: auto;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background-color: var(--bg-secondary);
  color: var(--text-color);
  border-bottom-left-radius: 4px;
}

.message-text {
  line-height: 1.6;
  font-size: 14px;
}

.message-text :deep(p) {
  margin: 0 0 12px 0;
}

.message-text :deep(p:last-child) {
  margin-bottom: 0;
}

.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4),
.message-text :deep(h5),
.message-text :deep(h6) {
  margin: 16px 0 8px 0;
  font-weight: 600;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.message-text :deep(li) {
  margin: 4px 0;
}

.message-text :deep(blockquote) {
  margin: 8px 0;
  padding: 8px 12px;
  border-left: 4px solid var(--primary-color);
  background-color: rgba(16, 163, 127, 0.1);
  border-radius: 4px;
}

.message-text :deep(code) {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}

.message-text :deep(pre) {
  margin: 12px 0;
  border-radius: 8px;
  overflow-x: auto;
}

.message-text :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
}

.message-text :deep(th),
.message-text :deep(td) {
  border: 1px solid var(--border-color);
  padding: 8px 12px;
  text-align: left;
}

.message-text :deep(th) {
  background-color: var(--bg-secondary);
  font-weight: 600;
}

.message-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.message-bubble:hover .message-actions {
  opacity: 1;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 4px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background-color: white;
  transform: scale(1.05);
}

.message-time {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  text-align: right;
}

.user-message .message-time {
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

/* 代码块样式 */
.message-text :deep(.code-block) {
  position: relative;
  margin: 12px 0;
}

.message-text :deep(.code-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: rgba(0, 0, 0, 0.1);
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.message-text :deep(.language-label) {
  color: var(--text-secondary);
}

.message-text :deep(.copy-btn) {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 2px;
  border-radius: 2px;
  transition: color 0.2s ease;
}

.message-text :deep(.copy-btn:hover) {
  color: var(--primary-color);
}

.message-text :deep(.table-container) {
  overflow-x: auto;
  margin: 12px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-item {
    gap: 8px;
  }
  
  .message-avatar {
    width: 28px;
    height: 28px;
  }
  
  .message-bubble {
    max-width: 90%;
    padding: 10px 12px;
  }
  
  .message-text {
    font-size: 13px;
  }
  
  .message-actions {
    opacity: 1;
  }
}
</style> 