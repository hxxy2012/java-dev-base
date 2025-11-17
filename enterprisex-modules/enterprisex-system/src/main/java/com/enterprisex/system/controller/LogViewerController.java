package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统日志查看器控制器
 */
@Tag(name = "系统日志查看器", description = "查看和管理系统日志文件")
@Slf4j
@RestController
@RequestMapping("/system/logviewer")
public class LogViewerController {

    // 日志文件目录 - 可以通过配置文件配置
    private static final String LOG_DIR = "logs/";

    /**
     * 日志文件信息DTO
     */
    @Data
    public static class LogFileInfo {
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String fileSizeStr;
        private Date lastModified;
    }

    /**
     * 日志内容响应DTO
     */
    @Data
    public static class LogContent {
        private String fileName;
        private String content;
        private Long totalLines;
        private Long fileSize;
        private String fileSizeStr;
        private Date lastModified;
    }

    /**
     * 获取日志文件列表
     */
    @Operation(summary = "获取日志文件列表")
    @GetMapping("/list")
    public R<List<LogFileInfo>> getLogFileList() {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists() || !logDir.isDirectory()) {
                return R.ok(new ArrayList<>());
            }

            File[] files = logDir.listFiles((dir, name) ->
                name.endsWith(".log") || name.endsWith(".txt")
            );

            if (files == null || files.length == 0) {
                return R.ok(new ArrayList<>());
            }

            List<LogFileInfo> logFileList = Arrays.stream(files)
                .map(file -> {
                    LogFileInfo info = new LogFileInfo();
                    info.setFileName(file.getName());
                    info.setFilePath(file.getAbsolutePath());
                    info.setFileSize(file.length());
                    info.setFileSizeStr(formatFileSize(file.length()));
                    info.setLastModified(new Date(file.lastModified()));
                    return info;
                })
                .sorted(Comparator.comparing(LogFileInfo::getLastModified).reversed())
                .collect(Collectors.toList());

            return R.ok(logFileList);
        } catch (Exception e) {
            log.error("获取日志文件列表失败", e);
            return R.fail("获取日志文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 查看日志文件内容
     * @param fileName 文件名
     * @param lines 读取行数（从文件末尾开始，默认1000行）
     */
    @Operation(summary = "查看日志文件内容")
    @GetMapping("/content")
    public R<LogContent> getLogContent(
        @RequestParam String fileName,
        @RequestParam(required = false, defaultValue = "1000") Integer lines
    ) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(LOG_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                return R.fail("日志文件不存在");
            }

            // 读取文件内容（读取最后N行）
            String content = readLastLines(file, lines);

            // 统计总行数
            long totalLines = countLines(file);

            LogContent logContent = new LogContent();
            logContent.setFileName(fileName);
            logContent.setContent(content);
            logContent.setTotalLines(totalLines);
            logContent.setFileSize(file.length());
            logContent.setFileSizeStr(formatFileSize(file.length()));
            logContent.setLastModified(new Date(file.lastModified()));

            return R.ok(logContent);
        } catch (Exception e) {
            log.error("读取日志文件失败", e);
            return R.fail("读取日志文件失败: " + e.getMessage());
        }
    }

    /**
     * 搜索日志内容
     */
    @Operation(summary = "搜索日志内容")
    @GetMapping("/search")
    public R<LogContent> searchLog(
        @RequestParam String fileName,
        @RequestParam String keyword,
        @RequestParam(required = false, defaultValue = "500") Integer maxLines
    ) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(LOG_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                return R.fail("日志文件不存在");
            }

            // 搜索包含关键字的行
            StringBuilder result = new StringBuilder();
            int matchCount = 0;

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null && matchCount < maxLines) {
                    if (line.contains(keyword)) {
                        result.append(line).append("\n");
                        matchCount++;
                    }
                }
            }

            LogContent logContent = new LogContent();
            logContent.setFileName(fileName);
            logContent.setContent(result.toString());
            logContent.setTotalLines((long) matchCount);
            logContent.setFileSize(file.length());
            logContent.setFileSizeStr(formatFileSize(file.length()));
            logContent.setLastModified(new Date(file.lastModified()));

            return R.ok(logContent);
        } catch (Exception e) {
            log.error("搜索日志失败", e);
            return R.fail("搜索日志失败: " + e.getMessage());
        }
    }

    /**
     * 下载日志文件
     */
    @Operation(summary = "下载日志文件")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadLog(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return ResponseEntity.badRequest().build();
            }

            File file = new File(LOG_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(file.length())
                .body(resource);
        } catch (Exception e) {
            log.error("下载日志文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 清空日志文件
     */
    @Operation(summary = "清空日志文件")
    @PostMapping("/clear/{fileName}")
    public R<String> clearLog(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(LOG_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                return R.fail("日志文件不存在");
            }

            // 清空文件内容
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write("");
            }

            log.info("日志文件已清空: {}", fileName);
            return R.ok("日志文件已清空");
        } catch (Exception e) {
            log.error("清空日志文件失败", e);
            return R.fail("清空日志文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除日志文件
     */
    @Operation(summary = "删除日志文件")
    @DeleteMapping("/{fileName}")
    public R<String> deleteLog(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(LOG_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                return R.fail("日志文件不存在");
            }

            if (file.delete()) {
                log.info("日志文件已删除: {}", fileName);
                return R.ok("日志文件已删除");
            } else {
                return R.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除日志文件失败", e);
            return R.fail("删除日志文件失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 读取文件最后N行
     */
    private String readLastLines(File file, int lines) throws IOException {
        if (file.length() == 0) {
            return "";
        }

        // 对于小文件，直接全部读取
        if (file.length() < 10 * 1024 * 1024) { // 10MB
            List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            int start = Math.max(0, allLines.size() - lines);
            return String.join("\n", allLines.subList(start, allLines.size()));
        }

        // 对于大文件，使用RandomAccessFile倒序读取
        List<String> lastLines = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            long pointer = fileLength - 1;
            StringBuilder sb = new StringBuilder();

            while (pointer >= 0 && lastLines.size() < lines) {
                raf.seek(pointer);
                int c = raf.read();

                if (c == '\n' && sb.length() > 0) {
                    lastLines.add(sb.reverse().toString());
                    sb = new StringBuilder();
                } else if (c != '\n' && c != '\r') {
                    sb.append((char) c);
                }
                pointer--;
            }

            if (sb.length() > 0) {
                lastLines.add(sb.reverse().toString());
            }
        }

        Collections.reverse(lastLines);
        return String.join("\n", lastLines);
    }

    /**
     * 统计文件行数
     */
    private long countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return reader.lines().count();
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
