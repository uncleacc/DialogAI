package com.dialogai.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 消息实体类
 * 
 * @author DialogAI
 */
@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型：USER-用户消息，ASSISTANT-AI回复
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role;

    /**
     * 消息状态：SENDING-发送中，SENT-已发送，FAILED-发送失败
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    /**
     * 消息序号（在会话中的顺序）
     */
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    /**
     * 关联的会话
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 消息角色枚举
     */
    public enum MessageRole {
        USER,       // 用户消息
        ASSISTANT   // AI回复
    }

    /**
     * 消息状态枚举
     */
    public enum MessageStatus {
        SENDING,    // 发送中
        SENT,       // 已发送
        FAILED      // 发送失败
    }
} 