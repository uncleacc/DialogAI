# DialogAI ��Ŀ AI�Ѻ����ù淶�ĵ�

## ��Ŀ����

DialogAI��һ�����ܶԻ�ϵͳ������ǰ��˷���ܹ���
- **ǰ��**: Vue.js + Vite���ṩ�û���������
- **���**: Spring Boot + Maven���ṩAPI�����ҵ���߼�
- **���Ĺ���**: ���ܶԻ�����Ϣ���������л���ʵʱͨ��

## 1. ��ĿĿ¼�ṹ

```
DialogAI/
������ DialogAI-client/                 # ǰ��Ӧ��
��   ������ src/
��   ��   ������ api/                     # API�ӿڷ�װ
��   ��   ��   ������ chat.js              # �������API
��   ��   ������ components/              # Vue���
��   ��   ��   ������ MessageInput.vue     # ��Ϣ�������
��   ��   ��   ������ MessageItem.vue      # ��Ϣ�����  
��   ��   ��   ������ MessageList.vue      # ��Ϣ�б����
��   ��   ��   ������ Sidebar.vue          # ��������
��   ��   ��   ������ ThemeToggle.vue      # �����л����
��   ��   ������ router/                  # ·������
��   ��   ��   ������ index.js             # ·�ɶ���
��   ��   ������ stores/                  # ״̬����
��   ��   ��   ������ chat.js              # ����״̬����
��   ��   ��   ������ theme.js             # ����״̬����
��   ��   ������ utils/                   # ���ߺ���
��   ��   ��   ������ date.js              # ���ڴ�����
��   ��   ��   ������ markdown.js          # Markdown������
��   ��   ������ views/                   # ҳ����ͼ
��   ��   ��   ������ ChatView.vue         # ����ҳ��
��   ��   ������ App.vue                  # �����
��   ��   ������ main.js                  # Ӧ�����
��   ��   ������ style.css                # ȫ����ʽ
��   ������ public/                      # ��̬��Դ
��   ������ package.json                 # ǰ����������
��   ������ vite.config.js              # Vite��������
��   ������ README.md                    # ǰ��˵���ĵ�
������ DialogAI-server/                 # ���Ӧ��
��   ������ src/main/java/com/dialogai/
��   ��   ������ config/                  # ������
��   ��   ��   ������ DeepSeekConfig.java  # AI��������
��   ��   ��   ������ RedisConfig.java     # Redis����
��   ��   ������ constant/                # ��������
��   ��   ��   ������ Constants.java       # ϵͳ����
��   ��   ������ controller/              # ��������
��   ��   ��   ������ ChatController.java  # ���������
��   ��   ������ dto/                     # ���ݴ������
��   ��   ��   ������ ChatRequestDto.java  # ��������DTO
��   ��   ��   ������ ChatResponseDto.java # ������ӦDTO
��   ��   ��   ������ ConversationDto.java # �Ի�DTO
��   ��   ��   ������ MessageDto.java      # ��ϢDTO
��   ��   ������ entity/                  # ʵ����
��   ��   ��   ������ Conversation.java    # �Ի�ʵ��
��   ��   ��   ������ Message.java         # ��Ϣʵ��
��   ��   ������ exception/               # �쳣����
��   ��   ��   ������ GlobalExceptionHandler.java # ȫ���쳣������
��   ��   ������ repository/              # ���ݷ��ʲ�
��   ��   ��   ������ ConversationRepository.java # �Ի��ֿ�
��   ��   ��   ������ MessageRepository.java      # ��Ϣ�ֿ�
��   ��   ������ service/                 # ҵ������
��   ��   ��   ������ ChatService.java     # �������
��   ��   ��   ������ ConversationService.java # �Ի�����
��   ��   ��   ������ DeepSeekService.java # AI����
��   ��   ��   ������ MessageService.java  # ��Ϣ����
��   ��   ������ DialogAiApplication.java # Ӧ��������
��   ������ src/main/resources/
��   ��   ������ application.yml          # Ӧ������
��   ������ src/test/                    # ���Դ���
��   ������ pom.xml                      # Maven��������
��   ������ README.md                    # ���˵���ĵ�
������ README.md                        # ��Ŀ����˵���ĵ�
```

