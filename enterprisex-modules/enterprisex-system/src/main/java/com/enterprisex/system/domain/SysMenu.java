package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprisex.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限对象 sys_menu
 *
 * @author EnterpriseX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单权限对象")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型：M目录C菜单F按钮")
    private String menuType;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "是否显示：0隐藏1显示")
    private Integer visible;

    @Schema(description = "状态：0禁用1正常")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "子菜单")
    private List<SysMenu> children = new ArrayList<>();
}
