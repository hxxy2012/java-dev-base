package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysFile;
import com.enterprisex.system.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理Controller
 */
@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/system/file")
public class FileController {

    @Autowired
    private SysFileService sysFileService;

    /**
     * 查询文件列表
     */
    @Operation(summary = "查询文件列表")
    @GetMapping("/list")
    public TableDataInfo<SysFile> list(SysFile sysFile) {
        List<SysFile> list = sysFileService.selectFileList(sysFile);
        return TableDataInfo.success(list);
    }

    /**
     * 获取文件详细信息
     */
    @Operation(summary = "获取文件详细信息")
    @GetMapping("/{fileId}")
    public R<SysFile> getInfo(@PathVariable Long fileId) {
        return R.ok(sysFileService.selectFileById(fileId));
    }

    /**
     * 上传文件
     */
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<SysFile> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        // 文件大小限制 100MB
        if (file.getSize() > 100 * 1024 * 1024) {
            return R.fail("文件大小不能超过100MB");
        }

        SysFile sysFile = sysFileService.uploadFile(file);
        return R.ok(sysFile);
    }

    /**
     * 下载文件
     */
    @Operation(summary = "下载文件")
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            SysFile sysFile = sysFileService.selectFileById(fileId);
            if (sysFile == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            InputStream inputStream = sysFileService.downloadFile(fileId);

            // 设置响应头
            response.setContentType(sysFile.getFileType());
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(sysFile.getOriginalName(), StandardCharsets.UTF_8));

            // 将文件流写入响应
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            inputStream.close();
        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取文件URL
     */
    @Operation(summary = "获取文件URL")
    @GetMapping("/url/{fileId}")
    public R<String> getFileUrl(@PathVariable Long fileId) {
        String url = sysFileService.getFileUrl(fileId);
        return R.ok(url);
    }

    /**
     * 删除文件
     */
    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileIds}")
    public R<Void> remove(@PathVariable List<Long> fileIds) {
        sysFileService.deleteFiles(fileIds);
        return R.ok();
    }
}