## 2. ����ܹ�ԭ��

### 2.1 �ֲ�ܹ�
- **���ֲ�(Controller)**: ����HTTP���󡢲���У�顢��Ӧ��װ
- **ҵ���(Service)**: ʵ�ֺ���ҵ���߼����Ի�����AI����
- **���ݷ��ʲ�(Repository)**: ���ݳ־û�����Ϣ�洢���Ի�����
- **ʵ���(Entity)**: ����ҵ��ʵ�������ģ��
- **DTO��**: ���ݴ����������ǰ��˽���

### 2.2 ǰ��˷���
- ǰ�˸����û�����ͽ����߼�
- ����ṩRESTful API����
- ͨ��HTTP/WebSocket����ͨ��
- ʵʱ��Ϣͨ��Server-Sent Events (SSE)����

## 3. ������֯Լ��

### 3.1 �������淶
- ͳһʹ��`com.dialogai.{ģ����}.{���}`��ʽ
- ģ����ʹ��ȫСд����`chat`��`conversation`��`message`
- ��������淶��
  - `controller`: ��������API�ӿ�
  - `service`: ҵ�����ӿں�ʵ��
  - `repository`: ���ݷ��ʽӿ�
  - `entity`: ʵ���������ģ��
  - `dto`: ���ݴ������
  - `config`: ������
  - `exception`: �쳣��
  - `constant`: ��������

### 3.2 �������淶
- Controller����`Controller`��β
- Service�ӿ���ǰ׺��ʵ������`Impl`��β
- Repository�ӿ���`Repository`��β
- ʵ����ʹ�����ʣ���`Message`��`Conversation`
- DTO����`Dto`��β
- ��������`Config`��β
- �쳣����`Exception`��β
- ��������`Constants`��β

### 3.3 ǰ���ļ������淶
- Vue���ʹ��PascalCase��������`MessageInput.vue`
- JavaScript�ļ�ʹ��camelCase��������`chat.js`
- ��ʽ�ļ�ʹ��kebab-case��������`message-list.css`
- ҳ����ͼ��`View`��β����`ChatView.vue`

## 4. ��������Լ��

### 4.1 �����������
- ͳһ��pom.xml�й���汾��
- ʹ��Spring Boot������ͳһ�汾
- ����������ܣ�
  - Spring Boot 2.7+
  - Spring Data JPA
  - Spring Web
  - MySQL/H2 ���ݿ�
  - Redis (��ѡ)
  - WebSocket/SSE֧��

### 4.2 ǰ����������
- ͳһ��package.json�й���汾��
- ����������ܣ�
  - Vue 3
  - Vite
  - Vue Router
  - Pinia (״̬����)
  - Axios (HTTP�ͻ���)
  - Markdown������

### 4.3 �汾������
- ���ڸ��������汾���޸���ȫ©��
- ����������������Ҫ�ԣ����⹦���ظ�
- ����ǰ��˼���ջ�ļ�����

## 5. AI�Ѻñ��ָ��

### 5.1 ������ָ��

#### 5.1.1 Java����淶
```java
// ��ע��ʾ��
/**
 * �������ӿ�
 * 
 * @author AI Assistant
 * @version 1.0
 */
public interface ChatService {
    
    /**
     * ������Ϣ����ȡAI��Ӧ
     *
     * @param request �����������
     * @return ������Ӧ����
     * @throws ChatException �����������Чʱ�׳�
     */
    ChatResponseDto sendMessage(ChatRequestDto request) throws ChatException;
}

// ʵ����ʾ��
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
        // ����У��
        if (request == null || StringUtils.isBlank(request.getMessage())) {
            throw new ChatException("INVALID_REQUEST", "��Ϣ���ݲ���Ϊ��");
        }
        
        try {
            // ҵ���߼�����
            logger.info("������������: {}", request.getConversationId());
            
            // ����AI����
            String aiResponse = deepSeekService.chat(request.getMessage());
            
            // ������Ϣ
            messageService.saveMessage(request, aiResponse);
            
            return new ChatResponseDto(aiResponse);
        } catch (Exception e) {
            logger.error("�������������쳣", e);
            throw new ChatException("CHAT_ERROR", "������������ʧ��");
        }
    }
}
```

