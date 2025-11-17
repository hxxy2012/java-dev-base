package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 系统工具Controller
 */
@Slf4j
@Tag(name = "系统工具")
@RestController
@RequestMapping("/system/tool")
public class ToolController {

    /**
     * 获取系统信息
     */
    @Operation(summary = "获取系统信息")
    @GetMapping("/info")
    public R<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();

        // JVM信息
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("name", System.getProperty("java.vm.name"));
        jvm.put("version", System.getProperty("java.version"));
        jvm.put("vendor", System.getProperty("java.vm.vendor"));

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long totalMemory = memoryMXBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
        long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
        jvm.put("totalMemory", totalMemory + "MB");
        jvm.put("usedMemory", usedMemory + "MB");
        jvm.put("freeMemory", (totalMemory - usedMemory) + "MB");

        info.put("jvm", jvm);

        // 系统信息
        Map<String, Object> system = new HashMap<>();
        system.put("os", System.getProperty("os.name"));
        system.put("arch", System.getProperty("os.arch"));
        system.put("version", System.getProperty("os.version"));

        try {
            InetAddress localhost = InetAddress.getLocalHost();
            system.put("hostname", localhost.getHostName());
            system.put("ip", localhost.getHostAddress());
        } catch (UnknownHostException e) {
            log.error("Failed to get host info", e);
        }

        info.put("system", system);

        // 线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> thread = new HashMap<>();
        thread.put("total", threadMXBean.getThreadCount());
        thread.put("daemon", threadMXBean.getDaemonThreadCount());
        thread.put("peak", threadMXBean.getPeakThreadCount());

        info.put("thread", thread);

        // 运行时信息
        Map<String, Object> runtime = new HashMap<>();
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = uptimeMillis / (1000 * 60 * 60 * 24);
        long hours = (uptimeMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptimeMillis % (1000 * 60 * 60)) / (1000 * 60);
        runtime.put("uptime", String.format("%d天%d小时%d分钟", days, hours, minutes));
        runtime.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());

        info.put("runtime", runtime);

        return R.ok(info);
    }

    /**
     * 获取客户端信息
     */
    @Operation(summary = "获取客户端信息")
    @GetMapping("/client")
    public R<Map<String, Object>> getClientInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();

        // 客户端IP
        String ip = getClientIp(request);
        info.put("ip", ip);

        // User-Agent
        String userAgent = request.getHeader("User-Agent");
        info.put("userAgent", userAgent);

        // 浏览器信息
        info.put("browser", parseBrowser(userAgent));

        // 操作系统
        info.put("os", parseOS(userAgent));

        // 请求头
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name ->
            headers.put(name, request.getHeader(name))
        );
        info.put("headers", headers);

        return R.ok(info);
    }

    /**
     * 系统时间
     */
    @Operation(summary = "获取系统时间")
    @GetMapping("/time")
    public R<Map<String, String>> getSystemTime() {
        Map<String, String> time = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        time.put("date", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        time.put("time", now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        time.put("dateTime", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        time.put("timestamp", String.valueOf(System.currentTimeMillis()));
        time.put("timezone", System.getProperty("user.timezone"));

        return R.ok(time);
    }

    /**
     * 系统环境变量
     */
    @Operation(summary = "获取系统环境变量")
    @GetMapping("/env")
    public R<Map<String, String>> getEnv() {
        Properties props = System.getProperties();
        Map<String, String> env = new HashMap<>();

        props.forEach((key, value) -> {
            // 过滤敏感信息
            String keyStr = key.toString();
            if (!keyStr.toLowerCase().contains("password") &&
                !keyStr.toLowerCase().contains("secret") &&
                !keyStr.toLowerCase().contains("key")) {
                env.put(keyStr, value.toString());
            }
        });

        return R.ok(env);
    }

    /**
     * 触发垃圾回收
     */
    @Operation(summary = "触发垃圾回收")
    @PostMapping("/gc")
    public R<String> triggerGC() {
        System.gc();
        log.info("Triggered garbage collection");
        return R.ok("垃圾回收已触发");
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于多级代理，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    /**
     * 解析浏览器类型
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Edge")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("Chrome")) {
            return "Google Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Apple Safari";
        } else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        }

        return "Unknown";
    }

    /**
     * 解析操作系统
     */
    private String parseOS(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }

        return "Unknown";
    }
}
