package com.dialogai.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天响应数据传输对象
 * 
 * @author DialogAI
 */
@Data
public class ChatResponseDto {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * AI回复内容
     */
    private String response;

    /**
     * 用户消息
     */
    private MessageDto userMessage;

    /**
     * AI消息
     */
    private MessageDto aiMessage;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
} 