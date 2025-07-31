# DialogAI 项目 AI友好配置规范文档

## 项目概述

DialogAI是一个智能对话系统，采用前后端分离架构：
- **前端**: Vue.js + Vite，提供用户交互界面
- **后端**: Spring Boot + Maven，提供API服务和业务逻辑
- **核心功能**: 智能对话、消息管理、主题切换、实时通信

## 1. 项目目录结构

```
DialogAI/
├── DialogAI-client/                 # 前端应用
│   ├── src/
│   │   ├── api/                     # API接口封装
│   │   │   └── chat.js              # 聊天相关API
│   │   ├── components/              # Vue组件
│   │   │   ├── MessageInput.vue     # 消息输入组件
│   │   │   ├── MessageItem.vue      # 消息项组件  
│   │   │   ├── MessageList.vue      # 消息列表组件
│   │   │   ├── Sidebar.vue          # 侧边栏组件
│   │   │   └── ThemeToggle.vue      # 主题切换组件
│   │   ├── router/                  # 路由配置
│   │   │   └── index.js             # 路由定义
│   │   ├── stores/                  # 状态管理
│   │   │   ├── chat.js              # 聊天状态管理
│   │   │   └── theme.js             # 主题状态管理
│   │   ├── utils/                   # 工具函数
│   │   │   ├── date.js              # 日期处理工具
│   │   │   └── markdown.js          # Markdown处理工具
│   │   ├── views/                   # 页面视图
│   │   │   └── ChatView.vue         # 聊天页面
│   │   ├── App.vue                  # 根组件
│   │   ├── main.js                  # 应用入口
│   │   └── style.css                # 全局样式
│   ├── public/                      # 静态资源
│   ├── package.json                 # 前端依赖配置
│   ├── vite.config.js              # Vite构建配置
│   └── README.md                    # 前端说明文档
├── DialogAI-server/                 # 后端应用
│   ├── src/main/java/com/dialogai/
│   │   ├── config/                  # 配置类
│   │   │   ├── DeepSeekConfig.java  # AI服务配置
│   │   │   └── RedisConfig.java     # Redis配置
│   │   ├── constant/                # 常量定义
│   │   │   └── Constants.java       # 系统常量
│   │   ├── controller/              # 控制器层
│   │   │   └── ChatController.java  # 聊天控制器
│   │   ├── dto/                     # 数据传输对象
│   │   │   ├── ChatRequestDto.java  # 聊天请求DTO
│   │   │   ├── ChatResponseDto.java # 聊天响应DTO
│   │   │   ├── ConversationDto.java # 对话DTO
│   │   │   └── MessageDto.java      # 消息DTO
│   │   ├── entity/                  # 实体类
│   │   │   ├── Conversation.java    # 对话实体
│   │   │   └── Message.java         # 消息实体
│   │   ├── exception/               # 异常处理
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   ├── repository/              # 数据访问层
│   │   │   ├── ConversationRepository.java # 对话仓库
│   │   │   └── MessageRepository.java      # 消息仓库
│   │   ├── service/                 # 业务服务层
│   │   │   ├── ChatService.java     # 聊天服务
│   │   │   ├── ConversationService.java # 对话服务
│   │   │   ├── DeepSeekService.java # AI服务
│   │   │   └── MessageService.java  # 消息服务
│   │   └── DialogAiApplication.java # 应用启动类
│   ├── src/main/resources/
│   │   └── application.yml          # 应用配置
│   ├── src/test/                    # 测试代码
│   ├── pom.xml                      # Maven依赖配置
│   └── README.md                    # 后端说明文档
└── README.md                        # 项目总体说明文档
```

## 2. 整体架构原则

### 2.1 分层架构
- **表现层(Controller)**: 处理HTTP请求、参数校验、响应封装
- **业务层(Service)**: 实现核心业务逻辑、对话处理、AI交互
- **数据访问层(Repository)**: 数据持久化、消息存储、对话管理
- **实体层(Entity)**: 定义业务实体和数据模型
- **DTO层**: 数据传输对象，用于前后端交互

