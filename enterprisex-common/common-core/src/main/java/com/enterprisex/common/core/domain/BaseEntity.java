package com.enterprisex.common.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础实体类
 *
 * @author EnterpriseX
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 搜索参数（用于分页查询，不存储到数据库）
     */
    @JsonIgnore
    private Map<String, Object> params = new HashMap<>();

    /**
     * 分页参数（页码）
     */
    @JsonIgnore
    private Integer pageNum;

    /**
     * 分页参数（每页数量）
     */
    @JsonIgnore
    private Integer pageSize;

    /**
     * 排序列
     */
    @JsonIgnore
    private String orderByColumn;

    /**
     * 排序方向 desc 或 asc
     */
    @JsonIgnore
    private String isAsc = "asc";
}
