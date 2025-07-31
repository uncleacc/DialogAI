package com.dialogai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数据库初始化配置类
 * 负责启动时检查数据库连接、创建数据库和表结构
 * 
 * @author DialogAI
 * @version 1.0
 */
@Slf4j
@Configuration
public class DatabaseInitConfig {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    /**
     * 数据库初始化检查器
     * 在应用启动时执行，检查数据库连接和基础数据
     */
    @Bean
    @Order(1)
    public CommandLineRunner databaseInitChecker(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("开始数据库初始化检查...");
            
            try {
                // 检查数据库连接
                checkDatabaseConnection(dataSource);
                
                // 检查表结构
                checkDatabaseTables(dataSource);
                
                // 检查基础数据
                checkBasicData(jdbcTemplate);
                
                log.info("数据库初始化检查完成！");
                
            } catch (Exception e) {
                log.error("数据库初始化检查失败！", e);
                throw new RuntimeException("数据库初始化失败", e);
            }
        };
    }

    /**
     * 检查数据库连接
     */
    private void checkDatabaseConnection(DataSource dataSource) throws SQLException {
        log.info("检查数据库连接...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 验证连接可用性
            if (!connection.isValid(5)) {
                throw new SQLException("数据库连接无效");
            }
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            log.info("数据库连接成功！");
            log.info("数据库URL: {}", dataSourceUrl);
            log.info("数据库产品: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            log.info("JDBC驱动: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
            log.info("连接用户: {}", dataSourceUsername);
            log.info("DDL模式: {}", ddlAuto);
            
            // 检查数据库是否支持事务
            if (metaData.supportsTransactions()) {
                log.info("数据库支持事务");
            } else {
                log.warn("数据库不支持事务");
            }
            
            // 检查隔离级别
            try {
                log.info("默认事务隔离级别: {}", connection.getTransactionIsolation());
            } catch (SQLException e) {
                log.debug("无法获取事务隔离级别: {}", e.getMessage());
            }
            
        } catch (SQLException e) {
            log.error("数据库连接失败！请检查数据库配置", e);
            throw e;
        }
    }

    /**
     * 检查数据库表结构
     */
    private void checkDatabaseTables(DataSource dataSource) throws SQLException {
        log.info("检查数据库表结构...");
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // 需要检查的核心表
            String[] requiredTables = {"conversations", "messages"};
            
            for (String tableName : requiredTables) {
                if (tableExists(metaData, tableName)) {
                    log.info("表 '{}' 已存在", tableName);
                    logTableInfo(metaData, tableName);
                } else {
                    log.warn("表 '{}' 不存在，将由Hibernate自动创建", tableName);
                }
            }
            
            // 检查可选表
            String[] optionalTables = {"users", "conversation_shares", "system_configs"};
            
            for (String tableName : optionalTables) {
                if (tableExists(metaData, tableName)) {
                    log.info("扩展表 '{}' 已存在", tableName);
                } else {
                    log.info("扩展表 '{}' 不存在，将根据配置创建", tableName);
                }
            }
            
        } catch (SQLException e) {
            log.error("检查数据库表结构失败！", e);
            throw e;
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    /**
     * 记录表信息
     */
    private void logTableInfo(DatabaseMetaData metaData, String tableName) throws SQLException {
        // 获取表的列数
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            int columnCount = 0;
            while (columns.next()) {
                columnCount++;
            }
            log.debug("表 '{}' 包含 {} 个列", tableName, columnCount);
        }
        
        // 获取表的索引数
        try (ResultSet indexes = metaData.getIndexInfo(null, null, tableName, false, false)) {
            int indexCount = 0;
            while (indexes.next()) {
                indexCount++;
            }
            log.debug("表 '{}' 包含 {} 个索引", tableName, indexCount);
        }
    }

    /**
     * 检查基础数据
     */
    private void checkBasicData(JdbcTemplate jdbcTemplate) {
        log.info("检查基础数据...");
        
        try {
            // 检查系统配置数据
            checkSystemConfigs(jdbcTemplate);
            
            // 检查是否有会话数据
            checkConversationData(jdbcTemplate);
            
        } catch (Exception e) {
            log.error("检查基础数据失败！", e);
            // 基础数据检查失败不应该阻止应用启动
        }
    }

    /**
     * 检查系统配置数据
     */
    private void checkSystemConfigs(JdbcTemplate jdbcTemplate) {
        try {
            // 检查system_configs表是否存在
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = 'system_configs'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableCount != null && tableCount > 0) {
                // 检查系统配置数据
                String countSql = "SELECT COUNT(*) FROM system_configs WHERE config_key = 'system.version'";
                Integer configCount = jdbcTemplate.queryForObject(countSql, Integer.class);
                
                if (configCount != null && configCount > 0) {
                    log.info("系统配置数据已存在");
                    
                    // 获取系统版本
                    String versionSql = "SELECT config_value FROM system_configs WHERE config_key = 'system.version'";
                    String version = jdbcTemplate.queryForObject(versionSql, String.class);
                    log.info("系统版本: {}", version);
                } else {
                    log.info("系统配置数据不存在，将使用默认配置");
                }
            } else {
                log.info("系统配置表不存在，跳过配置检查");
            }
            
        } catch (Exception e) {
            log.warn("检查系统配置数据时出错: {}", e.getMessage());
        }
    }

    /**
     * 检查会话数据
     */
    private void checkConversationData(JdbcTemplate jdbcTemplate) {
        try {
            String countSql = "SELECT COUNT(*) FROM conversations";
            Integer conversationCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (conversationCount != null && conversationCount > 0) {
                log.info("发现 {} 个会话记录", conversationCount);
                
                // 获取最新会话信息
                String latestSql = "SELECT title, created_at FROM conversations ORDER BY created_at DESC LIMIT 1";
                jdbcTemplate.query(latestSql, (rs) -> {
                    String title = rs.getString("title");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    log.info("最新会话: '{}' (创建于 {})", title, 
                            createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                });
            } else {
                log.info("暂无会话数据");
            }
            
        } catch (Exception e) {
            log.warn("检查会话数据时出错: {}", e.getMessage());
        }
    }

    /**
     * 数据库健康检查器
     * 定期检查数据库连接状态
     */
    @Bean
    public DatabaseHealthChecker databaseHealthChecker(DataSource dataSource) {
        return new DatabaseHealthChecker(dataSource);
    }

    /**
     * 数据库健康检查类
     */
    public static class DatabaseHealthChecker {
        private final DataSource dataSource;
        private boolean lastCheckResult = true;
        
        public DatabaseHealthChecker(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        
        /**
         * 检查数据库连接是否正常
         */
        public boolean isHealthy() {
            try (Connection connection = dataSource.getConnection()) {
                // 执行简单查询测试连接
                boolean isValid = connection.isValid(5); // 5秒超时
                
                if (isValid != lastCheckResult) {
                    if (isValid) {
                        log.info("数据库连接已恢复正常");
                    } else {
                        log.error("数据库连接异常！");
                    }
                    lastCheckResult = isValid;
                }
                
                return isValid;
                
            } catch (SQLException e) {
                if (lastCheckResult) {
                    log.error("数据库连接检查失败: {}", e.getMessage());
                    lastCheckResult = false;
                }
                return false;
            }
        }
        
        /**
         * 获取数据库信息
         */
        public String getDatabaseInfo() {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                return String.format("%s %s", 
                        metaData.getDatabaseProductName(), 
                        metaData.getDatabaseProductVersion());
            } catch (SQLException e) {
                return "无法获取数据库信息: " + e.getMessage();
            }
        }
    }
} 