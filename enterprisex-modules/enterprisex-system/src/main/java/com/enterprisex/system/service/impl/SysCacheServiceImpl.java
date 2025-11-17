package com.enterprisex.system.service.impl;

import com.enterprisex.common.redis.service.RedisCache;
import com.enterprisex.system.domain.CacheInfo;
import com.enterprisex.system.service.ISysCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存管理服务实现
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class SysCacheServiceImpl implements ISysCacheService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 缓存名称前缀列表
     */
    private static final List<String> CACHE_PREFIXES = Arrays.asList(
            "login_tokens:",
            "online_tokens:",
            "captcha_codes:",
            "sys_config:",
            "sys_dict:",
            "repeat_submit:",
            "rate_limit:",
            "pwd_err_cnt:"
    );

    @Override
    public List<String> getCacheNames() {
        return new ArrayList<>(CACHE_PREFIXES);
    }

    @Override
    public List<String> getCacheKeys(String cacheName) {
        try {
            Collection<String> keys = redisCache.keys(cacheName + "*");
            return new ArrayList<>(keys);
        } catch (Exception e) {
            log.error("获取缓存键名失败: cacheName={}", cacheName, e);
            return new ArrayList<>();
        }
    }

    @Override
    public CacheInfo getCacheValue(String cacheKey) {
        try {
            Object value = redisCache.getCacheObject(cacheKey);
            Long expire = redisCache.getExpire(cacheKey);

            return CacheInfo.builder()
                    .cacheKey(cacheKey)
                    .cacheValue(value != null ? value.toString() : "")
                    .expireTime(expire)
                    .build();
        } catch (Exception e) {
            log.error("获取缓存值失败: cacheKey={}", cacheKey, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> info = new HashMap<>();
        try {
            // 获取Redis基本信息
            Collection<String> allKeys = redisCache.keys("*");
            info.put("dbSize", allKeys.size());

            // 统计各类型缓存数量
            Map<String, Long> cacheStats = new HashMap<>();
            for (String prefix : CACHE_PREFIXES) {
                Collection<String> keys = redisCache.keys(prefix + "*");
                cacheStats.put(prefix, (long) keys.size());
            }
            info.put("cacheStats", cacheStats);

        } catch (Exception e) {
            log.error("获取Redis信息失败", e);
        }
        return info;
    }

    @Override
    public Map<String, Object> getCommandStats() {
        // 简化实现，返回基本统计
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_commands_processed", 0);
        stats.put("instantaneous_ops_per_sec", 0);
        return stats;
    }

    @Override
    public boolean clearCache(String cacheKey) {
        try {
            return redisCache.deleteObject(cacheKey);
        } catch (Exception e) {
            log.error("清除缓存失败: cacheKey={}", cacheKey, e);
            return false;
        }
    }

    @Override
    public int clearCacheByPrefix(String cachePrefix) {
        try {
            Collection<String> keys = redisCache.keys(cachePrefix + "*");
            if (keys.isEmpty()) {
                return 0;
            }

            int count = 0;
            for (String key : keys) {
                if (redisCache.deleteObject(key)) {
                    count++;
                }
            }

            log.info("清除缓存成功: prefix={}, count={}", cachePrefix, count);
            return count;
        } catch (Exception e) {
            log.error("清除缓存失败: prefix={}", cachePrefix, e);
            return 0;
        }
    }

    @Override
    public int clearAllCache() {
        try {
            Collection<String> keys = redisCache.keys("*");
            if (keys.isEmpty()) {
                return 0;
            }

            int count = 0;
            for (String key : keys) {
                if (redisCache.deleteObject(key)) {
                    count++;
                }
            }

            log.warn("清空所有缓存: count={}", count);
            return count;
        } catch (Exception e) {
            log.error("清空所有缓存失败", e);
            return 0;
        }
    }
}
