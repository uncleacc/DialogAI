# DialogAI 数据库自动初始化功能使用指南

## ? 功能概述

DialogAI项目现已完成数据库自动初始化功能的开发，实现了"开箱即用"的部署体验！

### ? 核心特性

- **? 自动创建数据库**：应用启动时自动创建数据库（如果不存在）
- **? 自动创建表结构**：使用Hibernate DDL自动创建所有必需的表
- **? 自动初始化数据**：插入系统配置和欢迎数据
- **? 健康检查接口**：实时监控数据库和业务状态
- **? 多环境支持**：开发、测试、生产环境的不同策略
- **?? 配置管理**：动态系统配置管理和缓存
- **? 监控面板**：详细的系统状态和数据统计

## ? 快速开始

### 1. 环境准备

确保您的系统已安装：
- Java 8+
- MySQL 8.0+（开发环境）
- Maven 3.6+

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dialog_ai?createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 3. 启动应用

**方式一：使用启动脚本（推荐）**
```bash
cd DialogAI-server
./start-with-db-init.sh dev 8080
```

**方式二：使用Maven**
```bash
cd DialogAI-server
mvn spring-boot:run -Dspring.profiles.active=dev
```

**方式三：使用JAR包**
```bash
cd DialogAI-server
mvn clean package -DskipTests
java -jar target/dialog-ai-backend-1.0.0.jar --spring.profiles.active=dev
```

### 4. 验证启动

应用启动后会看到类似日志：
```
[INFO] 开始数据库初始化检查...
[INFO] 数据库连接成功！
[INFO] 表 'conversations' 已存在
[INFO] 表 'messages' 已存在
[INFO] 数据库数据初始化完成！
```

访问健康检查接口验证：
```bash
curl http://localhost:8080/api/health
```

## ? API接口

### 健康检查接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/health` | GET | 系统整体健康检查 |
| `/api/health/database` | GET | 数据库专项检查 |
| `/api/health/business` | GET | 业务数据检查 |

### 系统配置接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/config/public` | GET | 获取所有公开配置 |
| `/api/config/core` | GET | 获取核心系统配置 |
| `/api/config/chat` | GET | 获取聊天相关配置 |
| `/api/config/ui` | GET | 获取UI相关配置 |
| `/api/config/features` | GET | 获取功能开关配置 |
| `/api/config/overview` | GET | 获取配置概览信息 |
| `/api/config/refresh` | POST | 刷新配置缓存 |

## ?? 数据库结构

### 核心表

1. **conversations** - 会话表
   - 存储用户对话会话信息
   - 支持状态管理（活跃、归档、删除）

2. **messages** - 消息表
   - 存储对话消息内容
   - 区分用户消息和AI回复
   - 支持消息状态跟踪

### 扩展表

3. **users** - 用户表（预留）
4. **conversation_shares** - 会话分享表（预留）
5. **system_configs** - 系统配置表

## ?? 配置管理

### 系统默认配置

| 配置键 | 默认值 | 说明 |
|--------|--------|------|
| `system.version` | 1.0.0 | 系统版本号 |
| `system.name` | DialogAI | 系统名称 |
| `chat.max_message_length` | 2000 | 单条消息最大长度 |
| `ai.default_model` | deepseek-chat | AI默认模型 |
| `ui.theme` | auto | 默认主题 |
| `feature.conversation_share` | true | 会话分享功能开关 |

### 动态配置管理

通过 `SystemConfigService` 可以动态读取和修改配置：

```java
@Autowired
private SystemConfigService configService;

// 获取配置
String theme = configService.getString("ui.theme", "auto");
Integer maxLength = configService.getInteger("chat.max_message_length", 2000);
Boolean shareEnabled = configService.getBoolean("feature.conversation_share", true);

// 设置配置
configService.setString("ui.theme", "dark", "用户界面主题", true);
```

## ? 多环境配置

### 开发环境 (dev)
- DDL模式：`create-drop` - 每次启动重新创建表
- 显示SQL：启用
- 数据初始化：启用

```bash
./start-with-db-init.sh dev 8080
```

### 测试环境 (test)
- 数据库：H2内存数据库
- DDL模式：`create-drop`
- H2控制台：启用

```bash
java -jar app.jar --spring.profiles.active=test
```

### 生产环境 (prod)
- DDL模式：`validate` - 仅验证表结构
- 显示SQL：禁用
- 数据初始化：禁用

```bash
# 设置环境变量
export DB_HOST=prod-mysql
export DB_PASSWORD=secure_password
export DEEPSEEK_API_KEY=your_api_key

./start-with-db-init.sh prod 8080
```

## ? 自定义配置

### 环境变量

支持通过环境变量自定义配置：

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=dialog_ai
export DB_USERNAME=root
export DB_PASSWORD=your_password
export DEEPSEEK_API_KEY=your_api_key
```

### 启动参数

```bash
java -jar app.jar \
  --spring.profiles.active=prod \
  --server.port=9090 \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/dialogai
```

## ?? 故障排除

### 常见问题

1. **数据库连接失败**
   ```
   解决方案：
   - 检查MySQL服务是否启动
   - 验证用户名密码
   - 确认网络连接
   ```

2. **权限不足**
   ```
   解决方案：
   - 使用有创建数据库权限的用户
   - 授予必要的数据库权限
   ```

3. **表已存在错误**
   ```
   解决方案：
   - 生产环境使用 validate 模式
   - 开发环境可删除数据库重新创建
   ```

### 日志调试

开启详细日志：
```yaml
logging:
  level:
    com.dialogai: DEBUG
    org.springframework.jdbc: DEBUG
```

## ? 监控和运维

### 健康检查示例

```bash
# 系统健康检查
curl http://localhost:8080/api/health
```

响应示例：
```json
{
  "status": "UP",
  "timestamp": "2024-01-20 15:30:00",
  "application": "DialogAI",
  "version": "1.0.0",
  "database": {
    "healthy": true,
    "product": "MySQL",
    "version": "8.0.33"
  },
  "business": {
    "healthy": true,
    "conversations": {
      "total": 1,
      "active": 1,
      "todayNew": 0
    }
  }
}
```

### 配置监控

```bash
# 获取配置概览
curl http://localhost:8080/api/config/overview
```

## ? 最佳实践

1. **开发环境**
   - 使用 `create-drop` 模式快速迭代
   - 启用SQL日志便于调试
   - 使用内存数据库进行单元测试

2. **生产环境**
   - 使用 `validate` 模式保护数据
   - 关闭SQL日志提升性能
   - 定期备份数据库
   - 监控健康检查接口

3. **配置管理**
   - 敏感配置使用环境变量
   - 利用配置缓存提升性能
   - 定期刷新配置缓存

## ? 相关文档

- [数据库设置详细说明](DATABASE_SETUP.md)
- [AI友好配置规范](../AI_FRIENDLY_STANDARDS.md)
- [项目API文档](doc/接口文档.md)

## ? 总结

通过本自动化数据库初始化功能，DialogAI项目实现了：

? **零配置启动** - 无需手动创建数据库和表  
? **多环境支持** - 开发、测试、生产环境无缝切换  
? **健康监控** - 实时监控数据库和业务状态  
? **配置管理** - 动态配置管理和缓存机制  
? **故障诊断** - 详细的错误信息和故障排除指南  

这大大简化了项目的部署和维护工作，让开发者可以专注于业务功能的开发！ 