#### 5.1.2 Vue����淶
```vue
<!-- MessageInput.vue -->
<template>
  <div class="message-input">
    <el-input
      v-model="inputMessage"
      type="textarea"
      :autosize="{ minRows: 1, maxRows: 4 }"
      placeholder="��������Ϣ..."
      @keyup.enter="handleSendMessage"
    />
    <el-button
      type="primary"
      :loading="isLoading"
      @click="handleSendMessage"
    >
      ����
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
        console.error('������Ϣʧ��:', error)
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

### 5.2 ���ģʽӦ��

#### 5.2.1 ����ģʽ - AI��������
```java
// AI������Խӿ�
public interface AiService {
    String chat(String message, String conversationId);
    boolean isAvailable();
}

// DeepSeekʵ��
@Service
public class DeepSeekService implements AiService {
    @Override
    public String chat(String message, String conversationId) {
        // DeepSeek API�����߼�
        return callDeepSeekApi(message, conversationId);
    }
    
    @Override
    public boolean isAvailable() {
        return deepSeekApiHealthCheck();
    }
}

// OpenAIʵ��
@Service
public class OpenAiService implements AiService {
    @Override
    public String chat(String message, String conversationId) {
        // OpenAI API�����߼�
        return callOpenAiApi(message, conversationId);
    }
    
    @Override
    public boolean isAvailable() {
        return openAiApiHealthCheck();
    }
}

// AI���񹤳�
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
            // ����Ĭ�Ͽ��÷���
            return getDefaultService();
        }
        return service;
    }
}
```

#### 5.2.2 �۲���ģʽ - ��Ϣ֪ͨ
```java
// ��Ϣ�¼�������
public interface MessageEventListener {
    void onMessageReceived(MessageEvent event);
    void onMessageSent(MessageEvent event);
}

// WebSocket֪ͨ������
@Component
public class WebSocketNotificationListener implements MessageEventListener {
    
    @Override
    public void onMessageReceived(MessageEvent event) {
        // ͨ��WebSocket������Ϣ
        webSocketService.broadcast(event.getMessage());
    }
    
    @Override
    public void onMessageSent(MessageEvent event) {
        // ��¼��Ϣ������־
        logger.info("��Ϣ�ѷ���: {}", event.getMessage().getId());
    }
}

// ��Ϣ���񷢲��¼�
@Service
public class MessageService {
    private final List<MessageEventListener> listeners;
    
    public void saveMessage(Message message) {
        // ������Ϣ
        messageRepository.save(message);
        
        // �����¼�
        MessageEvent event = new MessageEvent(message);
        listeners.forEach(listener -> listener.onMessageReceived(event));
    }
}
```

### 5.3 ���������

#### 5.3.1 �쳣����
```java
// �Զ����쳣��
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

// ȫ���쳣������
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleChatException(ChatException e) {
        logger.warn("����ҵ���쳣: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        logger.error("ϵͳ�쳣", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("SYSTEM_ERROR", "ϵͳ�����쳣�����Ժ�����"));
    }
}

// ������Ӧ����
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

#### 5.3.2 ǰ�˴�����
```javascript
// API������
import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// ����������
apiClient.interceptors.request.use(
  config => {
    // �������ͷ��
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// ��Ӧ������
apiClient.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const errorMessage = error.response?.data?.message || '����ʧ��'
    const errorCode = error.response?.data?.code || 'UNKNOWN_ERROR'
    
    // ͳһ������
    console.error(`API���� [${errorCode}]: ${errorMessage}`)
    
    // �׳���׼������
    throw new ApiError(errorCode, errorMessage)
  }
)

// �Զ���API������
class ApiError extends Error {
  constructor(code, message) {
    super(message)
    this.code = code
    this.name = 'ApiError'
  }
}

export { apiClient, ApiError }
```

