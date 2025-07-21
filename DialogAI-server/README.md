# DialogAI - AI在线对话系统

## 项目简介

DialogAI是一个基于Spring Boot的AI在线对话系统后端，支持与DeepSeek API进行智能对话，具备完整的会话管理功能。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **AI服务**: DeepSeek API
- **构建工具**: Maven
- **Java版本**: 17

## 核心功能

### 1. AI对话功能
- 支持与DeepSeek API进行智能对话
- 支持连续对话，保持上下文
- 自动保存对话历史

### 2. 会话管理
- 会话的增删改查
- 会话归档和删除
- 会话搜索功能
- 分页查询支持

### 3. 消息管理
- 消息的自动保存
- 消息历史查询
- 消息状态管理

### 4. 系统特性
- 缓存支持（Redis）
- 参数验证
- 全局异常处理
- 日志记录
- 事务管理

## 项目结构

```
src/main/java/com/dialogai/
├── DialogAiApplication.java          # 主启动类
├── config/                          # 配置类
│   ├── DeepSeekConfig.java         # DeepSeek API配置
│   └── RedisConfig.java            # Redis配置
├── constant/                        # 常量类
│   └── Constants.java              # 系统常量
├── controller/                      # 控制器层
│   └── ChatController.java         # 聊天控制器
├── dto/                            # 数据传输对象
│   ├── ChatRequestDto.java         # 聊天请求DTO
│   ├── ChatResponseDto.java        # 聊天响应DTO
│   ├── ConversationDto.java        # 会话DTO
│   └── MessageDto.java             # 消息DTO
├── entity/                         # 实体类
│   ├── Conversation.java           # 会话实体
│   └── Message.java                # 消息实体
├── exception/                      # 异常处理
│   └── GlobalExceptionHandler.java # 全局异常处理器
├── repository/                     # 数据访问层
│   ├── ConversationRepository.java # 会话Repository
│   └── MessageRepository.java      # 消息Repository
└── service/                        # 服务层
    ├── ChatService.java            # 聊天服务
    ├── ConversationService.java    # 会话服务
    ├── DeepSeekService.java       # DeepSeek API服务
    └── MessageService.java         # 消息服务
```

## 数据库设计

### 会话表 (conversations)
- `id`: 主键
- `title`: 会话标题
- `description`: 会话描述
- `user_id`: 用户ID
- `status`: 会话状态 (ACTIVE/ARCHIVED/DELETED)
- `created_at`: 创建时间
- `updated_at`: 更新时间

### 消息表 (messages)
- `id`: 主键
- `content`: 消息内容
- `role`: 消息角色 (USER/ASSISTANT)
- `status`: 消息状态 (SENDING/SENT/FAILED)
- `sequence_number`: 消息序号
- `conversation_id`: 会话ID
- `created_at`: 创建时间

## API接口

### 聊天相关
- `POST /api/chat/send` - 发送聊天消息
- `GET /api/chat/conversations` - 获取会话列表
- `GET /api/chat/conversations/page` - 分页获取会话列表
- `GET /api/chat/conversations/{id}` - 获取会话详情
- `POST /api/chat/conversations` - 创建新会话
- `PUT /api/chat/conversations/{id}/title` - 更新会话标题
- `PUT /api/chat/conversations/{id}/archive` - 归档会话
- `DELETE /api/chat/conversations/{id}` - 删除会话
- `GET /api/chat/conversations/search` - 搜索会话
- `DELETE /api/chat/conversations/{id}/messages` - 清空会话消息

## 配置说明

### 数据库配置
在 `application.yml` 中配置MySQL连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dialog_ai
    username: your_username
    password: your_password
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

### DeepSeek API配置
```yaml
deepseek:
  api:
    base-url: https://api.deepseek.com
    api-key: your-deepseek-api-key
    model: deepseek-chat
    timeout: 30000
```

## 快速开始

### 1. 环境准备
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库初始化
```sql
CREATE DATABASE dialog_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置修改
修改 `src/main/resources/application.yml` 中的数据库和Redis配置。

### 4. 启动应用
```bash
mvn spring-boot:run
```

### 5. 测试API
```bash
# 发送聊天消息
curl -X POST http://localhost:8080/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好",
    "userId": "test-user"
  }'
```

## 扩展性设计

### 1. 模块化设计
- 服务层职责分离
- 数据访问层独立
- 配置类可扩展

### 2. 缓存策略
- Redis缓存支持
- 缓存注解管理
- 可配置的缓存策略

### 3. 异常处理
- 全局异常处理器
- 统一的错误响应格式
- 详细的日志记录

### 4. 数据库设计
- 软删除支持
- 审计字段
- 索引优化

## 注意事项

1. **API密钥安全**: 请妥善保管DeepSeek API密钥，不要提交到代码仓库
2. **数据库备份**: 定期备份数据库数据
3. **性能监控**: 建议添加性能监控和告警
4. **安全考虑**: 生产环境建议添加认证和授权机制

## 开发计划

- [ ] 用户认证和授权
- [ ] WebSocket实时通信
- [ ] 文件上传功能
- [ ] 多AI模型支持
- [ ] 对话导出功能
- [ ] 性能优化
- [ ] 单元测试覆盖
- [ ] Docker部署支持

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

MIT License 