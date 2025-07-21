package com.dialogai.service;

import com.dialogai.dto.MessageDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息服务
 * 
 * @author DialogAI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    /**
     * 保存用户消息
     */
    @Transactional
    public MessageDto saveUserMessage(Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        // 获取下一个序号
        Integer nextSequence = getNextSequenceNumber(conversationId);

        Message message = new Message();
        message.setContent(content);
        message.setRole(Message.MessageRole.USER);
        message.setConversation(conversation);
        message.setSequenceNumber(nextSequence);

        Message savedMessage = messageRepository.save(message);
        log.info("保存用户消息: {}", savedMessage.getId());

        return MessageDto.fromEntity(savedMessage);
    }

    /**
     * 保存AI回复消息
     */
    @Transactional
    public MessageDto saveAiMessage(Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        // 获取下一个序号
        Integer nextSequence = getNextSequenceNumber(conversationId);

        Message message = new Message();
        message.setContent(content);
        message.setRole(Message.MessageRole.ASSISTANT);
        message.setConversation(conversation);
        message.setSequenceNumber(nextSequence);

        Message savedMessage = messageRepository.save(message);
        log.info("保存AI消息: {}", savedMessage.getId());

        return MessageDto.fromEntity(savedMessage);
    }

    /**
     * 获取会话的所有消息
     */
    @Cacheable(value = "messages", key = "#conversationId")
    public List<MessageDto> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationIdOrderBySequenceNumberAsc(conversationId)
                .stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取会话的消息
     */
    public Page<MessageDto> getMessagesByConversationId(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdOrderBySequenceNumberAsc(conversationId, pageable)
                .map(MessageDto::fromEntity);
    }

    /**
     * 获取会话的最近N条消息
     */
    public List<MessageDto> getRecentMessages(Long conversationId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return messageRepository.findRecentMessagesByConversationId(conversationId, pageable)
                .stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取消息
     */
    public Optional<MessageDto> getMessageById(Long id) {
        return messageRepository.findById(id)
                .map(MessageDto::fromEntity);
    }

    /**
     * 删除消息
     */
    @Transactional
    @CacheEvict(value = "messages", key = "#conversationId")
    public boolean deleteMessage(Long id, Long conversationId) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            if (message.getConversation().getId().equals(conversationId)) {
                messageRepository.delete(message);
                log.info("删除消息: {}", id);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取会话中最大的序号
     */
    private Integer getNextSequenceNumber(Long conversationId) {
        Optional<Integer> maxSequence = messageRepository.findMaxSequenceNumberByConversationId(conversationId);
        return maxSequence.map(sequence -> sequence + 1).orElse(1);
    }

    /**
     * 获取会话的消息数量
     */
    public long getMessageCount(Long conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    /**
     * 检查消息是否属于指定会话
     */
    public boolean isMessageBelongsToConversation(Long messageId, Long conversationId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        return messageOpt.map(message -> message.getConversation().getId().equals(conversationId)).orElse(false);
    }
} 