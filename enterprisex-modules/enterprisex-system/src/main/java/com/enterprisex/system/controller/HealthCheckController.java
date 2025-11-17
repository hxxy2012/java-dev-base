package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统健康检查Controller
 */
@Slf4j
@Tag(name = "健康检查")
@RestController
@RequestMapping("/system/health")
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 综合健康检查
     */
    @Operation(summary = "综合健康检查")
    @GetMapping("/check")
    public R<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();

        // 整体状态
        boolean isHealthy = true;

        // 数据库健康检查
        Map<String, Object> database = checkDatabase();
        health.put("database", database);
        if (!"UP".equals(database.get("status"))) {
            isHealthy = false;
        }

        // Redis健康检查
        Map<String, Object> redis = checkRedis();
        health.put("redis", redis);
        if (!"UP".equals(redis.get("status"))) {
            isHealthy = false;
        }

        // 磁盘空间检查
        Map<String, Object> disk = checkDiskSpace();
        health.put("disk", disk);
        if (!"UP".equals(disk.get("status"))) {
            isHealthy = false;
        }

        // 内存检查
        Map<String, Object> memory = checkMemory();
        health.put("memory", memory);
        if (!"UP".equals(memory.get("status"))) {
            isHealthy = false;
        }

        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("timestamp", System.currentTimeMillis());

        return R.ok(health);
    }

    /**
     * 数据库健康检查
     */
    @Operation(summary = "数据库健康检查")
    @GetMapping("/database")
    public R<Map<String, Object>> databaseHealth() {
        return R.ok(checkDatabase());
    }

    /**
     * Redis健康检查
     */
    @Operation(summary = "Redis健康检查")
    @GetMapping("/redis")
    public R<Map<String, Object>> redisHealth() {
        return R.ok(checkRedis());
    }

    /**
     * 磁盘空间检查
     */
    @Operation(summary = "磁盘空间检查")
    @GetMapping("/disk")
    public R<Map<String, Object>> diskHealth() {
        return R.ok(checkDiskSpace());
    }

    /**
     * 内存检查
     */
    @Operation(summary = "内存检查")
    @GetMapping("/memory")
    public R<Map<String, Object>> memoryHealth() {
        return R.ok(checkMemory());
    }

    /**
     * 检查数据库连接
     */
    private Map<String, Object> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long responseTime = System.currentTimeMillis() - startTime;

            result.put("status", "UP");
            result.put("responseTime", responseTime + "ms");
            result.put("message", "数据库连接正常");

            // 获取连接数
            try {
                Integer connections = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.PROCESSLIST", Integer.class);
                result.put("connections", connections);
            } catch (Exception e) {
                log.warn("Failed to get connection count", e);
            }

        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("message", "数据库连接失败: " + e.getMessage());
            log.error("Database health check failed", e);
        }
        return result;
    }

    /**
     * 检查Redis连接
     */
    private Map<String, Object> checkRedis() {
        Map<String, Object> result = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            redisTemplate.getConnectionFactory().getConnection().ping();
            long responseTime = System.currentTimeMillis() - startTime;

            result.put("status", "UP");
            result.put("responseTime", responseTime + "ms");
            result.put("message", "Redis连接正常");

        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("message", "Redis连接失败: " + e.getMessage());
            log.error("Redis health check failed", e);
        }
        return result;
    }

    /**
     * 检查磁盘空间
     */
    private Map<String, Object> checkDiskSpace() {
        Map<String, Object> result = new HashMap<>();
        try {
            java.io.File root = new java.io.File("/");
            long total = root.getTotalSpace();
            long free = root.getFreeSpace();
            long used = total - free;
            double usagePercent = (double) used / total * 100;

            result.put("total", formatSize(total));
            result.put("used", formatSize(used));
            result.put("free", formatSize(free));
            result.put("usagePercent", String.format("%.2f%%", usagePercent));

            // 如果使用率超过90%，标记为警告
            if (usagePercent > 90) {
                result.put("status", "WARNING");
                result.put("message", "磁盘空间不足");
            } else {
                result.put("status", "UP");
                result.put("message", "磁盘空间正常");
            }

        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("message", "磁盘检查失败: " + e.getMessage());
            log.error("Disk health check failed", e);
        }
        return result;
    }

    /**
     * 检查内存使用
     */
    private Map<String, Object> checkMemory() {
        Map<String, Object> result = new HashMap<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            long total = runtime.totalMemory();
            long free = runtime.freeMemory();
            long used = total - free;
            long max = runtime.maxMemory();
            double usagePercent = (double) used / max * 100;

            result.put("total", formatSize(total));
            result.put("used", formatSize(used));
            result.put("free", formatSize(free));
            result.put("max", formatSize(max));
            result.put("usagePercent", String.format("%.2f%%", usagePercent));

            // 如果使用率超过85%，标记为警告
            if (usagePercent > 85) {
                result.put("status", "WARNING");
                result.put("message", "内存使用率较高");
            } else {
                result.put("status", "UP");
                result.put("message", "内存使用正常");
            }

        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("message", "内存检查失败: " + e.getMessage());
            log.error("Memory health check failed", e);
        }
        return result;
    }

    /**
     * 格式化文件大小
     */
    private String formatSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", size / 1024.0 / 1024.0);
        } else {
            return String.format("%.2fGB", size / 1024.0 / 1024.0 / 1024.0);
        }
    }
}
