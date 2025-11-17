package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.system.config.MinioConfig;
import com.enterprisex.system.domain.SysFile;
import com.enterprisex.system.mapper.SysFileMapper;
import com.enterprisex.system.service.SysFileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 文件信息Service业务层处理
 */
@Slf4j
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 初始化，创建存储桶
     */
    @PostConstruct
    public void init() {
        try {
            String bucketName = minioConfig.getBucketName();
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Created MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to create MinIO bucket", e);
        }
    }

    @Override
    public SysFile uploadFile(MultipartFile file) {
        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成文件名（日期 + UUID）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String filePath = datePath + "/" + fileName;

            // 上传到MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 生成文件URL
            String fileUrl = String.format("/system/file/download/%s", filePath);

            // 保存文件记录到数据库
            SysFile sysFile = new SysFile();
            sysFile.setFileName(fileName);
            sysFile.setOriginalName(originalFilename);
            sysFile.setFilePath(filePath);
            sysFile.setFileUrl(fileUrl);
            sysFile.setFileSize(file.getSize());
            sysFile.setFileType(file.getContentType());
            sysFile.setFileExt(extension);
            sysFile.setStorageLocation("minio");

            this.save(sysFile);

            return sysFile;
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(Long fileId) {
        try {
            SysFile sysFile = this.getById(fileId);
            if (sysFile == null) {
                throw new ServiceException("文件不存在");
            }

            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(sysFile.getFilePath())
                    .build());
        } catch (Exception e) {
            log.error("Failed to download file", e);
            throw new ServiceException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(Long fileId) {
        try {
            SysFile sysFile = this.getById(fileId);
            if (sysFile == null) {
                throw new ServiceException("文件不存在");
            }

            // 从MinIO删除文件
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(sysFile.getFilePath())
                    .build());

            // 从数据库删除记录
            return this.removeById(fileId);
        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new ServiceException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFiles(List<Long> fileIds) {
        fileIds.forEach(this::deleteFile);
        return true;
    }

    @Override
    public List<SysFile> selectFileList(SysFile sysFile) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(sysFile.getFileName() != null, SysFile::getFileName, sysFile.getFileName())
                .like(sysFile.getOriginalName() != null, SysFile::getOriginalName, sysFile.getOriginalName())
                .eq(sysFile.getFileType() != null, SysFile::getFileType, sysFile.getFileType())
                .orderByDesc(SysFile::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public SysFile selectFileById(Long fileId) {
        return this.getById(fileId);
    }

    @Override
    public String getFileUrl(Long fileId) {
        try {
            SysFile sysFile = this.getById(fileId);
            if (sysFile == null) {
                throw new ServiceException("文件不存在");
            }

            // 生成预签名URL（有效期7天）
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(sysFile.getFilePath())
                    .method(io.minio.http.Method.GET)
                    .expiry(7 * 24 * 60 * 60)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get file URL", e);
            throw new ServiceException("获取文件URL失败: " + e.getMessage());
        }
    }
}