### 2.2 前后端分离
- 前端负责用户界面和交互逻辑
- 后端提供RESTful API服务
- 通过HTTP/WebSocket进行通信
- 实时消息通过Server-Sent Events (SSE)传输

## 3. 代码组织约束

### 3.1 包命名规范
- 统一使用`com.dialogai.{模块名}.{层次}`格式
- 模块名使用全小写，如`chat`、`conversation`、`message`
- 层次命名规范：
  - `controller`: 控制器和API接口
  - `service`: 业务服务接口和实现
  - `repository`: 数据访问接口
  - `entity`: 实体类和数据模型
  - `dto`: 数据传输对象
  - `config`: 配置类
  - `exception`: 异常类
  - `constant`: 常量定义

### 3.2 类命名规范
- Controller类以`Controller`结尾
- Service接口无前缀，实现类以`Impl`结尾
- Repository接口以`Repository`结尾
- 实体类使用名词，如`Message`、`Conversation`
- DTO类以`Dto`结尾
- 配置类以`Config`结尾
- 异常类以`Exception`结尾
- 常量类以`Constants`结尾

### 3.3 前端文件命名规范
- Vue组件使用PascalCase命名，如`MessageInput.vue`
- JavaScript文件使用camelCase命名，如`chat.js`
- 样式文件使用kebab-case命名，如`message-list.css`
- 页面视图以`View`结尾，如`ChatView.vue`

## 4. 依赖管理约束

### 4.1 后端依赖管理
- 统一在pom.xml中管理版本号
- 使用Spring Boot父依赖统一版本
- 核心依赖框架：
  - Spring Boot 2.7+
  - Spring Data JPA
  - Spring Web
  - MySQL/H2 数据库
  - Redis (可选)
  - WebSocket/SSE支持

### 4.2 前端依赖管理
- 统一在package.json中管理版本号
- 核心依赖框架：
  - Vue 3
  - Vite
  - Vue Router
  - Pinia (状态管理)
  - Axios (HTTP客户端)
  - Markdown解析器

### 4.3 版本兼容性
- 定期更新依赖版本，修复安全漏洞
- 新增依赖需评估必要性，避免功能重复
- 保持前后端技术栈的兼容性

## 5. AI友好编程指南

### 5.1 代码风格指南

#### 5.1.1 Java代码规范
```java
// 类注释示例
/**
 * 聊天服务接口
 * 
 * @author AI Assistant
 * @version 1.0
 */
public interface ChatService {
    
    /**
     * 发送消息并获取AI响应
     *
     * @param request 聊天请求对象
     * @return 聊天响应对象
     * @throws ChatException 当请求参数无效时抛出
     */
    ChatResponseDto sendMessage(ChatRequestDto request) throws ChatException;
}

// 实现类示例
@Service
public class ChatServiceImpl implements ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    private final DeepSeekService deepSeekService;
    private final MessageService messageService;
    
    @Autowired
    public ChatServiceImpl(DeepSeekService deepSeekService, 
                          MessageService messageService) {
        this.deepSeekService = deepSeekService;
        this.messageService = messageService;
    }
    
    @Override
    public ChatResponseDto sendMessage(ChatRequestDto request) throws ChatException {
        // 参数校验
        if (request == null || StringUtils.isBlank(request.getMessage())) {
            throw new ChatException("INVALID_REQUEST", "消息内容不能为空");
        }
        
        try {
            // 业务逻辑处理
            logger.info("处理聊天请求: {}", request.getConversationId());
            
            // 调用AI服务
            String aiResponse = deepSeekService.chat(request.getMessage());
            
            // 保存消息
            messageService.saveMessage(request, aiResponse);
            
            return new ChatResponseDto(aiResponse);
        } catch (Exception e) {
            logger.error("处理聊天请求异常", e);
            throw new ChatException("CHAT_ERROR", "处理聊天请求失败");
        }
    }
}
```

