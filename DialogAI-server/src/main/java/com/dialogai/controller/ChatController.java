package com.dialogai.controller;

import com.dialogai.dto.ChatRequestDto;
import com.dialogai.dto.ChatResponseDto;
import com.dialogai.dto.ConversationDto;
import com.dialogai.service.ChatService;
import com.dialogai.service.ConversationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

/**
 * 聊天控制器
 * 
 * @author DialogAI
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ConversationService conversationService;

    /**
     * 发送聊天消息
     */
    @PostMapping("/send")
    public ResponseEntity<ChatResponseDto> sendMessage(@Valid @RequestBody ChatRequestDto request) {
        log.info("[接口入口] POST /chat/send - 收到聊天请求: userId={}, message={}, conversationId={}", 
                request.getUserId(), request.getMessage(), request.getConversationId());
        try {
            ChatResponseDto response = chatService.processChatRequest(request);
            log.info("[接口出口] POST /chat/send - 处理成功: conversationId={}, success={}", 
                    response.getConversationId(), response.isSuccess());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[接口异常] POST /chat/send - 处理失败: userId={}, message={}", 
                    request.getUserId(), request.getMessage(), e);
            throw e;
        }
    }

    /**
     * AI流式对话接口 - 使用直接响应写入确保实时性
     */
    @PostMapping(value = "/stream-send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void streamSendMessage(@Valid @RequestBody ChatRequestDto request, 
                                javax.servlet.http.HttpServletResponse response) throws IOException {
        log.info("[接口入口] POST /chat/stream-send - 收到流式聊天请求: userId={}, message={}, conversationId={}", 
                request.getUserId(), request.getMessage(), request.getConversationId());
        
        // 设置响应头
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter writer = response.getWriter();
        
        try {
            // 立即发送连接确认
            writer.write("data: 连接已建立\n\n");
            writer.flush();
            log.info("[调试] 已发送连接确认并flush");
            
            // 处理流式请求
            chatService.processStreamingChatRequestDirect(request, writer);
            
        } catch (Exception e) {
            log.error("[接口异常] POST /chat/stream-send - 处理失败", e);
            writer.write("data: 处理失败: " + e.getMessage() + "\n\n");
            writer.flush();
        } finally {
            try {
                writer.write("data: [DONE]\n\n");
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log.error("关闭writer失败", e);
            }
        }
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] GET /chat/conversations - 获取会话列表: userId={}", userId);
        try {
            List<ConversationDto> conversations = conversationService.getConversationsByUserId(userId);
            log.info("[接口出口] GET /chat/conversations - 获取成功: userId={}, 会话数量={}", 
                    userId, conversations.size());
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("[接口异常] GET /chat/conversations - 获取失败: userId={}", userId, e);
            throw e;
        }
    }

    /**
     * 分页获取会话列表
     */
    @GetMapping("/conversations/page")
    public ResponseEntity<Page<ConversationDto>> getConversationsPage(
            @RequestParam(defaultValue = "default-user") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[接口入口] GET /chat/conversations/page - 分页获取会话列表: userId={}, page={}, size={}", 
                userId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ConversationDto> conversations = conversationService.getConversationsByUserId(userId, pageable);
            log.info("[接口出口] GET /chat/conversations/page - 获取成功: userId={}, page={}, size={}, 总数={}, 当前页数量={}", 
                    userId, page, size, conversations.getTotalElements(), conversations.getContent().size());
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("[接口异常] GET /chat/conversations/page - 获取失败: userId={}, page={}, size={}", 
                    userId, page, size, e);
            throw e;
        }
    }

    /**
     * 获取会话详情（包含消息）
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDto> getConversationWithMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] GET /chat/conversations/{conversationId} - 获取会话详情: conversationId={}, userId={}", 
                conversationId, userId);
        try {
            ConversationDto conversation = chatService.getConversationWithMessages(conversationId, userId);
            log.info("[接口出口] GET /chat/conversations/{conversationId} - 获取成功: conversationId={}, userId={}, 消息数量={}", 
                    conversationId, userId, conversation.getMessages() != null ? conversation.getMessages().size() : 0);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("[接口异常] GET /chat/conversations/{conversationId} - 获取失败: conversationId={}, userId={}", 
                    conversationId, userId, e);
            throw e;
        }
    }

    /**
     * 创建新会话
     */
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDto> createConversation(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] POST /chat/conversations - 创建新会话: userId={}, title={}, description={}", 
                userId, title, description);
        try {
            ConversationDto conversation = conversationService.createConversation(title, description, userId);
            log.info("[接口出口] POST /chat/conversations - 创建成功: userId={}, conversationId={}, title={}", 
                    userId, conversation.getId(), conversation.getTitle());
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("[接口异常] POST /chat/conversations - 创建失败: userId={}, title={}", 
                    userId, title, e);
            throw e;
        }
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/conversations/{conversationId}/title")
    public ResponseEntity<ConversationDto> updateConversationTitle(
            @PathVariable Long conversationId,
            @RequestParam String title,
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] PUT /chat/conversations/{conversationId}/title - 更新会话标题: conversationId={}, userId={}, title={}", 
                conversationId, userId, title);
        try {
            return conversationService.updateConversationTitle(conversationId, title, userId)
                    .map(conversation -> {
                        log.info("[接口出口] PUT /chat/conversations/{conversationId}/title - 更新成功: conversationId={}, userId={}, title={}", 
                                conversationId, userId, title);
                        return ResponseEntity.ok(conversation);
                    })
                    .orElseGet(() -> {
                        log.warn("[接口出口] PUT /chat/conversations/{conversationId}/title - 会话不存在: conversationId={}, userId={}", 
                                conversationId, userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("[接口异常] PUT /chat/conversations/{conversationId}/title - 更新失败: conversationId={}, userId={}, title={}", 
                    conversationId, userId, title, e);
            throw e;
        }
    }

    /**
     * 归档会话
     */
    @PutMapping("/conversations/{conversationId}/archive")
    public ResponseEntity<ConversationDto> archiveConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] PUT /chat/conversations/{conversationId}/archive - 归档会话: conversationId={}, userId={}", 
                conversationId, userId);
        try {
            return conversationService.archiveConversation(conversationId, userId)
                    .map(conversation -> {
                        log.info("[接口出口] PUT /chat/conversations/{conversationId}/archive - 归档成功: conversationId={}, userId={}", 
                                conversationId, userId);
                        return ResponseEntity.ok(conversation);
                    })
                    .orElseGet(() -> {
                        log.warn("[接口出口] PUT /chat/conversations/{conversationId}/archive - 会话不存在: conversationId={}, userId={}", 
                                conversationId, userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("[接口异常] PUT /chat/conversations/{conversationId}/archive - 归档失败: conversationId={}, userId={}", 
                    conversationId, userId, e);
            throw e;
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDto> deleteConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        log.info("[接口入口] DELETE /chat/conversations/{conversationId} - 删除会话: conversationId={}, userId={}", 
                conversationId, userId);
        try {
            return conversationService.deleteConversation(conversationId, userId)
                    .map(conversation -> {
                        log.info("[接口出口] DELETE /chat/conversations/{conversationId} - 删除成功: conversationId={}, userId={}", 
                                conversationId, userId);
                        return ResponseEntity.ok(conversation);
                    })
                    .orElseGet(() -> {
                        log.warn("[接口出口] DELETE /chat/conversations/{conversationId} - 会话不存在: conversationId={}, userId={}", 
                                conversationId, userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("[接口异常] DELETE /chat/conversations/{conversationId} - 删除失败: conversationId={}, userId={}", 
                    conversationId, userId, e);
            throw e;
        }
    }

    /**
     * 搜索会话
     */
    @GetMapping("/conversations/search")
    public ResponseEntity<List<ConversationDto>> searchConversations(@RequestParam String keyword) {
        log.info("[接口入口] GET /chat/conversations/search - 搜索会话: keyword={}", keyword);
        try {
            List<ConversationDto> conversations = conversationService.searchConversations(keyword);
            log.info("[接口出口] GET /chat/conversations/search - 搜索成功: keyword={}, 结果数量={}", 
                    keyword, conversations.size());
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("[接口异常] GET /chat/conversations/search - 搜索失败: keyword={}", keyword, e);
            throw e;
        }
    }
} 