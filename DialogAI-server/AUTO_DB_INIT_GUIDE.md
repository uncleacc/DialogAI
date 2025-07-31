# DialogAI ���ݿ��Զ���ʼ������ʹ��ָ��

## ? ���ܸ���

DialogAI��Ŀ����������ݿ��Զ���ʼ�����ܵĿ�����ʵ����"���伴��"�Ĳ������飡

### ? ��������

- **? �Զ��������ݿ�**��Ӧ������ʱ�Զ��������ݿ⣨��������ڣ�
- **? �Զ�������ṹ**��ʹ��Hibernate DDL�Զ��������б���ı�
- **? �Զ���ʼ������**������ϵͳ���úͻ�ӭ����
- **? �������ӿ�**��ʵʱ������ݿ��ҵ��״̬
- **? �໷��֧��**�����������ԡ����������Ĳ�ͬ����
- **?? ���ù���**����̬ϵͳ���ù���ͻ���
- **? ������**����ϸ��ϵͳ״̬������ͳ��

## ? ���ٿ�ʼ

### 1. ����׼��

ȷ������ϵͳ�Ѱ�װ��
- Java 8+
- MySQL 8.0+������������
- Maven 3.6+

### 2. �������ݿ�����

�༭ `src/main/resources/application.yml`��

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dialog_ai?createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 3. ����Ӧ��

**��ʽһ��ʹ�������ű����Ƽ���**
```bash
cd DialogAI-server
./start-with-db-init.sh dev 8080
```

**��ʽ����ʹ��Maven**
```bash
cd DialogAI-server
mvn spring-boot:run -Dspring.profiles.active=dev
```

**��ʽ����ʹ��JAR��**
```bash
cd DialogAI-server
mvn clean package -DskipTests
java -jar target/dialog-ai-backend-1.0.0.jar --spring.profiles.active=dev
```

### 4. ��֤����

Ӧ��������ῴ��������־��
```
[INFO] ��ʼ���ݿ��ʼ�����...
[INFO] ���ݿ����ӳɹ���
[INFO] �� 'conversations' �Ѵ���
[INFO] �� 'messages' �Ѵ���
[INFO] ���ݿ����ݳ�ʼ����ɣ�
```

���ʽ������ӿ���֤��
```bash
curl http://localhost:8080/api/health
```

## ? API�ӿ�

### �������ӿ�

| �ӿ� | ���� | ˵�� |
|------|------|------|
| `/api/health` | GET | ϵͳ���彡����� |
| `/api/health/database` | GET | ���ݿ�ר���� |
| `/api/health/business` | GET | ҵ�����ݼ�� |

### ϵͳ���ýӿ�

| �ӿ� | ���� | ˵�� |
|------|------|------|
| `/api/config/public` | GET | ��ȡ���й������� |
| `/api/config/core` | GET | ��ȡ����ϵͳ���� |
| `/api/config/chat` | GET | ��ȡ����������� |
| `/api/config/ui` | GET | ��ȡUI������� |
| `/api/config/features` | GET | ��ȡ���ܿ������� |
| `/api/config/overview` | GET | ��ȡ���ø�����Ϣ |
| `/api/config/refresh` | POST | ˢ�����û��� |

## ?? ���ݿ�ṹ

### ���ı�

1. **conversations** - �Ự��
   - �洢�û��Ի��Ự��Ϣ
   - ֧��״̬������Ծ���鵵��ɾ����

2. **messages** - ��Ϣ��
   - �洢�Ի���Ϣ����
   - �����û���Ϣ��AI�ظ�
   - ֧����Ϣ״̬����

### ��չ��

3. **users** - �û���Ԥ����
4. **conversation_shares** - �Ự�����Ԥ����
5. **system_configs** - ϵͳ���ñ�

## ?? ���ù���

### ϵͳĬ������

| ���ü� | Ĭ��ֵ | ˵�� |
|--------|--------|------|
| `system.version` | 1.0.0 | ϵͳ�汾�� |
| `system.name` | DialogAI | ϵͳ���� |
| `chat.max_message_length` | 2000 | ������Ϣ��󳤶� |
| `ai.default_model` | deepseek-chat | AIĬ��ģ�� |
| `ui.theme` | auto | Ĭ������ |
| `feature.conversation_share` | true | �Ự�����ܿ��� |

