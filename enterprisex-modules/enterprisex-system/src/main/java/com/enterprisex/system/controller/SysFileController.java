package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件管理控制器
 * 提供简单的文件上传下载功能
 *
 * @author EnterpriseX
 */
@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/system/file")
public class SysFileController {

    /**
     * 文件上传路径
     */
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 文件上传
     */
    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return R.fail("上传文件不能为空");
            }

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return R.fail("文件名不能为空");
            }

            // 获取文件扩展名
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            // 生成新文件名 (UUID + 扩展名)
            String newFileName = UUID.randomUUID().toString() + extension;

            // 按日期组织文件夹 yyyy/MM/dd
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String filePath = uploadPath + File.separator + datePath;

            // 创建目录
            Path directory = Paths.get(filePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // 保存文件
            File dest = new File(filePath + File.separator + newFileName);
            file.transferTo(dest);

            // 返回文件信息
            Map<String, String> result = new HashMap<>();
            result.put("fileName", originalFilename);
            result.put("filePath", datePath + "/" + newFileName);
            result.put("fileSize", String.valueOf(file.getSize()));
            result.put("fileType", file.getContentType());

            log.info("文件上传成功: {}", originalFilename);
            return R.ok("上传成功", result);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @Operation(summary = "文件下载")
    @GetMapping("/download")
    public void download(@RequestParam String filePath, HttpServletResponse response) {
        try {
            // 构建文件完整路径
            File file = new File(uploadPath + File.separator + filePath);

            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + file.getName());
            response.setContentLengthLong(file.length());

            // 读取文件并写入响应流
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            log.info("文件下载成功: {}", filePath);

        } catch (Exception e) {
            log.error("文件下载失败: {}", filePath, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除文件
     */
    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public R<Void> delete(@RequestParam String filePath) {
        try {
            File file = new File(uploadPath + File.separator + filePath);

            if (!file.exists()) {
                return R.fail("文件不存在");
            }

            if (file.delete()) {
                log.info("文件删除成功: {}", filePath);
                return R.ok("删除成功");
            } else {
                return R.fail("删除失败");
            }

        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            return R.fail("文件删除失败: " + e.getMessage());
        }
    }
}
