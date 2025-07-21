import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref(localStorage.getItem('theme') || 'light')
  
  const toggleTheme = () => {
    currentTheme.value = currentTheme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('theme', currentTheme.value)
  }
  
  const setTheme = (theme) => {
    currentTheme.value = theme
    localStorage.setItem('theme', theme)
  }
  
  return {
    currentTheme,
    toggleTheme,
    setTheme
  }
}) 