### ��̬���ù���

ͨ�� `SystemConfigService` ���Զ�̬��ȡ���޸����ã�

```java
@Autowired
private SystemConfigService configService;

// ��ȡ����
String theme = configService.getString("ui.theme", "auto");
Integer maxLength = configService.getInteger("chat.max_message_length", 2000);
Boolean shareEnabled = configService.getBoolean("feature.conversation_share", true);

// ��������
configService.setString("ui.theme", "dark", "�û���������", true);
```

## ? �໷������

### �������� (dev)
- DDLģʽ��`create-drop` - ÿ���������´�����
- ��ʾSQL������
- ���ݳ�ʼ��������

```bash
./start-with-db-init.sh dev 8080
```

### ���Ի��� (test)
- ���ݿ⣺H2�ڴ����ݿ�
- DDLģʽ��`create-drop`
- H2����̨������

```bash
java -jar app.jar --spring.profiles.active=test
```

### �������� (prod)
- DDLģʽ��`validate` - ����֤��ṹ
- ��ʾSQL������
- ���ݳ�ʼ��������

```bash
# ���û�������
export DB_HOST=prod-mysql
export DB_PASSWORD=secure_password
export DEEPSEEK_API_KEY=your_api_key

./start-with-db-init.sh prod 8080
```

## ? �Զ�������

### ��������

֧��ͨ�����������Զ������ã�

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=dialog_ai
export DB_USERNAME=root
export DB_PASSWORD=your_password
export DEEPSEEK_API_KEY=your_api_key
```

### ��������

```bash
java -jar app.jar \
  --spring.profiles.active=prod \
  --server.port=9090 \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/dialogai
```

## ?? �����ų�

### ��������

1. **���ݿ�����ʧ��**
   ```
   ���������
   - ���MySQL�����Ƿ�����
   - ��֤�û�������
   - ȷ����������
   ```

2. **Ȩ�޲���**
   ```
   ���������
   - ʹ���д������ݿ�Ȩ�޵��û�
   - �����Ҫ�����ݿ�Ȩ��
   ```

3. **���Ѵ��ڴ���**
   ```
   ���������
   - ��������ʹ�� validate ģʽ
   - ����������ɾ�����ݿ����´���
   ```

### ��־����

������ϸ��־��
```yaml
logging:
  level:
    com.dialogai: DEBUG
    org.springframework.jdbc: DEBUG
```

## ? ��غ���ά

### �������ʾ��

```bash
# ϵͳ�������
curl http://localhost:8080/api/health
```

��Ӧʾ����
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

### ���ü��

```bash
# ��ȡ���ø���
curl http://localhost:8080/api/config/overview
```

## ? ���ʵ��

1. **��������**
   - ʹ�� `create-drop` ģʽ���ٵ���
   - ����SQL��־���ڵ���
   - ʹ���ڴ����ݿ���е�Ԫ����

2. **��������**
   - ʹ�� `validate` ģʽ��������
   - �ر�SQL��־��������
   - ���ڱ������ݿ�
   - ��ؽ������ӿ�

3. **���ù���**
   - ��������ʹ�û�������
   - �������û�����������
   - ����ˢ�����û���

## ? ����ĵ�

- [���ݿ�������ϸ˵��](DATABASE_SETUP.md)
- [AI�Ѻ����ù淶](../AI_FRIENDLY_STANDARDS.md)
- [��ĿAPI�ĵ�](doc/�ӿ��ĵ�.md)

## ? �ܽ�

ͨ�����Զ������ݿ��ʼ�����ܣ�DialogAI��Ŀʵ���ˣ�

? **����������** - �����ֶ��������ݿ�ͱ�  
? **�໷��֧��** - ���������ԡ����������޷��л�  
? **�������** - ʵʱ������ݿ��ҵ��״̬  
? **���ù���** - ��̬���ù���ͻ������  
? **�������** - ��ϸ�Ĵ�����Ϣ�͹����ų�ָ��  

���������Ŀ�Ĳ����ά���������ÿ����߿���רע��ҵ���ܵĿ����� 