## 6. �ӿڿ����淶

### 6.1 RESTful API���

#### 6.1.1 URL��ƹ淶
- ʹ�����ʸ�����ʽ��`/api/conversations`��`/api/messages`
- ʹ�ò㼶��ϵ��`/api/conversations/{id}/messages`
- ���⶯�ʣ�ʹ��HTTP������ʾ����
- ʹ��Сд��ĸ�����ַ���`/api/chat-sessions`

#### 6.1.2 HTTP����ʹ��
- GET: ��ȡ��Դ
- POST: ������Դ
- PUT: ����������Դ
- PATCH: ���ָ�����Դ
- DELETE: ɾ����Դ

#### 6.1.3 API�ӿ�ʾ��
```java
@RestController
@RequestMapping("/api/v1")
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * ����������Ϣ
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
     * ��ȡ�Ի���ʷ
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
     * �����¶Ի�
     */
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDto> createConversation(
            @RequestBody @Valid CreateConversationRequest request) {
        
        ConversationDto conversation = chatService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }
    
    /**
     * ��ȡ�Ի��б�
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

### 6.2 ���ݴ���������

#### 6.2.1 DTO���ԭ��
- ר�������ݴ��䣬������ҵ���߼�
- �ֶ���֤ʹ��JSR-303ע��
- �ṩ�������ֶ�˵��
- �汾����ƣ�֧��API�ݽ�

```java
// ��������DTO
public class ChatRequestDto {
    
    @NotBlank(message = "��Ϣ���ݲ���Ϊ��")
    @Length(max = 2000, message = "��Ϣ���Ȳ��ܳ���2000�ַ�")
    private String message;
    
    @NotBlank(message = "�Ի�ID����Ϊ��")
    private String conversationId;
    
    private String userId;
    
    private Map<String, Object> metadata;
    
    // getters and setters
}

// ������ӦDTO
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

### 6.3 API�ĵ��淶

#### 6.3.1 Swagger/OpenAPI����
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
            .description("���ܶԻ�ϵͳAPI�ĵ�")
            .version("1.0.0")
            .build();
    }
}
```

#### 6.3.2 APIע��ʾ��
```java
@Api(tags = "�������")
@RestController
public class ChatController {
    
    @ApiOperation(value = "����������Ϣ", notes = "��ָ���Ի�������Ϣ����ȡAI��Ӧ")
    @ApiResponses({
        @ApiResponse(code = 200, message = "�ɹ�"),
        @ApiResponse(code = 400, message = "�����������"),
        @ApiResponse(code = 500, message = "�������ڲ�����")
    })
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @ApiParam(value = "�Ի�ID", required = true) @PathVariable String conversationId,
            @ApiParam(value = "��������", required = true) @RequestBody @Valid ChatRequestDto request) {
        // ʵ���߼�
    }
}
```

## 7. ���Թ淶

### 7.1 ��Ԫ����

#### 7.1.1 ���Ը���Ҫ��
- ҵ���߼���Ԫ���Ը����ʲ�����80%
- �ؼ����񷽷������в�������
- �쳣��������в��Ը���

#### 7.1.2 ����ʾ��
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

### 7.2 ���ɲ���

#### 7.2.1 API���ɲ���
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

### 7.3 ǰ�˲���

#### 7.3.1 Vue�������
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

## 8. �������ά�淶

### 8.1 ��������

#### 8.1.1 �����ļ�����
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

#### 8.1.2 Docker����
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

### 8.2 ��غ���־

#### 8.2.1 ��־����
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

#### 8.2.2 �������
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

## 9. ��ȫ�淶

### 9.1 ������֤
- �����û�������������֤������
- ��ֹXSS����������HTML��ǩ�ͽű�
- ��ֹSQLע�룺ʹ�ò�������ѯ
- �������볤�Ⱥ͸�ʽ

### 9.2 API��ȫ
- ʵ������Ƶ������
- ���в�����Ҫ�����֤
- ʹ��HTTPS������������
- API��Կ��ȫ�洢

### 9.3 ���ݰ�ȫ
- �������ݼ��ܴ洢
- �û��Ự��ȫ����
- ���������������
- ���ݱ��ݺͻָ�����

## 10. �����Ż�

### 10.1 �������
- ���ݿ��ѯ�Ż���ʹ������
- ���泣�����ݣ��������ݿ����
- �첽������ʱ����
- ���ӳ������Ż�

### 10.2 ǰ������
- ��������أ����ٳ�ʼ����С
- ͼƬ����Դѹ��
- CDN���پ�̬��Դ
- ������������

### 10.3 ͨ���Ż�
- HTTP/2֧��
- Gzipѹ��
- Keep-Alive���Ӹ���
- ����ĳ�ʱ����

## 11. AI��������ָ��

### 11.1 ��ʾ��ģ��

#### 11.1.1 ��˷�������ģ��
```
�����������Ϣ����һ��Spring Boot�����ࣺ
- �������ƣ�[������]
- ����ְ��[ְ������]
- ��������[��������������]
- ��Ҫ������
  1. [������]��[��������]
  2. [������]��[��������]
