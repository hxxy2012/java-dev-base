package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.utils.SecurityUtils;
import com.enterprisex.system.service.IDashboardService;
import com.enterprisex.system.service.ISysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Dashboard仪表盘控制器
 */
@Tag(name = "Dashboard仪表盘", description = "系统统计数据接口")
@RestController
@RequestMapping("/system/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @Autowired
    private ISysMessageService messageService;

    /**
     * Dashboard统计数据
     */
    @Data
    public static class DashboardStats {
        private Long userCount;
        private Long roleCount;
        private Long deptCount;
        private Long onlineUserCount;
        private Long todayLoginCount;
        private Long todayOperCount;
        private Long unreadMessageCount;
        private List<Map<String, Object>> recentLogins;
        private List<Map<String, Object>> recentOperations;
        private Map<String, Long> userStatusStats;
        private List<Map<String, Object>> weeklyLoginStats;
    }

    /**
     * 获取Dashboard统计数据
     */
    @Operation(summary = "获取Dashboard统计数据")
    @GetMapping("/stats")
    public R<DashboardStats> getStats() {
        DashboardStats stats = new DashboardStats();

        // 基础统计（带缓存）
        Map<String, Long> basicStats = dashboardService.getBasicStats();
        stats.setUserCount(basicStats.get("userCount"));
        stats.setRoleCount(basicStats.get("roleCount"));
        stats.setDeptCount(basicStats.get("deptCount"));

        // 在线用户数（从Redis实时获取）
        stats.setOnlineUserCount(dashboardService.getOnlineUserCount());

        // 今日统计
        Map<String, Long> todayStats = dashboardService.getTodayStats();
        stats.setTodayLoginCount(todayStats.get("todayLoginCount"));
        stats.setTodayOperCount(todayStats.get("todayOperCount"));

        // 未读消息数（如果用户已登录）
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            stats.setUnreadMessageCount(messageService.countUnreadMessages(userId));
        } else {
            stats.setUnreadMessageCount(0L);
        }

        // 最近记录（不缓存，保证实时性）
        stats.setRecentLogins(dashboardService.getRecentLogins(5));
        stats.setRecentOperations(dashboardService.getRecentOperations(5));

        // 用户状态统计（带缓存）
        stats.setUserStatusStats(dashboardService.getUserStatusStats());

        // 近7天登录统计
        stats.setWeeklyLoginStats(dashboardService.getLoginStats(7));

        return R.ok(stats);
    }

    /**
     * 获取系统快速访问链接
     */
    @Operation(summary = "获取快速访问链接")
    @GetMapping("/quickLinks")
    public R<List<Map<String, Object>>> getQuickLinks() {
        List<Map<String, Object>> links = new ArrayList<>();

        addQuickLink(links, "用户管理", "/system/user", "UserOutlined", "#1890ff");
        addQuickLink(links, "角色管理", "/system/role", "TeamOutlined", "#52c41a");
        addQuickLink(links, "菜单管理", "/system/menu", "MenuOutlined", "#faad14");
        addQuickLink(links, "系统监控", "/monitor/server", "MonitorOutlined", "#f5222d");
        addQuickLink(links, "操作日志", "/monitor/operlog", "FileTextOutlined", "#722ed1");
        addQuickLink(links, "定时任务", "/monitor/job", "ClockCircleOutlined", "#13c2c2");
        addQuickLink(links, "代码生成", "/tool/gen", "CodeOutlined", "#eb2f96");
        addQuickLink(links, "系统工具", "/tool/system", "ToolOutlined", "#fa8c16");

        return R.ok(links);
    }

    private void addQuickLink(List<Map<String, Object>> links, String name, String path, String icon, String color) {
        Map<String, Object> link = new HashMap<>();
        link.put("name", name);
        link.put("path", path);
        link.put("icon", icon);
        link.put("color", color);
        links.add(link);
    }
}
