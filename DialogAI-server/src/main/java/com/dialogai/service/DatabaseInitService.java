package com.dialogai.service;

import com.dialogai.entity.Conversation;
import com.dialogai.entity.Message;
import com.dialogai.entity.SystemConfig;
import com.dialogai.repository.ConversationRepository;
import com.dialogai.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Êý¾Ý¿â³õÊ¼»¯·þÎñ
 * ¸ºÔðÔÚÓ¦ÓÃÆô¶¯Ê±³õÊ¼»¯»ù´¡Êý¾Ý
 * 
 * @author DialogAI
 * @version 1.0
 */
@Slf4j
@Service
@Order(2)
public class DatabaseInitService implements CommandLineRunner {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseInitService(ConversationRepository conversationRepository,
                              MessageRepository messageRepository,
                              JdbcTemplate jdbcTemplate) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("¿ªÊ¼Êý¾Ý¿âÊý¾Ý³õÊ¼»¯...");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // ¼ÇÂ¼Á¬½Ó³Ø×´Ì¬
            logConnectionPoolStatus();
            
            // ³õÊ¼»¯ÏµÍ³ÅäÖÃ
            initSystemConfigs();
            
            // ³õÊ¼»¯Ä¬ÈÏ»á»°ºÍÏûÏ¢
            initDefaultConversation();
            
