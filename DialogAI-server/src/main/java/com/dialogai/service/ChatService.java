package com.dialogai.service;

import com.dialogai.dto.ChatRequestDto;
import com.dialogai.dto.ChatResponseDto;
import com.dialogai.dto.ConversationDto;
import com.dialogai.dto.MessageDto;
import com.dialogai.entity.Conversation;
import com.dialogai.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天服务 - 整合会话、消息和AI服务
 * 
 * @author DialogAI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final DeepSeekService deepSeekService;

    /**
     * 处理聊天请求
     */
    @Transactional
    @CacheEvict(value = {"conversations", "messages"}, allEntries = true)
    public ChatResponseDto processChatRequest(ChatRequestDto request) {
        ChatResponseDto response = new ChatResponseDto();
        response.setResponseTime(LocalDateTime.now());

        try {
            // 1. 获取或创建会话
            Long conversationId = request.getConversationId();
            if (conversationId == null) {
                // 创建新会话
                ConversationDto newConversation = conversationService.createConversation(
                    "新对话", 
                    "用户: " + request.getMessage().substring(0, Math.min(50, request.getMessage().length())),
                    request.getUserId()
                );
                conversationId = newConversation.getId();
                response.setConversationId(conversationId);
            } else {
                // 验证会话是否存在且属于当前用户
                if (!conversationService.existsByIdAndUserId(conversationId, request.getUserId())) {
                    throw new RuntimeException("会话不存在或无权限访问");
                }
                response.setConversationId(conversationId);
            }

            // 2. 保存用户消息
            MessageDto userMessage = messageService.saveUserMessage(conversationId, request.getMessage());
            response.setUserMessage(userMessage);

            // 3. 获取会话历史
            List<Message> conversationHistory = getConversationHistory(conversationId);

            // 4. 调用AI服务获取回复
            String aiResponse = deepSeekService.sendMessage(request.getMessage(), conversationHistory);

            // 5. 保存AI回复
            MessageDto aiMessage = messageService.saveAiMessage(conversationId, aiResponse);
            response.setAiMessage(aiMessage);
            response.setResponse(aiResponse);
            response.setSuccess(true);

            log.info("处理聊天请求成功，会话ID: {}, 用户消息: {}", conversationId, request.getMessage().substring(0, Math.min(20, request.getMessage().length())));

        } catch (Exception e) {
            log.error("处理聊天请求失败", e);
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    /**
     * 获取会话历史（用于AI上下文）
     */
    private List<Message> getConversationHistory(Long conversationId) {
        // 获取最近的20条消息作为上下文
        List<MessageDto> recentMessages = messageService.getRecentMessages(conversationId, 20);
        
        // 转换为Message实体（这里简化处理，实际应该从数据库直接查询）
        return recentMessages.stream()
                .map(this::convertToMessageEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将MessageDto转换为Message实体（简化实现）
     */
    private Message convertToMessageEntity(MessageDto dto) {
        Message message = new Message();
        message.setId(dto.getId());
        message.setContent(dto.getContent());
        message.setRole(dto.getRole());
        message.setSequenceNumber(dto.getSequenceNumber());
        message.setCreatedAt(dto.getCreatedAt());
        
        // 设置会话（简化处理）
        Conversation conversation = new Conversation();
        conversation.setId(dto.getConversationId());
        message.setConversation(conversation);
        
        return message;
    }

    /**
     * 获取会话的完整信息（包括消息）
     */
    public ConversationDto getConversationWithMessages(Long conversationId, String userId) {
        // 验证权限
        if (!conversationService.existsByIdAndUserId(conversationId, userId)) {
            throw new RuntimeException("会话不存在或无权限访问");
        }

        // 获取会话信息
        ConversationDto conversation = conversationService.getConversationById(conversationId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        // 获取消息列表
        List<MessageDto> messages = messageService.getMessagesByConversationId(conversationId);
        conversation.setMessages(messages);

        return conversation;
    }

    /**
     * 清空会话消息
     */
    @Transactional
    @CacheEvict(value = {"conversations", "messages"}, allEntries = true)
    public boolean clearConversationMessages(Long conversationId, String userId) {
        // 验证权限
        if (!conversationService.existsByIdAndUserId(conversationId, userId)) {
            throw new RuntimeException("会话不存在或无权限访问");
        }

        // 获取所有消息并删除
        List<MessageDto> messages = messageService.getMessagesByConversationId(conversationId);
        for (MessageDto message : messages) {
            messageService.deleteMessage(message.getId(), conversationId);
        }

        log.info("清空会话消息，会话ID: {}", conversationId);
        return true;
    }
} 