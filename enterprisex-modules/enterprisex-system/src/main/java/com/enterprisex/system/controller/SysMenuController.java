package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.system.domain.SysMenu;
import com.enterprisex.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @Operation(summary = "查询菜单列表")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> list = menuService.selectMenuList(menu);
        return R.ok(list);
    }

    /**
     * 获取菜单树结构
     */
    @Operation(summary = "查询菜单树")
    @GetMapping("/tree")
    public R<List<SysMenu>> tree(SysMenu menu) {
        List<SysMenu> list = menuService.selectMenuList(menu);
        List<SysMenu> tree = menuService.buildMenuTree(list);
        return R.ok(tree);
    }

    /**
     * 根据菜单ID获取详细信息
     */
    @Operation(summary = "获取菜单详情")
    @GetMapping("/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        SysMenu menu = menuService.getById(menuId);
        if (menu == null) {
            return R.fail("菜单不存在");
        }
        return R.ok(menu);
    }

    /**
     * 新增菜单
     */
    @Operation(summary = "新增菜单")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysMenu menu) {
        return R.toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @Operation(summary = "修改菜单")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysMenu menu) {
        return R.toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        return R.toAjax(menuService.deleteMenuById(menuId));
    }
}
