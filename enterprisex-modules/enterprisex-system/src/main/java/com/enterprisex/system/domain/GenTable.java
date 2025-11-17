package com.enterprisex.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代码生成业务表
 *
 * @author EnterpriseX
 */
@Data
public class GenTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成业务名
     */
    private String businessName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成作者
     */
    private String functionAuthor;

    /**
     * 主键信息
     */
    private String pkColumn;

    /**
     * 主键Java字段名
     */
    private String pkJavaField;

    /**
     * 主键Java类型
     */
    private String pkJavaType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
