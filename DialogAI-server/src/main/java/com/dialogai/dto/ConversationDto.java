package com.dialogai.dto;

import com.dialogai.entity.Conversation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话数据传输对象
 * 
 * @author DialogAI
 */
@Data
public class ConversationDto {

    private Long id;
    private String title;
    private String description;
    private String userId;
    private Conversation.ConversationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageDto> messages;
    private Integer messageCount;

    /**
     * 从实体转换为DTO
     */
    public static ConversationDto fromEntity(Conversation conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setDescription(conversation.getDescription());
        dto.setUserId(conversation.getUserId());
        dto.setStatus(conversation.getStatus());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        // 移除直接访问懒加载集合，messageCount将在需要时单独设置
        dto.setMessageCount(0);
        return dto;
    }

    /**
     * 从实体转换为DTO（包含消息数量）
     */
    public static ConversationDto fromEntityWithMessageCount(Conversation conversation, Integer messageCount) {
        ConversationDto dto = fromEntity(conversation);
        dto.setMessageCount(messageCount != null ? messageCount : 0);
        return dto;
    }
} 