package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.system.domain.CacheInfo;
import com.enterprisex.system.service.ISysCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 缓存管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "缓存管理")
@RestController
@RequestMapping("/monitor/cache")
public class SysCacheController {

    @Autowired
    private ISysCacheService cacheService;

    /**
     * 获取缓存监控信息
     */
    @Operation(summary = "获取缓存监控信息")
    @GetMapping("/info")
    public R<Map<String, Object>> getInfo() {
        Map<String, Object> info = cacheService.getRedisInfo();
        return R.ok(info);
    }

    /**
     * 获取所有缓存名称
     */
    @Operation(summary = "获取所有缓存名称")
    @GetMapping("/names")
    public R<List<String>> getCacheNames() {
        List<String> names = cacheService.getCacheNames();
        return R.ok(names);
    }

    /**
     * 获取缓存键名列表
     */
    @Operation(summary = "获取缓存键名列表")
    @GetMapping("/keys")
    public R<List<String>> getCacheKeys(@RequestParam String cacheName) {
        List<String> keys = cacheService.getCacheKeys(cacheName);
        return R.ok(keys);
    }

    /**
     * 获取缓存值
     */
    @Operation(summary = "获取缓存值")
    @GetMapping("/value")
    public R<CacheInfo> getCacheValue(@RequestParam String cacheKey) {
        CacheInfo info = cacheService.getCacheValue(cacheKey);
        return R.ok(info);
    }

    /**
     * 清除指定缓存
     */
    @Log(title = "缓存管理", businessType = BusinessType.CLEAN)
    @Operation(summary = "清除指定缓存")
    @DeleteMapping("/clear")
    public R<Void> clearCache(@RequestParam String cacheKey) {
        boolean success = cacheService.clearCache(cacheKey);
        return success ? R.ok("清除成功") : R.fail("清除失败");
    }

    /**
     * 清除指定前缀的缓存
     */
    @Log(title = "缓存管理", businessType = BusinessType.CLEAN)
    @Operation(summary = "清除指定前缀的缓存")
    @DeleteMapping("/clearPrefix")
    public R<Void> clearCacheByPrefix(@RequestParam String cachePrefix) {
        int count = cacheService.clearCacheByPrefix(cachePrefix);
        return R.ok(String.format("成功清除%d条缓存", count));
    }

    /**
     * 清除所有缓存
     */
    @Log(title = "缓存管理", businessType = BusinessType.CLEAN)
    @Operation(summary = "清除所有缓存")
    @DeleteMapping("/clearAll")
    public R<Void> clearAllCache() {
        int count = cacheService.clearAllCache();
        return R.ok(String.format("成功清除%d条缓存", count));
    }
}
