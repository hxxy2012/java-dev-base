package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * IP访问控制器
 */
@Tag(name = "IP访问控制", description = "IP黑白名单管理")
@Slf4j
@RestController
@RequestMapping("/system/ipaccess")
public class IpAccessController {

    // IP规则存储（实际项目中应该存储在数据库或Redis中）
    private static final Map<String, IpRule> IP_RULES = new ConcurrentHashMap<>();

    // IP访问统计
    private static final Map<String, AccessStat> ACCESS_STATS = new ConcurrentHashMap<>();

    /**
     * IP规则
     */
    @Data
    public static class IpRule {
        private String id;
        private String ipAddress;
        private Integer ruleType; // 1=白名单 2=黑名单
        private String description;
        private Integer status; // 1=启用 0=禁用
        private Date createTime;
        private String createBy;
    }

    /**
     * 访问统计
     */
    @Data
    public static class AccessStat {
        private String ipAddress;
        private Long accessCount;
        private Date lastAccessTime;
        private Long blockedCount;
    }

    /**
     * 获取IP规则列表
     */
    @Operation(summary = "获取IP规则列表")
    @GetMapping("/rules")
    public R<List<IpRule>> getRules(@RequestParam(required = false) Integer ruleType) {
        List<IpRule> rules = new ArrayList<>(IP_RULES.values());

        if (ruleType != null) {
            rules = rules.stream()
                .filter(r -> r.getRuleType().equals(ruleType))
                .toList();
        }

        rules.sort(Comparator.comparing(IpRule::getCreateTime).reversed());
        return R.ok(rules);
    }

    /**
     * 添加IP规则
     */
    @Operation(summary = "添加IP规则")
    @PostMapping("/rules")
    public R<String> addRule(@RequestBody IpRule rule) {
        // 验证IP地址格式
        if (!isValidIpAddress(rule.getIpAddress())) {
            return R.fail("IP地址格式不正确");
        }

        // 检查IP是否已存在
        if (IP_RULES.values().stream()
                .anyMatch(r -> r.getIpAddress().equals(rule.getIpAddress()))) {
            return R.fail("该IP规则已存在");
        }

        rule.setId(UUID.randomUUID().toString());
        rule.setCreateTime(new Date());
        rule.setCreateBy("admin");
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }

        IP_RULES.put(rule.getId(), rule);
        log.info("添加IP规则: {}", rule.getIpAddress());
        return R.ok("添加成功");
    }

    /**
     * 修改IP规则
     */
    @Operation(summary = "修改IP规则")
    @PutMapping("/rules")
    public R<String> updateRule(@RequestBody IpRule rule) {
        if (rule.getId() == null || !IP_RULES.containsKey(rule.getId())) {
            return R.fail("规则不存在");
        }

        IpRule existingRule = IP_RULES.get(rule.getId());
        existingRule.setDescription(rule.getDescription());
        existingRule.setStatus(rule.getStatus());
        existingRule.setRuleType(rule.getRuleType());

        log.info("更新IP规则: {}", rule.getId());
        return R.ok("更新成功");
    }

    /**
     * 删除IP规则
     */
    @Operation(summary = "删除IP规则")
    @DeleteMapping("/rules/{ids}")
    public R<String> deleteRules(@PathVariable String[] ids) {
        for (String id : ids) {
            IP_RULES.remove(id);
            log.info("删除IP规则: {}", id);
        }
        return R.ok("删除成功");
    }

    /**
     * 检查IP是否允许访问
     */
    @Operation(summary = "检查IP访问权限")
    @GetMapping("/check/{ipAddress}")
    public R<Map<String, Object>> checkAccess(@PathVariable String ipAddress) {
        Map<String, Object> result = new HashMap<>();

        // 统计访问次数
        updateAccessStat(ipAddress);

        boolean allowed = isIpAllowed(ipAddress);
        result.put("ipAddress", ipAddress);
        result.put("allowed", allowed);
        result.put("reason", allowed ? "允许访问" : "IP已被限制");

        return R.ok(result);
    }

    /**
     * 获取访问统计
     */
    @Operation(summary = "获取访问统计")
    @GetMapping("/stats")
    public R<List<AccessStat>> getAccessStats() {
        List<AccessStat> stats = new ArrayList<>(ACCESS_STATS.values());
        stats.sort(Comparator.comparing(AccessStat::getAccessCount).reversed());
        return R.ok(stats);
    }

    /**
     * 清空访问统计
     */
    @Operation(summary = "清空访问统计")
    @DeleteMapping("/stats")
    public R<String> clearStats() {
        ACCESS_STATS.clear();
        return R.ok("统计数据已清空");
    }

    // ==================== 辅助方法 ====================

    /**
     * 判断IP是否允许访问
     */
    private boolean isIpAllowed(String ipAddress) {
        // 检查黑名单
        boolean inBlacklist = IP_RULES.values().stream()
            .anyMatch(r -> r.getRuleType() == 2
                && r.getStatus() == 1
                && matchIp(ipAddress, r.getIpAddress()));

        if (inBlacklist) {
            return false;
        }

        // 检查白名单
        List<IpRule> whitelist = IP_RULES.values().stream()
            .filter(r -> r.getRuleType() == 1 && r.getStatus() == 1)
            .toList();

        // 如果没有白名单规则，默认允许
        if (whitelist.isEmpty()) {
            return true;
        }

        // 有白名单规则，检查IP是否在白名单中
        return whitelist.stream()
            .anyMatch(r -> matchIp(ipAddress, r.getIpAddress()));
    }

    /**
     * IP匹配（支持通配符和CIDR）
     */
    private boolean matchIp(String ip, String pattern) {
        // 精确匹配
        if (ip.equals(pattern)) {
            return true;
        }

        // 支持CIDR格式，如 192.168.1.0/24
        if (pattern.contains("/")) {
            return IpUtils.isIpInCidr(ip, pattern);
        }

        // 支持*通配符
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return Pattern.matches(regex, ip);
        }

        return false;
    }

    /**
     * 验证IP地址格式（支持CIDR、通配符和标准IP）
     */
    private boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // 支持CIDR格式，如 192.168.1.0/24
        if (ip.contains("/")) {
            return IpUtils.isValidCidr(ip);
        }

        // 支持*通配符
        if (ip.contains("*")) {
            return Pattern.matches("^(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)$", ip);
        }

        // 标准IP格式
        return IpUtils.isValidIp(ip);
    }

    /**
     * 更新访问统计
     */
    private void updateAccessStat(String ipAddress) {
        ACCESS_STATS.compute(ipAddress, (key, stat) -> {
            if (stat == null) {
                stat = new AccessStat();
                stat.setIpAddress(ipAddress);
                stat.setAccessCount(0L);
                stat.setBlockedCount(0L);
            }
            stat.setAccessCount(stat.getAccessCount() + 1);
            stat.setLastAccessTime(new Date());

            if (!isIpAllowed(ipAddress)) {
                stat.setBlockedCount(stat.getBlockedCount() + 1);
            }

            return stat;
        });
    }
}
