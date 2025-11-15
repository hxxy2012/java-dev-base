package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysMenu;
import com.enterprisex.system.mapper.SysMenuMapper;
import com.enterprisex.system.service.ISysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        // 菜单名称模糊查询
        if (StringUtils.isNotEmpty(menu.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }

        // 菜单类型查询
        if (StringUtils.isNotEmpty(menu.getMenuType())) {
            queryWrapper.eq(SysMenu::getMenuType, menu.getMenuType());
        }

        // 状态查询
        if (menu.getStatus() != null) {
            queryWrapper.eq(SysMenu::getStatus, menu.getStatus());
        }

        // 父菜单ID查询
        if (menu.getParentId() != null) {
            queryWrapper.eq(SysMenu::getParentId, menu.getParentId());
        }

        // 按照排序字段升序
        queryWrapper.orderByAsc(SysMenu::getOrderNum);

        return list(queryWrapper);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());

        for (SysMenu menu : menus) {
            // 如果是顶级节点（父菜单不在当前列表中），直接添加到结果集
            if (menu.getParentId() == null || menu.getParentId() == 0L || !tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }

        if (returnList.isEmpty()) {
            returnList = menus;
        }

        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu menu) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, menu);
        menu.setChildren(childList);

        for (SysMenu child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu menu) {
        List<SysMenu> childList = new ArrayList<>();

        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(menu.getMenuId())) {
                childList.add(n);
            }
        }

        return childList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu menu) {
        return getChildList(list, menu).size() > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMenu(SysMenu menu) {
        // 校验菜单名称唯一性
        if (!checkMenuNameUnique(menu)) {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }

        return baseMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenu(SysMenu menu) {
        // 校验菜单名称唯一性
        if (!checkMenuNameUnique(menu)) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }

        // 不能将自己设置为父菜单
        if (menu.getMenuId() != null && menu.getMenuId().equals(menu.getParentId())) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，不能将自己设置为父菜单");
        }

        return baseMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMenuById(Long menuId) {
        // 存在子菜单，不允许删除
        if (hasChildByMenuId(menuId)) {
            throw new ServiceException("存在子菜单，不允许删除");
        }

        return baseMapper.deleteById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        queryWrapper.eq(SysMenu::getParentId, menu.getParentId());

        if (menu.getMenuId() != null) {
            queryWrapper.ne(SysMenu::getMenuId, menu.getMenuId());
        }

        return count(queryWrapper) == 0;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, menuId);
        return count(queryWrapper) > 0;
    }
}
