package com.dialogai.service;

import com.dialogai.dto.ConversationDto;
import com.dialogai.entity.Conversation;
import com.dialogai.entity.Message;
import com.dialogai.repository.ConversationRepository;
import com.dialogai.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会话服务
 * 
 * @author DialogAI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * 创建新会话
     */
    @Transactional
    public ConversationDto createConversation(String title, String description, String userId) {
        Conversation conversation = new Conversation();
        conversation.setTitle(title);
        conversation.setDescription(description);
        conversation.setUserId(userId);
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        Conversation savedConversation = conversationRepository.save(conversation);
        log.info("创建新会话: {}", savedConversation.getId());
        
        return ConversationDto.fromEntity(savedConversation);
    }

    /**
     * 根据ID获取会话
     */
    // @Cacheable(value = "conversations", key = "#id")
    public Optional<ConversationDto> getConversationById(Long id) {
        return conversationRepository.findById(id)
                .map(ConversationDto::fromEntity);
    }

    /**
     * 根据ID和用户ID获取会话
     */
    public Optional<ConversationDto> getConversationByIdAndUserId(Long id, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId)
                .map(ConversationDto::fromEntity);
    }

    /**
     * 获取用户的所有会话
     */
    public List<ConversationDto> getConversationsByUserId(String userId) {
        return conversationRepository.findByUserIdAndStatusNotOrderByUpdatedAtDesc(userId, Conversation.ConversationStatus.DELETED)
                .stream()
                .map(ConversationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取用户的会话
     */
    public Page<ConversationDto> getConversationsByUserId(String userId, Pageable pageable) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(ConversationDto::fromEntity);
    }

    /**
     * 更新会话标题
     */
    @Transactional
    // @CacheEvict(value = "conversations", key = "#id")
    public Optional<ConversationDto> updateConversationTitle(Long id, String title, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId)
                .map(conversation -> {
                    conversation.setTitle(title);
                    conversation.setUpdatedAt(LocalDateTime.now());
                    Conversation savedConversation = conversationRepository.save(conversation);
                    log.info("更新会话标题: {}", id);
                    return ConversationDto.fromEntity(savedConversation);
                });
    }

    /**
     * 归档会话
     */
    @Transactional
    // @CacheEvict(value = "conversations", key = "#id")
    public Optional<ConversationDto> archiveConversation(Long id, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId)
                .map(conversation -> {
                    conversation.setStatus(Conversation.ConversationStatus.ARCHIVED);
                    conversation.setUpdatedAt(LocalDateTime.now());
                    Conversation savedConversation = conversationRepository.save(conversation);
                    log.info("归档会话: {}", id);
                    return ConversationDto.fromEntity(savedConversation);
                });
    }

    /**
     * 删除会话（软删除）
     */
    @Transactional
    // @CacheEvict(value = "conversations", key = "#id")
    public Optional<ConversationDto> deleteConversation(Long id, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId)
                .map(conversation -> {
                    conversation.setStatus(Conversation.ConversationStatus.DELETED);
                    conversation.setUpdatedAt(LocalDateTime.now());
                    Conversation savedConversation = conversationRepository.save(conversation);
                    log.info("删除会话: {}", id);
                    return ConversationDto.fromEntity(savedConversation);
                });
    }

    /**
     * 搜索会话
     */
    public List<ConversationDto> searchConversations(String keyword) {
        return conversationRepository.findByTitleContaining(keyword)
                .stream()
                .map(ConversationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取会话的消息数量
     */
    public long getMessageCount(Long conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    /**
     * 检查会话是否存在且属于指定用户
     */
    public boolean existsByIdAndUserId(Long id, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId).isPresent();
    }
} 