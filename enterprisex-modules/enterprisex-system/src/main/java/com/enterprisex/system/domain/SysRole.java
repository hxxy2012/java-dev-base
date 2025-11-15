package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprisex.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色对象 sys_role
 *
 * @author EnterpriseX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色对象")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "role_id", type = IdType.AUTO)
    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符串")
    private String roleKey;

    @Schema(description = "显示顺序")
    private Integer roleSort;

    @Schema(description = "数据范围：1全部2自定义3本部门4本部门及以下5仅本人")
    private Integer dataScope;

    @Schema(description = "状态：0禁用1正常")
    private Integer status;

    @Schema(description = "删除标志：0正常1删除")
    private Integer delFlag;

    @TableField(exist = false)
    @Schema(description = "菜单ID列表")
    private Long[] menuIds;

    @TableField(exist = false)
    @Schema(description = "部门ID列表")
    private Long[] deptIds;
}
