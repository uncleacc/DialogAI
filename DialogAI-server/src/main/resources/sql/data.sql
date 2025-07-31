-- DialogAI数据库初始化数据脚本
-- 创建时间: 2024-01-20

-- 设置字符集
SET NAMES utf8mb4;

-- 插入系统配置数据
INSERT IGNORE INTO `system_configs` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('system.version', '1.0.0', 'STRING', '系统版本号', 1),
('system.name', 'DialogAI', 'STRING', '系统名称', 1),
('system.description', '智能对话系统', 'STRING', '系统描述', 1),
('chat.max_message_length', '2000', 'INTEGER', '单条消息最大长度', 1),
('chat.max_messages_per_conversation', '1000', 'INTEGER', '每个会话最大消息数', 1),
('chat.auto_title_enabled', 'true', 'BOOLEAN', '是否启用自动生成会话标题', 1),
('ai.default_model', 'deepseek-chat', 'STRING', 'AI默认模型', 0),
('ai.max_tokens', '4000', 'INTEGER', 'AI响应最大token数', 0),
('ai.temperature', '0.7', 'STRING', 'AI创造性参数', 0),
('ui.theme', 'auto', 'STRING', '默认主题：light-明亮，dark-暗色，auto-自动', 1),
('ui.language', 'zh-CN', 'STRING', '默认语言', 1),
('feature.conversation_share', 'true', 'BOOLEAN', '是否启用会话分享功能', 1),
('feature.export_conversation', 'true', 'BOOLEAN', '是否启用会话导出功能', 1),
('security.max_login_attempts', '5', 'INTEGER', '最大登录尝试次数', 0),
('security.session_timeout', '7200', 'INTEGER', '会话超时时间（秒）', 0);

-- 插入默认用户（示例数据）
INSERT IGNORE INTO `users` (`id`, `username`, `email`, `nickname`, `status`) VALUES
(1, 'default_user', 'user@dialogai.com', 'AI用户', 'ACTIVE'),
(2, 'admin', 'admin@dialogai.com', '系统管理员', 'ACTIVE'); 