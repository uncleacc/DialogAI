import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

// 配置marked
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (err) {
        console.error('代码高亮错误:', err)
      }
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true,
  gfm: true
})

// 自定义渲染器
const renderer = new marked.Renderer()

// 自定义代码块渲染
renderer.code = (code, language) => {
  const validLanguage = hljs.getLanguage(language) ? language : 'plaintext'
  const highlighted = hljs.highlight(code, { language: validLanguage }).value
  
  return `
    <div class="code-block">
      <div class="code-header">
        <span class="language-label">${validLanguage}</span>
        <button class="copy-btn" onclick="copyCode(this)" data-code="${encodeURIComponent(code)}">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
          </svg>
        </button>
      </div>
      <pre><code class="hljs ${validLanguage}">${highlighted}</code></pre>
    </div>
  `
}

// 自定义表格渲染
renderer.table = (header, body) => {
  return `
    <div class="table-container">
      <table>
        <thead>${header}</thead>
        <tbody>${body}</tbody>
      </table>
    </div>
  `
}

marked.use({ renderer })

// 复制代码功能
window.copyCode = function(button) {
  const code = decodeURIComponent(button.getAttribute('data-code'))
  navigator.clipboard.writeText(code).then(() => {
    // 显示复制成功提示
    const originalText = button.innerHTML
    button.innerHTML = `
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="20,6 9,17 4,12"></polyline>
      </svg>
    `
    button.style.color = '#10a37f'
    
    setTimeout(() => {
      button.innerHTML = originalText
      button.style.color = ''
    }, 2000)
  }).catch(err => {
    console.error('复制失败:', err)
  })
}

export const renderMarkdown = (text) => {
  try {
    return marked(text)
  } catch (error) {
    console.error('Markdown渲染错误:', error)
    return text
  }
} 