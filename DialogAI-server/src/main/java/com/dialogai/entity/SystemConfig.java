package com.dialogai.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 
 * @author DialogAI
 * @version 1.0
 */
@Entity
@Table(name = "system_configs")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 配置键
     */
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    /**
     * 配置值
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    /**
     * 配置类型：STRING-字符串，INTEGER-整数，BOOLEAN-布尔值，JSON-JSON对象
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false, length = 20)
    private ConfigType configType = ConfigType.STRING;

    /**
     * 配置描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 是否公开：false-私有，true-公开
     */
    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic = false;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 配置类型枚举
     */
    public enum ConfigType {
        STRING,     // 字符串
        INTEGER,    // 整数
        BOOLEAN,    // 布尔值
        JSON        // JSON对象
    }

    /**
     * 获取配置值作为字符串
     */
    public String getStringValue() {
        return configValue;
    }

    /**
     * 获取配置值作为整数
     */
    public Integer getIntegerValue() {
        if (configValue == null) {
            return null;
        }
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取配置值作为布尔值
     */
    public Boolean getBooleanValue() {
        if (configValue == null) {
            return null;
        }
        return Boolean.parseBoolean(configValue);
    }

    /**
     * 设置配置值
     */
    public void setStringValue(String value) {
        this.configValue = value;
        this.configType = ConfigType.STRING;
    }

    /**
     * 设置配置值
     */
    public void setIntegerValue(Integer value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.INTEGER;
    }

    /**
     * 设置配置值
     */
    public void setBooleanValue(Boolean value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.BOOLEAN;
    }
} 