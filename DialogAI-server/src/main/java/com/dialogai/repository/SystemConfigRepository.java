package com.dialogai.repository;

import com.dialogai.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置仓库接口
 * 
 * @author DialogAI
 * @version 1.0
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * 根据配置键查找配置
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * 根据配置键检查是否存在
     */
    boolean existsByConfigKey(String configKey);

    /**
     * 获取所有公开的配置
     */
    List<SystemConfig> findByIsPublicTrueOrderByConfigKey();

    /**
     * 根据配置类型查找配置
     */
    List<SystemConfig> findByConfigTypeOrderByConfigKey(SystemConfig.ConfigType configType);

    /**
     * 根据配置键前缀查找配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configKey LIKE :prefix% ORDER BY s.configKey")
    List<SystemConfig> findByConfigKeyStartingWith(@Param("prefix") String prefix);

    /**
     * 获取公开的配置，按前缀过滤
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configKey LIKE :prefix% AND s.isPublic = true ORDER BY s.configKey")
    List<SystemConfig> findPublicConfigsByPrefix(@Param("prefix") String prefix);

    /**
     * 根据配置键批量查找
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configKey IN :keys ORDER BY s.configKey")
    List<SystemConfig> findByConfigKeyIn(@Param("keys") List<String> keys);

    /**
     * 删除指定前缀的配置（用于批量清理）
     */
    @Query("DELETE FROM SystemConfig s WHERE s.configKey LIKE :prefix%")
    void deleteByConfigKeyStartingWith(@Param("prefix") String prefix);
} 