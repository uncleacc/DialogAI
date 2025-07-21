package com.dialogai.repository;

import com.dialogai.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息数据访问层
 * 
 * @author DialogAI
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 根据会话ID查找消息列表
     */
    List<Message> findByConversationIdOrderBySequenceNumberAsc(Long conversationId);

    /**
     * 根据会话ID分页查找消息
     */
    Page<Message> findByConversationIdOrderBySequenceNumberAsc(Long conversationId, Pageable pageable);

    /**
     * 根据会话ID和角色查找消息
     */
    List<Message> findByConversationIdAndRoleOrderBySequenceNumberAsc(Long conversationId, Message.MessageRole role);

    /**
     * 查找会话中的最大序号
     */
    @Query("SELECT MAX(m.sequenceNumber) FROM Message m WHERE m.conversation.id = :conversationId")
    Optional<Integer> findMaxSequenceNumberByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 根据会话ID统计消息数量
     */
    long countByConversationId(Long conversationId);

    /**
     * 根据会话ID和状态查找消息
     */
    List<Message> findByConversationIdAndStatusOrderBySequenceNumberAsc(Long conversationId, Message.MessageStatus status);

    /**
     * 查找最近的N条消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sequenceNumber DESC")
    List<Message> findRecentMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);
} 