package com.enterprisex.common.core.domain.model;

import lombok.Data;

import java.util.Set;

/**
 * 数据权限上下文
 *
 * @author EnterpriseX
 */
@Data
public class DataScopeContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 数据范围：1全部2自定义3本部门4本部门及以下5仅本人
     */
    private Integer dataScope;

    /**
     * 自定义数据权限的部门ID列表（当dataScope=2时使用）
     */
    private Set<Long> deptIds;

    /**
     * 是否是超级管理员
     */
    private boolean isAdmin;

    /**
     * SQL条件
     */
    private String sqlCondition;
}
