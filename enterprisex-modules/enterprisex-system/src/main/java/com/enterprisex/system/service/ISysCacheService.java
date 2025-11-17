package com.enterprisex.system.service;

import com.enterprisex.system.domain.CacheInfo;

import java.util.List;
import java.util.Map;

/**
 * 缓存管理服务接口
 *
 * @author EnterpriseX
 */
public interface ISysCacheService {

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称列表
     */
    List<String> getCacheNames();

    /**
     * 获取缓存键名列表
     *
     * @param cacheName 缓存名称
     * @return 缓存键名列表
     */
    List<String> getCacheKeys(String cacheName);

    /**
     * 获取缓存值
     *
     * @param cacheKey 缓存键名
     * @return 缓存信息
     */
    CacheInfo getCacheValue(String cacheKey);

    /**
     * 获取Redis信息
     *
     * @return Redis信息
     */
    Map<String, Object> getRedisInfo();

    /**
     * 获取命令统计
     *
     * @return 命令统计信息
     */
    Map<String, Object> getCommandStats();

    /**
     * 清除指定缓存
     *
     * @param cacheKey 缓存键名
     * @return 是否成功
     */
    boolean clearCache(String cacheKey);

    /**
     * 清除指定前缀的缓存
     *
     * @param cachePrefix 缓存前缀
     * @return 清除数量
     */
    int clearCacheByPrefix(String cachePrefix);

    /**
     * 清除所有缓存
     *
     * @return 清除数量
     */
    int clearAllCache();
}