#### 5.1.2 Vue组件规范
```vue
<!-- MessageInput.vue -->
<template>
  <div class="message-input">
    <el-input
      v-model="inputMessage"
      type="textarea"
      :autosize="{ minRows: 1, maxRows: 4 }"
      placeholder="请输入消息..."
      @keyup.enter="handleSendMessage"
    />
    <el-button
      type="primary"
      :loading="isLoading"
      @click="handleSendMessage"
    >
      发送
    </el-button>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useChatStore } from '@/stores/chat'

export default {
  name: 'MessageInput',
  emits: ['send-message'],
  setup(props, { emit }) {
    const inputMessage = ref('')
    const isLoading = ref(false)
    const chatStore = useChatStore()
    
    const canSend = computed(() => {
      return inputMessage.value.trim() && !isLoading.value
    })
    
    const handleSendMessage = async () => {
      if (!canSend.value) return
      
      const message = inputMessage.value.trim()
      inputMessage.value = ''
      isLoading.value = true
      
      try {
        await chatStore.sendMessage(message)
        emit('send-message', message)
      } catch (error) {
        console.error('发送消息失败:', error)
      } finally {
        isLoading.value = false
      }
    }
    
    return {
      inputMessage,
      isLoading,
      canSend,
      handleSendMessage
    }
  }
}
</script>

<style scoped>
.message-input {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid var(--border-color);
}
</style>
```

### 5.2 设计模式应用

#### 5.2.1 策略模式 - AI服务适配
```java
// AI服务策略接口
public interface AiService {
    String chat(String message, String conversationId);
    boolean isAvailable();
}

// DeepSeek实现
@Service
public class DeepSeekService implements AiService {
    @Override
    public String chat(String message, String conversationId) {
        // DeepSeek API调用逻辑
        return callDeepSeekApi(message, conversationId);
    }
    
    @Override
    public boolean isAvailable() {
        return deepSeekApiHealthCheck();
    }
}

// OpenAI实现
@Service
public class OpenAiService implements AiService {
    @Override
    public String chat(String message, String conversationId) {
        // OpenAI API调用逻辑
        return callOpenAiApi(message, conversationId);
    }
    
    @Override
    public boolean isAvailable() {
        return openAiApiHealthCheck();
    }
}

// AI服务工厂
@Component
public class AiServiceFactory {
    private final Map<String, AiService> serviceMap;
    
    @Autowired
    public AiServiceFactory(List<AiService> services) {
        this.serviceMap = services.stream()
            .collect(Collectors.toMap(
                service -> service.getClass().getSimpleName(),
                Function.identity()
            ));
    }
    
    public AiService getService(String serviceName) {
        AiService service = serviceMap.get(serviceName);
        if (service == null || !service.isAvailable()) {
            // 返回默认可用服务
            return getDefaultService();
        }
        return service;
    }
}
```

#### 5.2.2 观察者模式 - 消息通知
```java
// 消息事件监听器
public interface MessageEventListener {
    void onMessageReceived(MessageEvent event);
    void onMessageSent(MessageEvent event);
}

// WebSocket通知监听器
@Component
public class WebSocketNotificationListener implements MessageEventListener {
    
    @Override
    public void onMessageReceived(MessageEvent event) {
        // 通过WebSocket推送消息
        webSocketService.broadcast(event.getMessage());
    }
    
    @Override
    public void onMessageSent(MessageEvent event) {
        // 记录消息发送日志
        logger.info("消息已发送: {}", event.getMessage().getId());
    }
}

// 消息服务发布事件
@Service
public class MessageService {
    private final List<MessageEventListener> listeners;
    
    public void saveMessage(Message message) {
        // 保存消息
        messageRepository.save(message);
        
        // 发布事件
        MessageEvent event = new MessageEvent(message);
        listeners.forEach(listener -> listener.onMessageReceived(event));
    }
}
```

