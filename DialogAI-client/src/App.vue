<template>
  <div id="app" :data-theme="theme">
    <router-view />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useThemeStore } from './stores/theme'

export default {
  name: 'App',
  setup() {
    const themeStore = useThemeStore()
    const theme = computed(() => themeStore.currentTheme)
    
    return {
      theme
    }
  }
}
</script>

<style scoped>
#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}
</style> 