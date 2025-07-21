<template>
  <div class="message-input-container">
    <div class="input-wrapper">
      <div class="input-area">
        <textarea
          ref="textareaRef"
          v-model="message"
          class="message-textarea"
          :placeholder="placeholder"
          :disabled="isLoading"
          @keydown="handleKeydown"
          @input="autoResize"
          rows="1"
        ></textarea>
        
        <div class="input-actions">
          <button 
            class="send-btn"
            :class="{ 'loading': isLoading }"
            :disabled="!canSend || isLoading"
            @click="sendMessage"
          >
            <Send v-if="!isLoading" size="16" />
            <div v-else class="spinner"></div>
          </button>
        </div>
      </div>
      
      <div class="input-footer">
        <span class="input-hint">
          {{ isLoading ? 'AI正在思考中...' : '按 Enter 发送，Shift + Enter 换行' }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, nextTick, watch } from 'vue'
import { Send } from 'lucide-vue-next'

export default {
  name: 'MessageInput',
  components: {
    Send
  },
  props: {
    isLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['send-message'],
  setup(props, { emit }) {
    const textareaRef = ref(null)
    const message = ref('')
    
    const canSend = computed(() => {
      return message.value.trim().length > 0 && !props.isLoading
    })
    
    const placeholder = computed(() => {
      return props.isLoading ? '请稍候...' : '输入您的消息...'
    })
    
    const sendMessage = () => {
      if (!canSend.value) return
      
      const content = message.value.trim()
      if (content) {
        emit('send-message', content)
        message.value = ''
        autoResize()
      }
    }
    
    const handleKeydown = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
    }
    
    const autoResize = () => {
      nextTick(() => {
        if (textareaRef.value) {
          textareaRef.value.style.height = 'auto'
          textareaRef.value.style.height = Math.min(textareaRef.value.scrollHeight, 120) + 'px'
        }
      })
    }
    
    // 监听加载状态变化，重置输入框
    watch(() => props.isLoading, (newVal, oldVal) => {
      if (oldVal && !newVal) {
        // 加载完成后，聚焦输入框
        nextTick(() => {
          if (textareaRef.value) {
            textareaRef.value.focus()
          }
        })
      }
    })
    
    return {
      textareaRef,
      message,
      canSend,
      placeholder,
      sendMessage,
      handleKeydown,
      autoResize
    }
  }
}
</script>

<style scoped>
.message-input-container {
  border-top: 1px solid var(--border-color);
  background-color: var(--bg-color);
  padding: 16px 24px;
  position: relative;
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 12px 16px;
  transition: border-color 0.2s ease;
}

.input-area:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(16, 163, 127, 0.1);
}

.message-textarea {
  flex: 1;
  border: none;
  background: none;
  resize: none;
  outline: none;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-color);
  min-height: 20px;
  max-height: 120px;
  overflow-y: auto;
}

.message-textarea::placeholder {
  color: var(--text-secondary);
}

.message-textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.input-actions {
  display: flex;
  align-items: center;
}

.send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  background-color: var(--primary-hover);
  transform: scale(1.05);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.send-btn.loading {
  background-color: var(--text-secondary);
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.input-footer {
  margin-top: 8px;
  text-align: center;
}

.input-hint {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-input-container {
    padding: 12px 16px;
  }
  
  .input-area {
    padding: 10px 12px;
  }
  
  .message-textarea {
    font-size: 13px;
  }
  
  .send-btn {
    width: 28px;
    height: 28px;
  }
  
  .input-hint {
    font-size: 11px;
  }
}
</style> 