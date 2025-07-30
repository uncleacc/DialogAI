package com.dialogai.repository;

import com.dialogai.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会话数据访问层
 * 
 * @author DialogAI
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * 根据用户ID查找会话列表
     */
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(String userId);

    /**
     * 根据用户ID和状态查找会话列表
     */
    List<Conversation> findByUserIdAndStatusOrderByUpdatedAtDesc(String userId, Conversation.ConversationStatus status);

    /**
     * 根据用户ID分页查找会话
     */
    Page<Conversation> findByUserIdOrderByUpdatedAtDesc(String userId, Pageable pageable);

    /**
     * 根据状态查找会话
     */
    List<Conversation> findByStatus(Conversation.ConversationStatus status);

    /**
     * 根据标题模糊查询会话
     */
    @Query("SELECT c FROM Conversation c WHERE c.title LIKE %:keyword% AND c.status != 'DELETED'")
    List<Conversation> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 查找活跃的会话
     */
    @Query("SELECT c FROM Conversation c WHERE c.status = 'ACTIVE' ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveConversations();

    /**
     * 根据会话ID和用户ID查找会话
     */
    Optional<Conversation> findByIdAndUserId(Long id, String userId);

    /**
     * 根据用户ID和状态查找会话列表，排除 DELETED 状态
     */
    List<Conversation> findByUserIdAndStatusNotOrderByUpdatedAtDesc(String userId, Conversation.ConversationStatus status);
} 