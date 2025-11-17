package com.enterprisex.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.utils.SecurityUtils;
import com.enterprisex.system.domain.SysLoginLog;
import com.enterprisex.system.domain.SysOperLog;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        // 基础统计
        stats.setUserCount(userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, 0)));
        stats.setRoleCount(roleService.count());
        stats.setDeptCount(deptService.count());

        // 在线用户数（这里简化处理，实际应该从Redis获取在线会话数）
        stats.setOnlineUserCount(0L);

        // 今日登录数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        stats.setTodayLoginCount(loginLogService.count(
            new LambdaQueryWrapper<SysLoginLog>()
                .ge(SysLoginLog::getLoginTime, todayStart)
        ));

        // 今日操作数
        stats.setTodayOperCount(operLogService.count(
            new LambdaQueryWrapper<SysOperLog>()
                .ge(SysOperLog::getOperTime, todayStart)
        ));

        // 未读消息数（如果用户已登录）
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            stats.setUnreadMessageCount(messageService.countUnreadMessages(userId));
        } else {
            stats.setUnreadMessageCount(0L);
        }

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
        List<SysLoginLog> loginLogs = loginLogService.list(
            new LambdaQueryWrapper<SysLoginLog>()
                .orderByDesc(SysLoginLog::getLoginTime)
                .last("LIMIT 5")
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

    /**
     * 获取最近操作记录
     */
    private List<Map<String, Object>> getRecentOperations() {
        List<SysOperLog> operLogs = operLogService.list(
            new LambdaQueryWrapper<SysOperLog>()
                .orderByDesc(SysOperLog::getOperTime)
                .last("LIMIT 5")
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

    /**
     * 获取用户状态统计
     */
    private Map<String, Long> getUserStatusStats() {
        Map<String, Long> statusStats = new HashMap<>();

        // 统计正常用户数
        long normalCount = userService.count(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getDelFlag, 0)
        );

        // 统计禁用用户数
        long disabledCount = userService.count(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 0)
                .eq(SysUser::getDelFlag, 0)
        );

        statusStats.put("正常", normalCount);
        statusStats.put("禁用", disabledCount);
        return statusStats;
    }

    /**
     * 获取近7天登录统计
     */
    private List<Map<String, Object>> getWeeklyLoginStats() {
        List<Map<String, Object>> weeklyStats = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(23, 59, 59);

            // 统计当天的登录次数
            long count = loginLogService.count(
                new LambdaQueryWrapper<SysLoginLog>()
                    .ge(SysLoginLog::getLoginTime, dayStart)
                    .le(SysLoginLog::getLoginTime, dayEnd)
            );

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.format(dateFormatter));
            dayStat.put("count", count);
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
