package com.dialogai.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 聊天请求数据传输对象
 * 
 * @author DialogAI
 */
@Data
public class ChatRequestDto {

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 会话ID（可选，如果不提供则创建新会话）
     */
    private Long conversationId;

    /**
     * 用户ID（预留字段）
     */
    private String userId;
} 