            long endTime = System.currentTimeMillis();
            log.info("Êý¾Ý¿âÊý¾Ý³õÊ¼»¯Íê³É£¡ºÄÊ±: {}ms", endTime - startTime);
            
        } catch (Exception e) {
            log.error("Êý¾Ý¿âÊý¾Ý³õÊ¼»¯Ê§°Ü£¡", e);
            // Êý¾Ý³õÊ¼»¯Ê§°Ü²»Ó¦¸Ã×èÖ¹Ó¦ÓÃÆô¶¯£¬Ö»¼ÇÂ¼´íÎó
        }
    }

    /**
     * ¼ÇÂ¼Á¬½Ó³Ø×´Ì¬
     */
    private void logConnectionPoolStatus() {
        try {
            Integer activeConnections = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.PROCESSLIST WHERE DB = DATABASE()", 
                Integer.class
            );
            log.info("µ±Ç°»îÔ¾Á¬½ÓÊý: {}", activeConnections);
        } catch (Exception e) {
            log.debug("ÎÞ·¨»ñÈ¡Á¬½Ó³Ø×´Ì¬: {}", e.getMessage());
        }
    }

    /**
     * ³õÊ¼»¯ÏµÍ³ÅäÖÃ
     */
    @Transactional
    public void initSystemConfigs() {
        log.info("³õÊ¼»¯ÏµÍ³ÅäÖÃ...");
        
        try {
            // ¼ì²ésystem_configs±íÊÇ·ñ´æÔÚ
            if (!isSystemConfigTableExists()) {
                log.info("ÏµÍ³ÅäÖÃ±í²»´æÔÚ£¬Ìø¹ýÅäÖÃ³õÊ¼»¯");
                return;
            }
            
            // ¼ì²éÊÇ·ñÒÑÓÐÅäÖÃÊý¾Ý
            String countSql = "SELECT COUNT(*) FROM system_configs";
            Integer configCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (configCount != null && configCount > 0) {
                log.info("ÏµÍ³ÅäÖÃÒÑ´æÔÚ {} Ïî£¬Ìø¹ý³õÊ¼»¯", configCount);
                return;
            }
            
            // ²åÈëÄ¬ÈÏÅäÖÃ
            insertDefaultConfigs();
            
            log.info("ÏµÍ³ÅäÖÃ³õÊ¼»¯Íê³É");
            
        } catch (Exception e) {
            log.error("³õÊ¼»¯ÏµÍ³ÅäÖÃÊ§°Ü", e);
        }
    }

    /**
     * ³õÊ¼»¯Ä¬ÈÏ»á»°
     */
    @Transactional
    public void initDefaultConversation() {
        log.info("³õÊ¼»¯Ä¬ÈÏ»á»°...");
        
        try {
            // ¼ì²éÊÇ·ñÒÑÓÐ»á»°Êý¾Ý
            long conversationCount = conversationRepository.count();
            
            if (conversationCount > 0) {
                log.info("ÒÑ´æÔÚ {} ¸ö»á»°£¬Ìø¹ýÄ¬ÈÏ»á»°³õÊ¼»¯", conversationCount);
                return;
            }
            
            // ´´½¨Ä¬ÈÏ»á»°
            Conversation defaultConversation = createDefaultConversation();
            
            // ´´½¨»¶Ó­ÏûÏ¢
            createWelcomeMessage(defaultConversation);
            
            log.info("Ä¬ÈÏ»á»°³õÊ¼»¯Íê³É");
            
        } catch (Exception e) {
            log.error("³õÊ¼»¯Ä¬ÈÏ»á»°Ê§°Ü", e);
        }
    }

    /**
     * ¼ì²éÏµÍ³ÅäÖÃ±íÊÇ·ñ´æÔÚ
     */
    private boolean isSystemConfigTableExists() {
        try {
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = 'system_configs'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            return tableCount != null && tableCount > 0;
        } catch (Exception e) {
            log.warn("¼ì²éÏµÍ³ÅäÖÃ±íÊÇ·ñ´æÔÚÊ±³ö´í: {}", e.getMessage());
            return false;
        }
    }

    /**
     * ²åÈëÄ¬ÈÏÅäÖÃ
     */
    private void insertDefaultConfigs() {
        String insertSql = "INSERT IGNORE INTO system_configs " +
                "(config_key, config_value, config_type, description, is_public) VALUES " +
                "(?, ?, ?, ?, ?)";
        
        // ÏµÍ³ÐÅÏ¢ÅäÖÃ
        executeConfigInsert(insertSql, "system.version", "1.0.0", "STRING", "ÏµÍ³°æ±¾ºÅ", true);
        executeConfigInsert(insertSql, "system.name", "DialogAI", "STRING", "ÏµÍ³Ãû³Æ", true);
        executeConfigInsert(insertSql, "system.description", "ÖÇÄÜ¶Ô»°ÏµÍ³", "STRING", "ÏµÍ³ÃèÊö", true);
        
        // ÁÄÌìÏà¹ØÅäÖÃ
        executeConfigInsert(insertSql, "chat.max_message_length", "2000", "INTEGER", "µ¥ÌõÏûÏ¢×î´ó³¤¶È", true);
        executeConfigInsert(insertSql, "chat.max_messages_per_conversation", "1000", "INTEGER", "Ã¿¸ö»á»°×î´óÏûÏ¢Êý", true);
        executeConfigInsert(insertSql, "chat.auto_title_enabled", "true", "BOOLEAN", "ÊÇ·ñÆôÓÃ×Ô¶¯Éú³É»á»°±êÌâ", true);
        
        // AIÏà¹ØÅäÖÃ
        executeConfigInsert(insertSql, "ai.default_model", "deepseek-chat", "STRING", "AIÄ¬ÈÏÄ£ÐÍ", false);
        executeConfigInsert(insertSql, "ai.max_tokens", "4000", "INTEGER", "AIÏìÓ¦×î´ótokenÊý", false);
        executeConfigInsert(insertSql, "ai.temperature", "0.7", "STRING", "AI´´ÔìÐÔ²ÎÊý", false);
        
        // UIÏà¹ØÅäÖÃ
        executeConfigInsert(insertSql, "ui.theme", "auto", "STRING", "Ä¬ÈÏÖ÷Ìâ£ºlight-Ã÷ÁÁ£¬dark-°µÉ«£¬auto-×Ô¶¯", true);
        executeConfigInsert(insertSql, "ui.language", "zh-CN", "STRING", "Ä¬ÈÏÓïÑÔ", true);
        
        // ¹¦ÄÜ¿ª¹ØÅäÖÃ
        executeConfigInsert(insertSql, "feature.conversation_share", "true", "BOOLEAN", "ÊÇ·ñÆôÓÃ»á»°·ÖÏí¹¦ÄÜ", true);
        executeConfigInsert(insertSql, "feature.export_conversation", "true", "BOOLEAN", "ÊÇ·ñÆôÓÃ»á»°µ¼³ö¹¦ÄÜ", true);
        
        // °²È«Ïà¹ØÅäÖÃ
        executeConfigInsert(insertSql, "security.max_login_attempts", "5", "INTEGER", "×î´óµÇÂ¼³¢ÊÔ´ÎÊý", false);
        executeConfigInsert(insertSql, "security.session_timeout", "7200", "INTEGER", "»á»°³¬Ê±Ê±¼ä£¨Ãë£©", false);
        
        log.info("Ä¬ÈÏÏµÍ³ÅäÖÃ²åÈëÍê³É");
    }

    /**
     * Ö´ÐÐÅäÖÃ²åÈë
     */
    private void executeConfigInsert(String sql, String key, String value, String type, String description, boolean isPublic) {
        try {
            int rowsAffected = jdbcTemplate.update(sql, key, value, type, description, isPublic ? 1 : 0);
            if (rowsAffected > 0) {
                log.debug("²åÈëÅäÖÃÏî: {} = {}", key, value);
            }
        } catch (DataIntegrityViolationException e) {
            log.debug("ÅäÖÃÏî {} ÒÑ´æÔÚ£¬Ìø¹ý²åÈë", key);
        } catch (Exception e) {
            log.warn("²åÈëÅäÖÃÏî {} Ê§°Ü: {}", key, e.getMessage());
        }
    }

    /**
     * ´´½¨Ä¬ÈÏ»á»°
     */
    private Conversation createDefaultConversation() {
        Conversation conversation = new Conversation();
        conversation.setTitle("»¶Ó­Ê¹ÓÃ DialogAI");
        conversation.setDescription("ÕâÊÇÄúµÄµÚÒ»¸ö¶Ô»°»á»°£¬¿ªÊ¼ÓëAIÖúÊÖÁÄÌì°É£¡");
        conversation.setUserId("default_user");
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * ´´½¨»¶Ó­ÏûÏ¢
     */
    private void createWelcomeMessage(Conversation conversation) {
        Message welcomeMessage = new Message();
        welcomeMessage.setContent(buildWelcomeMessageContent());
        welcomeMessage.setRole(Message.MessageRole.ASSISTANT);
        welcomeMessage.setStatus(Message.MessageStatus.SENT);
        welcomeMessage.setSequenceNumber(1);
        welcomeMessage.setConversation(conversation);
        welcomeMessage.setCreatedAt(LocalDateTime.now());
        
        messageRepository.save(welcomeMessage);
    }

    /**
     * ¹¹½¨»¶Ó­ÏûÏ¢ÄÚÈÝ
     */
    private String buildWelcomeMessageContent() {
        return "ÄúºÃ£¡»¶Ó­Ê¹ÓÃ DialogAI ÖÇÄÜ¶Ô»°ÏµÍ³£¡ÎÒÊÇÄúµÄAIÖúÊÖ£¬ºÜ¸ßÐËÎªÄú·þÎñ¡£\n\n" +
               "ÎÒ¿ÉÒÔ°ïÖúÄú£º\n" +
               "- »Ø´ð¸÷ÖÖÎÊÌâ\n" +
               "- Ð­Öú½â¾öÎÊÌâ\n" +
               "- ½øÐÐ´´ÒâÐ´×÷\n" +
               "- ´úÂë±à³ÌÐ­Öú\n" +
               "- Ñ§Ï°ÖªÊ¶ÌÖÂÛ\n\n" +
               "ÇëËæÊ±ÏòÎÒÌáÎÊ£¬ÎÒ»á¾¡Á¦ÎªÄúÌá¹©ÓÐ°ïÖúµÄ»Ø´ð£¡";
    }

    /**
     * »ñÈ¡ÏµÍ³ÅäÖÃÖµ
     */
    public String getSystemConfig(String key, String defaultValue) {
        try {
            if (!isSystemConfigTableExists()) {
                return defaultValue;
            }
            
            String sql = "SELECT config_value FROM system_configs WHERE config_key = ?";
            String value = jdbcTemplate.queryForObject(sql, String.class, key);
            return value != null ? value : defaultValue;
            
        } catch (Exception e) {
            log.debug("»ñÈ¡ÏµÍ³ÅäÖÃ {} Ê§°Ü: {}", key, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * ÉèÖÃÏµÍ³ÅäÖÃÖµ
     */
    @Transactional
    public boolean setSystemConfig(String key, String value, String type, String description, boolean isPublic) {
        try {
            if (!isSystemConfigTableExists()) {
                log.warn("ÏµÍ³ÅäÖÃ±í²»´æÔÚ£¬ÎÞ·¨ÉèÖÃÅäÖÃ");
                return false;
            }
            
            String upsertSql = "INSERT INTO system_configs " +
                    "(config_key, config_value, config_type, description, is_public, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, NOW(), NOW()) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "config_value = VALUES(config_value), " +
                    "config_type = VALUES(config_type), " +
                    "description = VALUES(description), " +
                    "is_public = VALUES(is_public), " +
                    "updated_at = NOW()";
            
            int rowsAffected = jdbcTemplate.update(upsertSql, key, value, type, description, isPublic ? 1 : 0);
            
            if (rowsAffected > 0) {
                log.debug("ÉèÖÃÏµÍ³ÅäÖÃ: {} = {}", key, value);
                return true;
            }
            
        } catch (Exception e) {
            log.error("ÉèÖÃÏµÍ³ÅäÖÃÊ§°Ü: {} = {}", key, value, e);
        }
        
        return false;
    }

    /**
     * »ñÈ¡ËùÓÐ¹«¿ªµÄÏµÍ³ÅäÖÃ
     */
    public List<SystemConfig> getPublicConfigs() {
        try {
            if (!isSystemConfigTableExists()) {
                return Collections.emptyList();
            }
            
            String sql = "SELECT * FROM system_configs WHERE is_public = 1 ORDER BY config_key";
            
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                SystemConfig config = new SystemConfig();
                config.setId(rs.getLong("id"));
                config.setConfigKey(rs.getString("config_key"));
                config.setConfigValue(rs.getString("config_value"));
                config.setConfigType(SystemConfig.ConfigType.valueOf(rs.getString("config_type")));
                config.setDescription(rs.getString("description"));
                config.setIsPublic(rs.getBoolean("is_public"));
                config.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                config.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return config;
            });
            
        } catch (Exception e) {
            log.error("»ñÈ¡¹«¿ªÏµÍ³ÅäÖÃÊ§°Ü", e);
            return Collections.emptyList();
        }
    }
} 