- �쳣����[�쳣����Ҫ��]
- ����Ҫ��[����Ҫ��]

����ѭ���´�����
- ʹ��Java 11����
- ����ע��ʹ��Javadoc��ʽ
- �쳣Ӧ���������������ϸ��Ϣ
- ��ѭ��Ŀ������Լ��
- ʹ��Spring Bootע��
```

#### 11.1.2 Vue�������ģ��
```
�����������Ϣ����һ��Vue 3�����
- ������ƣ�[�����]
- ������ܣ�[��������]
- Props������[�����б�]
- �����¼���[�¼��б�]
- ʹ�õ����ʽAPI��[Composition API]
- ��ʽҪ��[��ʽҪ��]

����ѭ����Ҫ��
- ʹ��Vue 3 Composition API
- ʹ��TypeScript��������ã�
- ���Ӧ������Ӧʽ��
- ������Ҫ�Ĵ�����
- ��ѭVue�ٷ����ָ��
```

### 11.2 �������Ҫ��

1. **����淶��**: �Ƿ������Ŀ������Լ���͸�ʽ������
2. **�쳣����**: �Ƿ���ȷ�������쳣�ͱ߽����
3. **���ܿ���**: �Ƿ��������������Ż��ռ�
4. **��ȫ��**: �Ƿ���ڰ�ȫ©�������
5. **�ɲ�����**: �����Ƿ���ڱ�д��������
6. **��ά����**: �����Ƿ������׶�������ά��
7. **һ����**: �Ƿ������д�����ͼܹ�����һ��

### 11.3 �����Ż�����

1. **�״�����**: �ص��ע����ʵ�ֺ�����ṹ
2. **��һ���Ż�**: �����쳣����ͱ߽����
3. **�ڶ����Ż�**: �Ż����ܺͰ�ȫ��
4. **�������Ż�**: ���Ʋ��Ժ��ĵ�
5. **���ռ��**: ȷ�����������͹淶��

## �ܽ�

���ĵ��ṩ��DialogAI��Ŀ��AI�Ѻ����ù淶����������Ŀ�ܹ���������֯�������淶������Ҫ��ȸ������档��ѭ��Щ�淶����ȷ����

1. **��������**: ͳһ�Ĵ������������׼
2. **����Ч��**: ��������Ŀ�ṹ�Ϳ�������
3. **ά����**: ��������ά���Ĵ����
4. **��չ��**: ֧����Ŀδ���Ĺ�����չ
5. **AI�Ѻ�**: ����AI���������ʹ�������

���鿪���Ŷ��ϸ���ѭ��Щ�淶����������Ŀ��չ��ʵ����Ҫ��ʱ���º����ƹ淶���ݡ� 