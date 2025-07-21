package com.dialogai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API配置
 * 
 * @author DialogAI
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekConfig {

    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout;
} 