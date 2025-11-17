package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.system.service.*;
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
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysOperLogService operLogService;

    @Autowired
    private ISysLoginLogService loginLogService;

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

        // 基础统计
        stats.setUserCount(userService.count());
        stats.setRoleCount(roleService.count());
        stats.setDeptCount(deptService.count());

        // 在线用户数（这里简化处理，实际应该从Redis获取）
        stats.setOnlineUserCount(0L);

        // 今日登录数（简化处理）
        stats.setTodayLoginCount(loginLogService.count());

        // 今日操作数（简化处理）
        stats.setTodayOperCount(operLogService.count());

        // 未读消息数（简化处理）
        stats.setUnreadMessageCount(0L);

        // 最近登录记录
        stats.setRecentLogins(getRecentLogins());

        // 最近操作记录
        stats.setRecentOperations(getRecentOperations());

        // 用户状态统计
        stats.setUserStatusStats(getUserStatusStats());

        // 近7天登录统计
        stats.setWeeklyLoginStats(getWeeklyLoginStats());

        return R.ok(stats);
    }

    /**
     * 获取最近登录记录
     */
    private List<Map<String, Object>> getRecentLogins() {
        List<Map<String, Object>> recentLogins = new ArrayList<>();
        // 简化处理，实际应该从登录日志表查询
        for (int i = 0; i < 5; i++) {
            Map<String, Object> login = new HashMap<>();
            login.put("username", "user" + i);
            login.put("ip", "192.168.1." + (100 + i));
            login.put("time", new Date());
            login.put("status", "成功");
            recentLogins.add(login);
        }
        return recentLogins;
    }

    /**
     * 获取最近操作记录
     */
    private List<Map<String, Object>> getRecentOperations() {
        List<Map<String, Object>> recentOps = new ArrayList<>();
        // 简化处理，实际应该从操作日志表查询
        String[] operations = {"新增用户", "修改角色", "删除菜单", "查询日志", "导出数据"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> op = new HashMap<>();
            op.put("title", operations[i % operations.length]);
            op.put("operator", "admin");
            op.put("time", new Date());
            op.put("status", i % 4 == 0 ? "失败" : "成功");
            recentOps.add(op);
        }
        return recentOps;
    }

    /**
     * 获取用户状态统计
     */
    private Map<String, Long> getUserStatusStats() {
        Map<String, Long> statusStats = new HashMap<>();
        // 简化处理，实际应该从用户表统计
        statusStats.put("正常", 85L);
        statusStats.put("禁用", 15L);
        return statusStats;
    }

    /**
     * 获取近7天登录统计
     */
    private List<Map<String, Object>> getWeeklyLoginStats() {
        List<Map<String, Object>> weeklyStats = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", String.format("%02d/%02d",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)));
            // 模拟数据，实际应该从登录日志表统计
            dayStat.put("count", (long) (Math.random() * 100 + 50));
            weeklyStats.add(dayStat);
        }

        return weeklyStats;
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
