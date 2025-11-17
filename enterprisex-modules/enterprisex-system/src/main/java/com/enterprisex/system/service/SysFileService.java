package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件信息Service接口
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 上传文件到MinIO
     *
     * @param file 文件
     * @return 文件信息
     */
    SysFile uploadFile(MultipartFile file);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件流
     */
    InputStream downloadFile(Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 结果
     */
    boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 结果
     */
    boolean deleteFiles(List<Long> fileIds);

    /**
     * 查询文件列表
     *
     * @param sysFile 文件信息
     * @return 文件列表
     */
    List<SysFile> selectFileList(SysFile sysFile);

    /**
     * 根据ID查询文件
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    SysFile selectFileById(Long fileId);

    /**
     * 获取文件下载URL
     *
     * @param fileId 文件ID
     * @return 下载URL
     */
    String getFileUrl(Long fileId);
}
