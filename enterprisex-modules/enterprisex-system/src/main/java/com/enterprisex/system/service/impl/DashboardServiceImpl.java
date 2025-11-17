package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprisex.common.redis.service.RedisCache;
import com.enterprisex.system.domain.SysLoginLog;
import com.enterprisex.system.domain.SysOperLog;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Dashboard服务实现
 *
 * @author EnterpriseX
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysLoginLogService loginLogService;

    @Autowired
    private ISysOperLogService operLogService;

    @Autowired
    private RedisCache redisCache;

    private static final String CACHE_KEY_PREFIX = "dashboard:";
    private static final String CACHE_KEY_BASIC_STATS = CACHE_KEY_PREFIX + "basic_stats";
    private static final String CACHE_KEY_USER_STATUS = CACHE_KEY_PREFIX + "user_status";
    private static final int CACHE_EXPIRE_MINUTES = 5; // 缓存5分钟

    @Override
    public Map<String, Long> getBasicStats() {
        // 尝试从缓存获取
        Map<String, Long> cachedStats = redisCache.getCacheObject(CACHE_KEY_BASIC_STATS);
        if (cachedStats != null) {
            return cachedStats;
        }

        // 从数据库查询
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, 0)));
        stats.put("roleCount", roleService.count());
        stats.put("deptCount", deptService.count());

        // 存入缓存
        redisCache.setCacheObject(CACHE_KEY_BASIC_STATS, stats, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return stats;
    }

    @Override
    public Map<String, Long> getTodayStats() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        Map<String, Long> stats = new HashMap<>();
        stats.put("todayLoginCount", loginLogService.count(
            new LambdaQueryWrapper<SysLoginLog>()
                .ge(SysLoginLog::getLoginTime, todayStart)
        ));
        stats.put("todayOperCount", operLogService.count(
            new LambdaQueryWrapper<SysOperLog>()
                .ge(SysOperLog::getOperTime, todayStart)
        ));

        return stats;
    }

    @Override
    public Map<String, Long> getUserStatusStats() {
        // 尝试从缓存获取
        Map<String, Long> cachedStats = redisCache.getCacheObject(CACHE_KEY_USER_STATUS);
        if (cachedStats != null) {
            return cachedStats;
        }

        // 从数据库查询
        Map<String, Long> stats = new HashMap<>();
        stats.put("正常", userService.count(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getDelFlag, 0)
        ));
        stats.put("禁用", userService.count(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 0)
                .eq(SysUser::getDelFlag, 0)
        ));

        // 存入缓存
        redisCache.setCacheObject(CACHE_KEY_USER_STATUS, stats, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getRecentLogins(int limit) {
        List<SysLoginLog> loginLogs = loginLogService.list(
            new LambdaQueryWrapper<SysLoginLog>()
                .orderByDesc(SysLoginLog::getLoginTime)
                .last("LIMIT " + limit)
        );

        return loginLogs.stream().map(log -> {
            Map<String, Object> login = new HashMap<>();
            login.put("username", log.getUsername());
            login.put("ip", log.getIpaddr());
            login.put("time", log.getLoginTime());
            login.put("status", log.getStatus() == 1 ? "成功" : "失败");
            login.put("location", log.getLoginLocation());
            login.put("browser", log.getBrowser());
            return login;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRecentOperations(int limit) {
        List<SysOperLog> operLogs = operLogService.list(
            new LambdaQueryWrapper<SysOperLog>()
                .orderByDesc(SysOperLog::getOperTime)
                .last("LIMIT " + limit)
        );

        return operLogs.stream().map(log -> {
            Map<String, Object> op = new HashMap<>();
            op.put("title", log.getTitle());
            op.put("operator", log.getOperName());
            op.put("time", log.getOperTime());
            op.put("status", log.getStatus() == 1 ? "成功" : "失败");
            op.put("businessType", getBusinessTypeName(log.getBusinessType()));
            op.put("costTime", log.getCostTime());
            return op;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getLoginStats(int days) {
        List<Map<String, Object>> stats = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(23, 59, 59);

            long count = loginLogService.count(
                new LambdaQueryWrapper<SysLoginLog>()
                    .ge(SysLoginLog::getLoginTime, dayStart)
                    .le(SysLoginLog::getLoginTime, dayEnd)
            );

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.format(dateFormatter));
            dayStat.put("count", count);
            stats.add(dayStat);
        }

        return stats;
    }

    @Override
    public void clearCache() {
        redisCache.deleteObject(CACHE_KEY_BASIC_STATS);
        redisCache.deleteObject(CACHE_KEY_USER_STATUS);
    }

    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(Integer businessType) {
        if (businessType == null) return "其它";
        switch (businessType) {
            case 1: return "新增";
            case 2: return "修改";
            case 3: return "删除";
            case 4: return "授权";
            case 5: return "导出";
            case 6: return "导入";
            case 7: return "强退";
            case 8: return "清空";
            default: return "其它";
        }
    }
}
