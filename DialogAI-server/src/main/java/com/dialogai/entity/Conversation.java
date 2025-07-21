package com.dialogai.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话实体类
 * 
 * @author DialogAI
 */
@Entity
@Table(name = "conversations")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话标题
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * 会话描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 用户ID（预留字段，用于多用户支持）
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 会话状态：ACTIVE-活跃，ARCHIVED-归档，DELETED-已删除
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 消息列表（一对多关系）
     */
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    /**
     * 会话状态枚举
     */
    public enum ConversationStatus {
        ACTIVE,     // 活跃
        ARCHIVED,   // 归档
        DELETED     // 已删除
    }
} 