### 5.3 错误处理策略

#### 5.3.1 异常处理
```java
// 自定义异常类
public class ChatException extends RuntimeException {
    private final String errorCode;
    
    public ChatException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleChatException(ChatException e) {
        logger.warn("聊天业务异常: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        logger.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("SYSTEM_ERROR", "系统处理异常，请稍后重试"));
    }
}

// 错误响应对象
public class ErrorResponse {
    private String code;
    private String message;
    private Long timestamp;
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // getters and setters
}
```

#### 5.3.2 前端错误处理
```javascript
// API错误处理
import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 添加请求头等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const errorMessage = error.response?.data?.message || '请求失败'
    const errorCode = error.response?.data?.code || 'UNKNOWN_ERROR'
    
    // 统一错误处理
    console.error(`API错误 [${errorCode}]: ${errorMessage}`)
    
    // 抛出标准化错误
    throw new ApiError(errorCode, errorMessage)
  }
)

// 自定义API错误类
class ApiError extends Error {
  constructor(code, message) {
    super(message)
    this.code = code
    this.name = 'ApiError'
  }
}

export { apiClient, ApiError }
```

## 6. 接口开发规范

### 6.1 RESTful API设计

#### 6.1.1 URL设计规范
- 使用名词复数形式：`/api/conversations`、`/api/messages`
- 使用层级关系：`/api/conversations/{id}/messages`
- 避免动词：使用HTTP方法表示操作
- 使用小写字母和连字符：`/api/chat-sessions`

#### 6.1.2 HTTP方法使用
- GET: 获取资源
- POST: 创建资源
- PUT: 完整更新资源
- PATCH: 部分更新资源
- DELETE: 删除资源

#### 6.1.3 API接口示例
```java
@RestController
@RequestMapping("/api/v1")
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * 发送聊天消息
     */
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @PathVariable String conversationId,
            @RequestBody @Valid ChatRequestDto request) {
        
        request.setConversationId(conversationId);
        ChatResponseDto response = chatService.sendMessage(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取对话历史
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<MessageDto> messages = chatService.getMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * 创建新对话
     */
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDto> createConversation(
            @RequestBody @Valid CreateConversationRequest request) {
        
        ConversationDto conversation = chatService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }
    
    /**
     * 获取对话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<ConversationDto> conversations = chatService.getConversations(page, size);
        return ResponseEntity.ok(conversations);
    }
}
```

### 6.2 数据传输对象设计

#### 6.2.1 DTO设计原则
- 专用于数据传输，不包含业务逻辑
- 字段验证使用JSR-303注解
- 提供清晰的字段说明
- 版本化设计，支持API演进

```java
// 聊天请求DTO
public class ChatRequestDto {
    
    @NotBlank(message = "消息内容不能为空")
    @Length(max = 2000, message = "消息长度不能超过2000字符")
    private String message;
    
    @NotBlank(message = "对话ID不能为空")
    private String conversationId;
    
    private String userId;
    
    private Map<String, Object> metadata;
    
    // getters and setters
}

// 聊天响应DTO
public class ChatResponseDto {
    
    private String messageId;
    
    private String content;
    
    private String conversationId;
    
    private LocalDateTime timestamp;
    
    private String status;
    
    private Map<String, Object> metadata;
    
    // constructors, getters and setters
}
```

### 6.3 API文档规范

#### 6.3.1 Swagger/OpenAPI配置
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.dialogai.controller"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("DialogAI API")
            .description("智能对话系统API文档")
            .version("1.0.0")
            .build();
    }
}
```

#### 6.3.2 API注释示例
```java
@Api(tags = "聊天管理")
@RestController
public class ChatController {
    
