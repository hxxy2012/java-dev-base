package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库备份与恢复控制器
 */
@Tag(name = "数据库备份管理", description = "数据库备份与恢复接口")
@Slf4j
@RestController
@RequestMapping("/system/backup")
public class BackupController {

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/enterprisex}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:root}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:root123}")
    private String datasourcePassword;

    // 备份文件存储目录
    private static final String BACKUP_DIR = System.getProperty("user.home") + "/enterprisex-backups/";

    /**
     * 备份信息DTO
     */
    @Data
    public static class BackupInfo {
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String fileSizeStr;
        private Date createTime;
    }

    /**
     * 创建数据库备份
     */
    @Operation(summary = "创建数据库备份")
    @PostMapping("/create")
    public R<String> createBackup() {
        try {
            // 确保备份目录存在
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // 从URL中提取数据库名
            String dbName = extractDatabaseName(datasourceUrl);
            String dbHost = extractDatabaseHost(datasourceUrl);
            String dbPort = extractDatabasePort(datasourceUrl);

            // 生成备份文件名
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = dbName + "_backup_" + timestamp + ".sql";
            String filePath = BACKUP_DIR + fileName;

            // 构建mysqldump命令
            List<String> commands = new ArrayList<>();

            // 检查操作系统
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                commands.add("cmd.exe");
                commands.add("/c");
            } else {
                commands.add("/bin/sh");
                commands.add("-c");
            }

            // mysqldump命令
            String mysqldumpCmd = String.format(
                "mysqldump -h%s -P%s -u%s -p%s --databases %s --result-file=%s",
                dbHost, dbPort, datasourceUsername, datasourcePassword, dbName, filePath
            );
            commands.add(mysqldumpCmd);

            // 执行命令
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("数据库备份成功: {}", fileName);
                return R.ok("备份创建成功", fileName);
            } else {
                log.error("数据库备份失败: {}", output.toString());
                // 如果备份失败，删除可能生成的空文件
                Files.deleteIfExists(Paths.get(filePath));
                return R.fail("备份失败: mysqldump命令不可用或数据库连接失败");
            }
        } catch (Exception e) {
            log.error("创建数据库备份失败", e);
            return R.fail("创建备份失败: " + e.getMessage());
        }
    }

    /**
     * 获取备份列表
     */
    @Operation(summary = "获取备份列表")
    @GetMapping("/list")
    public R<List<BackupInfo>> getBackupList() {
        try {
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                return R.ok(new ArrayList<>());
            }

            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
            if (files == null || files.length == 0) {
                return R.ok(new ArrayList<>());
            }

            List<BackupInfo> backupList = Arrays.stream(files)
                .map(file -> {
                    BackupInfo info = new BackupInfo();
                    info.setFileName(file.getName());
                    info.setFilePath(file.getAbsolutePath());
                    info.setFileSize(file.length());
                    info.setFileSizeStr(formatFileSize(file.length()));
                    info.setCreateTime(new Date(file.lastModified()));
                    return info;
                })
                .sorted(Comparator.comparing(BackupInfo::getCreateTime).reversed())
                .collect(Collectors.toList());

            return R.ok(backupList);
        } catch (Exception e) {
            log.error("获取备份列表失败", e);
            return R.fail("获取备份列表失败: " + e.getMessage());
        }
    }

    /**
     * 下载备份文件
     */
    @Operation(summary = "下载备份文件")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return ResponseEntity.badRequest().build();
            }

            File file = new File(BACKUP_DIR + fileName);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
        } catch (Exception e) {
            log.error("下载备份文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 恢复数据库备份
     */
    @Operation(summary = "恢复数据库备份")
    @PostMapping("/restore/{fileName}")
    public R<String> restoreBackup(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(BACKUP_DIR + fileName);
            if (!file.exists()) {
                return R.fail("备份文件不存在");
            }

            // 从URL中提取数据库信息
            String dbHost = extractDatabaseHost(datasourceUrl);
            String dbPort = extractDatabasePort(datasourceUrl);

            // 构建mysql命令
            List<String> commands = new ArrayList<>();

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                commands.add("cmd.exe");
                commands.add("/c");
            } else {
                commands.add("/bin/sh");
                commands.add("-c");
            }

            String mysqlCmd = String.format(
                "mysql -h%s -P%s -u%s -p%s < %s",
                dbHost, dbPort, datasourceUsername, datasourcePassword, file.getAbsolutePath()
            );
            commands.add(mysqlCmd);

            // 执行命令
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("数据库恢复成功: {}", fileName);
                return R.ok("数据库恢复成功");
            } else {
                log.error("数据库恢复失败: {}", output.toString());
                return R.fail("恢复失败: mysql命令不可用或SQL文件格式错误");
            }
        } catch (Exception e) {
            log.error("恢复数据库备份失败", e);
            return R.fail("恢复备份失败: " + e.getMessage());
        }
    }

    /**
     * 删除备份文件
     */
    @Operation(summary = "删除备份文件")
    @DeleteMapping("/{fileName}")
    public R<String> deleteBackup(@PathVariable String fileName) {
        try {
            // 验证文件名安全性
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return R.fail("非法的文件名");
            }

            File file = new File(BACKUP_DIR + fileName);
            if (!file.exists()) {
                return R.fail("备份文件不存在");
            }

            if (file.delete()) {
                log.info("删除备份文件成功: {}", fileName);
                return R.ok("删除成功");
            } else {
                return R.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除备份文件失败", e);
            return R.fail("删除失败: " + e.getMessage());
        }
    }

    /**
     * 上传并恢复备份文件
     */
    @Operation(summary = "上传并恢复备份文件")
    @PostMapping("/upload")
    public R<String> uploadAndRestore(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return R.fail("上传文件为空");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".sql")) {
                return R.fail("只支持.sql格式的备份文件");
            }

            // 保存文件
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String newFileName = "upload_" + timestamp + "_" + fileName;
            File destFile = new File(BACKUP_DIR + newFileName);
            file.transferTo(destFile);

            log.info("备份文件上传成功: {}", newFileName);
            return R.ok("上传成功", newFileName);
        } catch (Exception e) {
            log.error("上传备份文件失败", e);
            return R.fail("上传失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private String extractDatabaseName(String url) {
        // jdbc:mysql://localhost:3306/enterprisex?useUnicode=true...
        int lastSlash = url.lastIndexOf("/");
        int questionMark = url.indexOf("?", lastSlash);
        if (questionMark > 0) {
            return url.substring(lastSlash + 1, questionMark);
        } else {
            return url.substring(lastSlash + 1);
        }
    }

    private String extractDatabaseHost(String url) {
        // jdbc:mysql://localhost:3306/enterprisex
        int start = url.indexOf("//") + 2;
        int end = url.indexOf(":", start);
        if (end > 0) {
            return url.substring(start, end);
        } else {
            end = url.indexOf("/", start);
            return url.substring(start, end);
        }
    }

    private String extractDatabasePort(String url) {
        // jdbc:mysql://localhost:3306/enterprisex
        int start = url.indexOf("//") + 2;
        int portStart = url.indexOf(":", start);
        if (portStart > 0) {
            int portEnd = url.indexOf("/", portStart);
            return url.substring(portStart + 1, portEnd);
        }
        return "3306"; // 默认端口
    }

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
