package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprisex.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件信息对象 sys_file
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件ID */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /** 文件名称 */
    private String fileName;

    /** 原始文件名 */
    private String originalName;

    /** 文件路径 */
    private String filePath;

    /** 文件URL */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 文件扩展名 */
    private String fileExt;

    /** 存储位置（minio/local） */
    private String storageLocation;

    /** 上传者 */
    private String uploadBy;

    /** 上传IP */
    private String uploadIp;
}
