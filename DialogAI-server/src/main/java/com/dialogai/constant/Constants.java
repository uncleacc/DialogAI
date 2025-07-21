package com.dialogai.constant;

/**
 * 系统常量
 * 
 * @author DialogAI
 */
public class Constants {

    /**
     * 默认用户ID
     */
    public static final String DEFAULT_USER_ID = "default-user";

    /**
     * 默认会话标题
     */
    public static final String DEFAULT_CONVERSATION_TITLE = "新对话";

    /**
     * 最大消息长度
     */
    public static final int MAX_MESSAGE_LENGTH = 4000;

    /**
     * 最大会话历史消息数量
     */
    public static final int MAX_CONVERSATION_HISTORY = 20;

    /**
     * 缓存相关常量
     */
    public static class Cache {
        public static final String CONVERSATIONS = "conversations";
        public static final String MESSAGES = "messages";
        public static final long DEFAULT_TTL = 3600; // 1小时
    }

    /**
     * API相关常量
     */
    public static class Api {
        public static final String BASE_PATH = "/api";
        public static final String CHAT_PATH = "/chat";
        public static final String CONVERSATIONS_PATH = "/conversations";
    }

    /**
     * 数据库相关常量
     */
    public static class Database {
        public static final String CONVERSATIONS_TABLE = "conversations";
        public static final String MESSAGES_TABLE = "messages";
    }
} 