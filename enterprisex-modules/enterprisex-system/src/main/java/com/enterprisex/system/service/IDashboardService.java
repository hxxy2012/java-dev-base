package com.enterprisex.system.service;

import java.util.List;
import java.util.Map;

/**
 * Dashboard服务接口
 *
 * @author EnterpriseX
 */
public interface IDashboardService {

    /**
     * 获取基础统计数据（带缓存）
     *
     * @return 统计数据Map
     */
    Map<String, Long> getBasicStats();

    /**
     * 获取今日统计数据
     *
     * @return 今日统计Map
     */
    Map<String, Long> getTodayStats();

    /**
     * 获取用户状态统计
     *
     * @return 用户状态Map
     */
    Map<String, Long> getUserStatusStats();

    /**
     * 获取最近登录记录
     *
     * @param limit 限制数量
     * @return 登录记录列表
     */
    List<Map<String, Object>> getRecentLogins(int limit);

    /**
     * 获取最近操作记录
     *
     * @param limit 限制数量
     * @return 操作记录列表
     */
    List<Map<String, Object>> getRecentOperations(int limit);

    /**
     * 获取近N天登录统计
     *
     * @param days 天数
     * @return 登录统计列表
     */
    List<Map<String, Object>> getLoginStats(int days);

    /**
     * 清除Dashboard缓存
     */
    void clearCache();
}
