package com.dialogai.dto;

import com.dialogai.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息数据传输对象
 * 
 * @author DialogAI
 */
@Data
public class MessageDto {

    private Long id;
    private String content;
    private Message.MessageRole role;
    private Message.MessageStatus status;
    private Integer sequenceNumber;
    private Long conversationId;
    private LocalDateTime createdAt;

    /**
     * 从实体转换为DTO
     */
    public static MessageDto fromEntity(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setRole(message.getRole());
        dto.setStatus(message.getStatus());
        dto.setSequenceNumber(message.getSequenceNumber());
        dto.setConversationId(message.getConversation().getId());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
} 