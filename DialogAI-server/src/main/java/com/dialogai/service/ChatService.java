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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
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
     * 处理流式AI对话请求，异步推送AI回复到SseEmitter
     */
    @Async
    public void processStreamingChatRequest(ChatRequestDto request, SseEmitter emitter) {
        try {
            log.info("[流式请求] 收到请求: userId={}, message={}, conversationId={}", request.getUserId(), request.getMessage(), request.getConversationId());
            // 1. 检查/创建会话
            Long conversationId = request.getConversationId();
            if (conversationId == null) {
                ConversationDto conv = conversationService.createConversation("新对话", "", request.getUserId());
                conversationId = conv.getId();
                log.info("[流式请求] 新建会话: conversationId={}", conversationId);
            } else {
                log.info("[流式请求] 使用已有会话: conversationId={}", conversationId);
            }

            // 2. 保存用户消息
            messageService.saveUserMessage(conversationId, request.getMessage());
            log.info("[流式请求] 已保存用户消息: conversationId={}, message={}", conversationId, request.getMessage());

            // 3. 流式获取AI回复
            StringBuilder aiReply = new StringBuilder();
            Consumer<String> streamCallback = chunk -> {
                try {
                    aiReply.append(chunk);
                    // 使用正确的SSE格式并强制flush
                    emitter.send(SseEmitter.event()
                        .name("chunk")
                        .data(chunk)
                        .id(String.valueOf(System.currentTimeMillis())));
                    log.info("[流式请求] 推送AI回复分片: {}", chunk);
                } catch (IOException e) {
                    log.error("[流式请求] 推送AI分片出错", e);
                    emitter.completeWithError(e);
                }
            };

            // 4. 调用DeepSeekService的流式方法
            deepSeekService.streamChat(request, streamCallback);
            log.info("[流式请求] AI回复流式推送完成: conversationId={}", conversationId);

            // 5. 保存AI回复消息
            messageService.saveAiMessage(conversationId, aiReply.toString());
            log.info("[流式请求] 已保存AI消息: conversationId={}, aiReply={}", conversationId, aiReply);

            // 6. 可选：更新会话描述和更新时间
            // conversationService.updateDescription(conversationId, aiReply.toString());

            // 7. 结束推送
            emitter.complete();
            log.info("[流式请求] SSE推送完成: conversationId={}", conversationId);
        } catch (Exception e) {
            log.error("[流式请求] 处理异常", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 处理流式AI对话请求，直接写入PrintWriter确保实时性
     */
    public void processStreamingChatRequestDirect(ChatRequestDto request, PrintWriter writer) {
        try {
            log.info("[流式请求] 收到请求: userId={}, message={}, conversationId={}", request.getUserId(), request.getMessage(), request.getConversationId());
            
            // 1. 检查/创建会话
            Long conversationId = request.getConversationId();
            if (conversationId == null) {
                ConversationDto conv = conversationService.createConversation("新对话", "", request.getUserId());
                conversationId = conv.getId();
                log.info("[流式请求] 新建会话: conversationId={}", conversationId);
            } else {
                log.info("[流式请求] 使用已有会话: conversationId={}", conversationId);
            }

            // 2. 保存用户消息
            messageService.saveUserMessage(conversationId, request.getMessage());
            log.info("[流式请求] 已保存用户消息: conversationId={}, message={}", conversationId, request.getMessage());

            // 3. 流式获取AI回复
            StringBuilder aiReply = new StringBuilder();
            Consumer<String> streamCallback = chunk -> {
                try {
                    aiReply.append(chunk);
                    // 直接写入并立即flush
                    writer.write("data: " + chunk + "\n\n");
                    writer.flush();
                    log.info("[流式请求] 直接推送AI回复分片: {}", chunk);
                } catch (Exception e) {
                    log.error("[流式请求] 推送AI分片出错", e);
                }
            };

            // 4. 调用DeepSeekService的流式方法
            deepSeekService.streamChat(request, streamCallback);
            log.info("[流式请求] AI回复流式推送完成: conversationId={}", conversationId);

            // 5. 保存AI回复消息
            messageService.saveAiMessage(conversationId, aiReply.toString());
            log.info("[流式请求] 已保存AI消息: conversationId={}, aiReply长度={}", conversationId, aiReply.length());

            log.info("[流式请求] 直接流式处理完成: conversationId={}", conversationId);
        } catch (Exception e) {
            log.error("[流式请求] 处理异常", e);
            try {
                writer.write("data: 处理出错: " + e.getMessage() + "\n\n");
                writer.flush();
            } catch (Exception ex) {
                log.error("写入错误消息失败", ex);
            }
        }
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