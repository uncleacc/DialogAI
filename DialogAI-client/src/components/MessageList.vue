<template>
  <div class="message-list" ref="messageListRef">
    <!-- 欢迎消息 -->
    <div v-if="messages.length === 0 && !isLoading" class="welcome-message">
      <div class="welcome-content">
        <div class="welcome-icon">
          <Bot size="48" />
        </div>
        <h2>欢迎使用 DialogAI</h2>
        <p>我是您的智能对话助手，可以帮您解答问题、编写代码、分析数据等。</p>
        <div class="welcome-suggestions">
          <div class="suggestion-title">您可以尝试问我：</div>
          <div class="suggestion-items">
            <button 
              v-for="suggestion in suggestions" 
              :key="suggestion"
              class="suggestion-item"
              @click="$emit('send-suggestion', suggestion)"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 消息列表 -->
    <div v-else class="messages-container">
      <div 
        v-for="message in messages" 
        :key="message.id"
        class="message-wrapper"
        :class="{ 'fade-in': true }"
      >
        <MessageItem 
          :message="message"
          @regenerate="$emit('regenerate', message.id)"
        />
        <!-- 调试信息 -->
        <!-- <div v-if="message.role === 'ASSISTANT'" class="debug-info" style="font-size: 12px; color: #666; margin-top: 4px;">
          消息ID: {{ message.id }}, 内容长度: {{ message.content.length }}
        </div> -->
      </div>
      
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-message">
        <div class="loading-indicator">
          <div class="loading-dots">
            <div class="dot"></div>
            <div class="dot"></div>
            <div class="dot"></div>
          </div>
          <span class="loading-text">AI正在思考中...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch, nextTick } from 'vue'
import { Bot } from 'lucide-vue-next'
import MessageItem from './MessageItem.vue'

export default {
  name: 'MessageList',
  components: {
    Bot,
    MessageItem
  },
  props: {
    messages: {
      type: Array,
      default: () => []
    },
    isLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['regenerate', 'send-suggestion'],
  setup(props) {
    const messageListRef = ref(null)
    
    // 欢迎建议
    const suggestions = [
      '请介绍一下你自己',
      '帮我写一个Python函数',
      '解释一下什么是机器学习',
      '帮我分析这段代码',
      '写一个简单的网页'
    ]
    
    // 自动滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messageListRef.value) {
          messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
      })
    }
    
    // 监听消息变化，自动滚动
    watch(() => props.messages.length, scrollToBottom)
    watch(() => props.isLoading, scrollToBottom)
    
    // 监听消息内容变化，确保流式更新时能实时显示
    watch(() => props.messages, () => {
      console.log('[MessageList] 消息列表变化，触发滚动');
      scrollToBottom()
    }, { deep: true })
    
    return {
      messageListRef,
      suggestions
    }
  }
}
</script>

<style scoped>
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  background-color: var(--bg-color);
}

.welcome-message {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px 20px;
}

.welcome-content {
  max-width: 600px;
  text-align: center;
}

.welcome-icon {
  margin-bottom: 24px;
  color: var(--primary-color);
}

.welcome-content h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-color);
}

.welcome-content p {
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin-bottom: 32px;
}

.welcome-suggestions {
  text-align: left;
}

.suggestion-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
  margin-bottom: 12px;
}

.suggestion-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.suggestion-item {
  padding: 12px 16px;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  text-align: left;
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.suggestion-item:hover {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.messages-container {
  padding: 0 20px;
}

.message-wrapper {
  margin-bottom: 24px;
}

.loading-message {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 24px;
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background-color: var(--bg-secondary);
  border-radius: 12px;
  max-width: 80%;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: var(--text-secondary);
  border-radius: 50%;
  animation: loading-dot 1.4s infinite ease-in-out;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading-dot {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-list {
    padding: 16px 0;
  }
  
  .welcome-content h2 {
    font-size: 24px;
  }
  
  .welcome-content p {
    font-size: 14px;
  }
  
  .messages-container {
    padding: 0 16px;
  }
  
  .loading-indicator {
    max-width: 90%;
  }
}
</style> 