    @ApiOperation(value = "发送聊天消息", notes = "向指定对话发送消息并获取AI响应")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @ApiParam(value = "对话ID", required = true) @PathVariable String conversationId,
            @ApiParam(value = "聊天请求", required = true) @RequestBody @Valid ChatRequestDto request) {
        // 实现逻辑
    }
}
```

## 7. 测试规范

### 7.1 单元测试

#### 7.1.1 测试覆盖要求
- 业务逻辑单元测试覆盖率不低于80%
- 关键服务方法必须有测试用例
- 异常情况必须有测试覆盖

#### 7.1.2 测试示例
```java
@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {
    
    @Mock
    private DeepSeekService deepSeekService;
    
    @Mock
    private MessageService messageService;
    
    @InjectMocks
    private ChatServiceImpl chatService;
    
    @Test
    void sendMessage_Success() {
        // Given
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");
        request.setConversationId("conv-123");
        
        when(deepSeekService.chat(anyString())).thenReturn("AI Response");
        
        // When
        ChatResponseDto response = chatService.sendMessage(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("AI Response");
        verify(messageService).saveMessage(any(), any());
    }
    
    @Test
    void sendMessage_EmptyMessage_ThrowsException() {
        // Given
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("");
        
        // When & Then
        assertThrows(ChatException.class, () -> chatService.sendMessage(request));
    }
}
```

### 7.2 集成测试

#### 7.2.1 API集成测试
```java
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ChatControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Test
    void sendMessage_Integration_Success() {
        // Given
        Conversation conversation = new Conversation();
        conversation.setId("conv-123");
        conversationRepository.save(conversation);
        
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");
        
        // When
        ResponseEntity<ChatResponseDto> response = restTemplate.postForEntity(
            "/api/v1/conversations/conv-123/messages",
            request,
            ChatResponseDto.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
```

### 7.3 前端测试

#### 7.3.1 Vue组件测试
```javascript
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import MessageInput from '@/components/MessageInput.vue'

describe('MessageInput.vue', () => {
  let wrapper

  beforeEach(() => {
    const pinia = createPinia()
    wrapper = mount(MessageInput, {
      global: {
        plugins: [pinia]
      }
    })
  })

  it('renders input field', () => {
    expect(wrapper.find('textarea').exists()).toBe(true)
  })

  it('emits send-message event when button clicked', async () => {
    await wrapper.find('textarea').setValue('Hello')
    await wrapper.find('button').trigger('click')
    
    expect(wrapper.emitted('send-message')).toBeTruthy()
    expect(wrapper.emitted('send-message')[0]).toEqual(['Hello'])
  })

  it('disables button when input is empty', () => {
    expect(wrapper.find('button').element.disabled).toBe(true)
  })
})
```

## 8. 部署和运维规范

### 8.1 环境配置

#### 8.1.1 配置文件管理
```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  
  datasource:
    url: ${DATABASE_URL:jdbc:h2:mem:testdb}
    username: ${DATABASE_USERNAME:sa}
    password: ${DATABASE_PASSWORD:}
  
  jpa:
    hibernate:
      ddl-auto: ${JPA_DDL_AUTO:update}
    show-sql: ${JPA_SHOW_SQL:false}

dialogai:
  deepseek:
    api-key: ${DEEPSEEK_API_KEY:}
    base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com}
  
  redis:
    enabled: ${REDIS_ENABLED:false}
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
```

#### 8.1.2 Docker配置
```dockerfile
# Dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/dialogai-server-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  dialogai-server:
    build: ./DialogAI-server
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:mysql://mysql:3306/dialogai
      - DATABASE_USERNAME=root
      - DATABASE_PASSWORD=password
    depends_on:
      - mysql
  
  dialogai-client:
    build: ./DialogAI-client
    ports:
      - "3000:3000"
    depends_on:
      - dialogai-server
  
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=dialogai
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

### 8.2 监控和日志

#### 8.2.1 日志配置
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="!prod">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/dialogai.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/dialogai.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="WARN">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

#### 8.2.2 健康检查
```java
@Component
public class HealthCheckController {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        status.put("services", checkServices());
        
        return ResponseEntity.ok(status);
    }
    
    private Map<String, String> checkServices() {
        Map<String, String> services = new HashMap<>();
        services.put("deepseek", deepSeekService.isAvailable() ? "UP" : "DOWN");
        return services;
    }
}
```

## 9. 安全规范

### 9.1 输入验证
- 所有用户输入必须进行验证和清理
- 防止XSS攻击：过滤HTML标签和脚本
- 防止SQL注入：使用参数化查询
- 限制输入长度和格式

### 9.2 API安全
- 实现请求频率限制
- 敏感操作需要身份认证
- 使用HTTPS传输敏感数据
- API密钥安全存储

### 9.3 数据安全
- 敏感数据加密存储
- 用户会话安全管理
- 定期清理过期数据
- 数据备份和恢复策略

## 10. 性能优化

### 10.1 后端性能
- 数据库查询优化，使用索引
- 缓存常用数据，减少数据库访问
- 异步处理长耗时操作
- 连接池配置优化

### 10.2 前端性能
- 组件懒加载，减少初始包大小
- 图片和资源压缩
- CDN加速静态资源
- 浏览器缓存策略

### 10.3 通信优化
- HTTP/2支持
- Gzip压缩
- Keep-Alive连接复用
- 合理的超时配置

## 11. AI代码生成指导

### 11.1 提示词模板

#### 11.1.1 后端服务生成模板
```
请根据以下信息生成一个Spring Boot服务类：
- 服务名称：[服务名]
- 服务职责：[职责描述]
- 依赖服务：[依赖的其他服务]
- 主要方法：
  1. [方法名]：[方法描述]
  2. [方法名]：[方法描述]
- 异常处理：[异常处理要求]
- 特殊要求：[其他要求]

请遵循以下代码风格：
- 使用Java 11特性
- 方法注释使用Javadoc格式
- 异常应当包含错误码和详细信息
- 遵循项目的命名约定
- 使用Spring Boot注解
```

#### 11.1.2 Vue组件生成模板
```
请根据以下信息生成一个Vue 3组件：
- 组件名称：[组件名]
- 组件功能：[功能描述]
- Props参数：[参数列表]
- 发射事件：[事件列表]
- 使用的组合式API：[Composition API]
- 样式要求：[样式要求]

请遵循以下要求：
- 使用Vue 3 Composition API
- 使用TypeScript（如果适用）
- 组件应当是响应式的
- 包含必要的错误处理
- 遵循Vue官方风格指南
```

### 11.2 代码审查要点

1. **代码规范性**: 是否符合项目的命名约定和格式化规则
2. **异常处理**: 是否正确处理了异常和边界情况
3. **性能考虑**: 是否存在性能问题或优化空间
4. **安全性**: 是否存在安全漏洞或风险
5. **可测试性**: 代码是否便于编写测试用例
6. **可维护性**: 代码是否清晰易懂，便于维护
7. **一致性**: 是否与现有代码风格和架构保持一致

### 11.3 迭代优化策略

1. **首次生成**: 重点关注功能实现和整体结构
2. **第一次优化**: 完善异常处理和边界情况
3. **第二次优化**: 优化性能和安全性
4. **第三次优化**: 完善测试和文档
5. **最终检查**: 确保代码质量和规范性

## 总结

本文档提供了DialogAI项目的AI友好配置规范，涵盖了项目架构、代码组织、开发规范、测试要求等各个方面。遵循这些规范可以确保：

1. **代码质量**: 统一的代码风格和质量标准
2. **开发效率**: 清晰的项目结构和开发流程
3. **维护性**: 易于理解和维护的代码库
4. **扩展性**: 支持项目未来的功能扩展
5. **AI友好**: 便于AI辅助开发和代码生成

建议开发团队严格遵循这些规范，并根据项目发展的实际需要适时更新和完善规范内容。 