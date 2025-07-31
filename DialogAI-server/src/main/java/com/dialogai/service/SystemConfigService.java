package com.dialogai.service;

import com.dialogai.entity.SystemConfig;
import com.dialogai.repository.SystemConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ÏµÍ³ÅäÖÃ¹ÜÀí·þÎñ
 * Ìá¹©ÏµÍ³ÅäÖÃµÄÔöÉ¾¸Ä²é¹¦ÄÜ
 * 
 * @author DialogAI
 * @version 1.0
 */
@Slf4j
@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    @Autowired
    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * »ñÈ¡ÅäÖÃÖµ£¨×Ö·û´®ÀàÐÍ£©
     */
    @Cacheable(value = "systemConfig", key = "#configKey")
    public String getString(String configKey, String defaultValue) {
        try {
            Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
            return config.map(SystemConfig::getStringValue).orElse(defaultValue);
        } catch (Exception e) {
            log.warn("»ñÈ¡ÅäÖÃ {} Ê§°Ü: {}", configKey, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * »ñÈ¡ÅäÖÃÖµ£¨ÕûÊýÀàÐÍ£©
     */
    @Cacheable(value = "systemConfig", key = "#configKey")
    public Integer getInteger(String configKey, Integer defaultValue) {
        try {
            Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
            if (config.isPresent()) {
                Integer value = config.get().getIntegerValue();
                return value != null ? value : defaultValue;
            }
            return defaultValue;
        } catch (Exception e) {
            log.warn("»ñÈ¡ÅäÖÃ {} Ê§°Ü: {}", configKey, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * »ñÈ¡ÅäÖÃÖµ£¨²¼¶ûÀàÐÍ£©
     */
    @Cacheable(value = "systemConfig", key = "#configKey")
    public Boolean getBoolean(String configKey, Boolean defaultValue) {
        try {
            Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
            if (config.isPresent()) {
                Boolean value = config.get().getBooleanValue();
                return value != null ? value : defaultValue;
            }
            return defaultValue;
        } catch (Exception e) {
            log.warn("»ñÈ¡ÅäÖÃ {} Ê§°Ü: {}", configKey, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * ÉèÖÃ×Ö·û´®ÅäÖÃ
     */
    @Transactional
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean setString(String configKey, String value, String description, boolean isPublic) {
        return saveOrUpdateConfig(configKey, value, SystemConfig.ConfigType.STRING, description, isPublic);
    }

    /**
     * ÉèÖÃÕûÊýÅäÖÃ
     */
    @Transactional
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean setInteger(String configKey, Integer value, String description, boolean isPublic) {
        String stringValue = value != null ? value.toString() : null;
        return saveOrUpdateConfig(configKey, stringValue, SystemConfig.ConfigType.INTEGER, description, isPublic);
    }

    /**
     * ÉèÖÃ²¼¶ûÅäÖÃ
     */
    @Transactional
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean setBoolean(String configKey, Boolean value, String description, boolean isPublic) {
        String stringValue = value != null ? value.toString() : null;
        return saveOrUpdateConfig(configKey, stringValue, SystemConfig.ConfigType.BOOLEAN, description, isPublic);
    }

    /**
     * ±£´æ»ò¸üÐÂÅäÖÃ
     */
    private boolean saveOrUpdateConfig(String configKey, String configValue, 
                                     SystemConfig.ConfigType configType, 
                                     String description, boolean isPublic) {
        try {
            Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(configKey);
            
            SystemConfig config;
            if (existingConfig.isPresent()) {
                // ¸üÐÂÏÖÓÐÅäÖÃ
                config = existingConfig.get();
                config.setConfigValue(configValue);
                config.setConfigType(configType);
                config.setDescription(description);
                config.setIsPublic(isPublic);
                config.setUpdatedAt(LocalDateTime.now());
                
                log.debug("¸üÐÂÅäÖÃ: {} = {}", configKey, configValue);
            } else {
                // ´´½¨ÐÂÅäÖÃ
                config = new SystemConfig();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                config.setConfigType(configType);
                config.setDescription(description);
                config.setIsPublic(isPublic);
                config.setCreatedAt(LocalDateTime.now());
                config.setUpdatedAt(LocalDateTime.now());
                
                log.debug("´´½¨ÅäÖÃ: {} = {}", configKey, configValue);
            }
            
            systemConfigRepository.save(config);
            return true;
            
        } catch (Exception e) {
            log.error("±£´æÅäÖÃÊ§°Ü: {} = {}", configKey, configValue, e);
            return false;
        }
    }

    /**
     * É¾³ýÅäÖÃ
     */
    @Transactional
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean deleteConfig(String configKey) {
        try {
            Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
            if (config.isPresent()) {
                systemConfigRepository.delete(config.get());
                log.info("É¾³ýÅäÖÃ: {}", configKey);
                return true;
            } else {
                log.warn("ÅäÖÃ²»´æÔÚ: {}", configKey);
                return false;
            }
        } catch (Exception e) {
            log.error("É¾³ýÅäÖÃÊ§°Ü: {}", configKey, e);
            return false;
        }
    }

    /**
     * »ñÈ¡ËùÓÐ¹«¿ªµÄÅäÖÃ
     */
    public List<SystemConfig> getPublicConfigs() {
        try {
            return systemConfigRepository.findByIsPublicTrueOrderByConfigKey();
        } catch (Exception e) {
            log.error("»ñÈ¡¹«¿ªÅäÖÃÊ§°Ü", e);
            return Collections.emptyList();
        }
    }

    /**
     * »ñÈ¡¹«¿ªÅäÖÃµÄMapÐÎÊ½
     */
    public Map<String, String> getPublicConfigsAsMap() {
        return getPublicConfigs().stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        config -> config.getConfigValue() != null ? config.getConfigValue() : "",
                        (existing, replacement) -> existing
                ));
    }

    /**
     * ¸ù¾ÝÇ°×º»ñÈ¡ÅäÖÃ
     */
    public List<SystemConfig> getConfigsByPrefix(String prefix) {
        try {
            return systemConfigRepository.findByConfigKeyStartingWith(prefix);
        } catch (Exception e) {
            log.error("¸ù¾ÝÇ°×º»ñÈ¡ÅäÖÃÊ§°Ü: {}", prefix, e);
            return Collections.emptyList();
        }
    }

    /**
     * ¸ù¾ÝÇ°×º»ñÈ¡¹«¿ªÅäÖÃ
     */
    public List<SystemConfig> getPublicConfigsByPrefix(String prefix) {
        try {
            return systemConfigRepository.findPublicConfigsByPrefix(prefix);
        } catch (Exception e) {
            log.error("¸ù¾ÝÇ°×º»ñÈ¡¹«¿ªÅäÖÃÊ§°Ü: {}", prefix, e);
            return Collections.emptyList();
        }
    }

    /**
     * ÅúÁ¿»ñÈ¡ÅäÖÃ
     */
    public Map<String, String> getConfigs(List<String> configKeys) {
        try {
            List<SystemConfig> configs = systemConfigRepository.findByConfigKeyIn(configKeys);
            return configs.stream()
                    .collect(Collectors.toMap(
                            SystemConfig::getConfigKey,
                            config -> config.getConfigValue() != null ? config.getConfigValue() : "",
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("ÅúÁ¿»ñÈ¡ÅäÖÃÊ§°Ü", e);
            return Collections.emptyMap();
        }
    }

    /**
     * ¼ì²éÅäÖÃÊÇ·ñ´æÔÚ
     */
    public boolean exists(String configKey) {
        try {
            return systemConfigRepository.existsByConfigKey(configKey);
        } catch (Exception e) {
            log.error("¼ì²éÅäÖÃÊÇ·ñ´æÔÚÊ§°Ü: {}", configKey, e);
            return false;
        }
    }

    /**
     * »ñÈ¡ÅäÖÃÏêÇé
     */
    public Optional<SystemConfig> getConfigDetail(String configKey) {
        try {
            return systemConfigRepository.findByConfigKey(configKey);
        } catch (Exception e) {
            log.error("»ñÈ¡ÅäÖÃÏêÇéÊ§°Ü: {}", configKey, e);
            return Optional.empty();
        }
    }

    /**
     * Çå¿Õ»º´æ
     */
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void clearCache() {
        log.info("Çå¿ÕÏµÍ³ÅäÖÃ»º´æ");
    }

    /**
     * »ñÈ¡ÏµÍ³ºËÐÄÅäÖÃ
     */
    public Map<String, String> getCoreConfigs() {
        List<String> coreKeys = Arrays.asList(
                "system.version",
                "system.name", 
                "system.description",
                "chat.max_message_length",
                "ai.default_model",
                "ui.theme",
                "ui.language"
        );
        return getConfigs(coreKeys);
    }

    /**
     * »ñÈ¡ÁÄÌìÏà¹ØÅäÖÃ
     */
    public Map<String, String> getChatConfigs() {
        return getPublicConfigsByPrefix("chat.").stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        config -> config.getConfigValue() != null ? config.getConfigValue() : ""
                ));
    }

    /**
     * »ñÈ¡UIÏà¹ØÅäÖÃ
     */
    public Map<String, String> getUiConfigs() {
        return getPublicConfigsByPrefix("ui.").stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        config -> config.getConfigValue() != null ? config.getConfigValue() : ""
                ));
    }

    /**
     * »ñÈ¡¹¦ÄÜ¿ª¹ØÅäÖÃ
     */
    public Map<String, String> getFeatureConfigs() {
        return getPublicConfigsByPrefix("feature.").stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        config -> config.getConfigValue() != null ? config.getConfigValue() : ""
                ));
    }
} 