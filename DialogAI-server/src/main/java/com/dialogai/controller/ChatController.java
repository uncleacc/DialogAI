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
        log.info("收到聊天请求: {}", request.getMessage());
        ChatResponseDto response = chatService.processChatRequest(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(
            @RequestParam(defaultValue = "default-user") String userId) {
        List<ConversationDto> conversations = conversationService.getConversationsByUserId(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * 分页获取会话列表
     */
    @GetMapping("/conversations/page")
    public ResponseEntity<Page<ConversationDto>> getConversationsPage(
            @RequestParam(defaultValue = "default-user") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByUserId(userId, pageable);
        return ResponseEntity.ok(conversations);
    }

    /**
     * 获取会话详情（包含消息）
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDto> getConversationWithMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        ConversationDto conversation = chatService.getConversationWithMessages(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * 创建新会话
     */
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDto> createConversation(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "default-user") String userId) {
        ConversationDto conversation = conversationService.createConversation(title, description, userId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/conversations/{conversationId}/title")
    public ResponseEntity<ConversationDto> updateConversationTitle(
            @PathVariable Long conversationId,
            @RequestParam String title,
            @RequestParam(defaultValue = "default-user") String userId) {
        return conversationService.updateConversationTitle(conversationId, title, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 归档会话
     */
    @PutMapping("/conversations/{conversationId}/archive")
    public ResponseEntity<ConversationDto> archiveConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        return conversationService.archiveConversation(conversationId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDto> deleteConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        return conversationService.deleteConversation(conversationId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜索会话
     */
    @GetMapping("/conversations/search")
    public ResponseEntity<List<ConversationDto>> searchConversations(@RequestParam String keyword) {
        List<ConversationDto> conversations = conversationService.searchConversations(keyword);
        return ResponseEntity.ok(conversations);
    }

    /**
     * 清空会话消息
     */
    @DeleteMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Void> clearConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "default-user") String userId) {
        boolean success = chatService.clearConversationMessages(conversationId, userId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
} 