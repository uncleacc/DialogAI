<template>
  <button class="theme-toggle" @click="toggleTheme" :title="toggleTitle">
    <Sun v-if="currentTheme === 'dark'" size="18" />
    <Moon v-else size="18" />
  </button>
</template>

<script>
import { computed } from 'vue'
import { Sun, Moon } from 'lucide-vue-next'
import { useThemeStore } from '../stores/theme'

export default {
  name: 'ThemeToggle',
  components: {
    Sun,
    Moon
  },
  setup() {
    const themeStore = useThemeStore()
    
    const currentTheme = computed(() => themeStore.currentTheme)
    const toggleTitle = computed(() => 
      currentTheme.value === 'light' ? '切换到深色模式' : '切换到浅色模式'
    )
    
    const toggleTheme = () => {
      themeStore.toggleTheme()
    }
    
    return {
      currentTheme,
      toggleTitle,
      toggleTheme
    }
  }
}
</script>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.theme-toggle:hover {
  background-color: var(--border-color);
  transform: scale(1.05);
}

.theme-toggle:active {
  transform: scale(0.95);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .theme-toggle {
    width: 32px;
    height: 32px;
  }
}
</style> 