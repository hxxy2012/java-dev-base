package com.enterprisex.auth.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由菜单视图对象
 *
 * @author EnterpriseX
 */
@Data
public class RouterVo {

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单类型：M目录C菜单F按钮
     */
    private String menuType;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 是否显示：0隐藏1显示
     */
    private Integer visible;

    /**
     * 元信息
     */
    private MetaVo meta;

    /**
     * 子菜单
     */
    private List<RouterVo> children = new ArrayList<>();

    /**
     * 路由元信息
     */
    @Data
    public static class MetaVo {
        /**
         * 菜单标题
         */
        private String title;

        /**
         * 菜单图标
         */
        private String icon;

        /**
         * 是否隐藏
         */
        private Boolean hidden;
    }
}
