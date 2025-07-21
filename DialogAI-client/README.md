# DialogAI 前端客户端

基于 Vue 3 + Vite 构建的现代化 AI 对话界面，提供类似 ChatGPT 的用户体验。

## 功能特性

### 🎨 界面设计
- **现代化设计**: 简洁专业的界面风格，专注于对话体验
- **深色/浅色主题**: 支持主题切换，适应不同使用环境
- **响应式布局**: 完美适配桌面端和移动端设备
- **流畅动画**: 丰富的交互动画，提升用户体验

### 💬 对话功能
- **实时对话**: 与 AI 进行流畅的实时对话
- **消息气泡**: 用户消息和 AI 回复采用气泡式设计
- **Markdown 渲染**: 支持代码高亮、表格、列表等格式
- **代码复制**: 一键复制代码块内容
- **重新生成**: 支持重新生成 AI 回复

### 📱 用户体验
- **固定输入框**: 输入区域固定在页面底部，随时可用
- **自动滚动**: 新消息自动滚动到可见区域
- **键盘快捷键**: Enter 发送，Shift+Enter 换行
- **加载动画**: AI 思考时显示友好的加载动画
- **错误处理**: 网络异常时显示友好提示

### 🔧 技术特性
- **Vue 3 Composition API**: 现代化的响应式开发
- **Pinia 状态管理**: 高效的状态管理方案
- **Vite 构建工具**: 快速的开发体验
- **TypeScript 支持**: 完整的类型支持
- **ESLint + Prettier**: 代码质量和格式化

## 技术栈

- **框架**: Vue 3.3.4
- **构建工具**: Vite 4.4.5
- **状态管理**: Pinia 2.1.6
- **路由**: Vue Router 4.2.4
- **HTTP 客户端**: Axios 1.4.0
- **Markdown 渲染**: Marked 5.1.1
- **代码高亮**: Highlight.js 11.9.0
- **图标**: Lucide Vue Next 0.263.1
- **日期处理**: Day.js 1.11.9

## 快速开始

### 环境要求
- Node.js 16+
- npm 或 yarn

### 安装依赖
```bash
cd DialogAI-client
npm install
```

### 开发模式
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览构建结果
```bash
npm run preview
```

## 项目结构

```
src/
├── api/                    # API 接口
│   └── chat.js            # 聊天相关 API
├── components/             # Vue 组件
│   ├── MessageInput.vue   # 消息输入组件
│   ├── MessageList.vue    # 消息列表组件
│   ├── MessageItem.vue    # 单个消息组件
│   ├── Sidebar.vue        # 侧边栏组件
│   └── ThemeToggle.vue    # 主题切换组件
├── stores/                 # Pinia 状态管理
│   ├── chat.js            # 聊天状态
│   └── theme.js           # 主题状态
├── utils/                  # 工具函数
│   ├── markdown.js        # Markdown 渲染
│   └── date.js            # 日期处理
├── views/                  # 页面组件
│   └── ChatView.vue       # 聊天主页面
├── router/                 # 路由配置
│   └── index.js
├── App.vue                 # 根组件
├── main.js                 # 应用入口
└── style.css               # 全局样式
```

## 核心组件

### ChatView.vue
主聊天页面，包含侧边栏、消息列表和输入区域。

### MessageInput.vue
消息输入组件，支持多行输入、自动调整高度、键盘快捷键。

### MessageItem.vue
单个消息组件，支持 Markdown 渲染、代码高亮、复制功能。

### Sidebar.vue
侧边栏组件，显示会话列表，支持新建、选择、删除会话。

## API 集成

项目通过 `src/api/chat.js` 与后端 API 进行通信：

- **发送消息**: `POST /api/chat/send`
- **获取会话**: `GET /api/chat/conversations`
- **会话详情**: `GET /api/chat/conversations/{id}`
- **创建会话**: `POST /api/chat/conversations`
- **删除会话**: `DELETE /api/chat/conversations/{id}`

## 主题系统

支持浅色和深色主题切换：

```css
:root {
  --primary-color: #10a37f;
  --bg-color: #ffffff;
  --text-color: #374151;
  /* 其他变量... */
}

[data-theme="dark"] {
  --bg-color: #1a1a1a;
  --text-color: #e5e7eb;
  /* 深色主题变量... */
}
```

## 开发指南

### 添加新组件
1. 在 `src/components/` 创建组件文件
2. 使用 Composition API 编写逻辑
3. 添加响应式样式
4. 在需要的地方导入使用

### 状态管理
使用 Pinia 进行状态管理：
- `useChatStore()`: 管理聊天相关状态
- `useThemeStore()`: 管理主题状态

### 样式规范
- 使用 CSS 变量实现主题切换
- 采用 BEM 命名规范
- 响应式设计优先考虑移动端

## 部署

### 构建
```bash
npm run build
```

### 部署到服务器
将 `dist` 目录部署到 Web 服务器即可。

### Docker 部署
```dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License 