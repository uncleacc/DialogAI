package com.dialogai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AI对话系统主启动类
 * 
 * @author DialogAI
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class DialogAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DialogAiApplication.class, args);
    }
} 