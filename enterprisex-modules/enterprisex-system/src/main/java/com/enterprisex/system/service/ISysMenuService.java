package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysMenu;

import java.util.List;

/**
 * 菜单业务层
 *
 * @author EnterpriseX
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 构建菜单树
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 是否存在子菜单
     */
    boolean hasChildByMenuId(Long menuId);
}
