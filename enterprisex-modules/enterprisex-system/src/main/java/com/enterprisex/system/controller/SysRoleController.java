package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysRole;
import com.enterprisex.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 获取角色列表
     */
    @Operation(summary = "查询角色列表")
    @GetMapping("/list")
    public TableDataInfo<SysRole> list(SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据角色ID获取详细信息
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{roleId}")
    public R<SysRole> getInfo(@PathVariable Long roleId) {
        SysRole role = roleService.getById(roleId);
        if (role == null) {
            return R.fail("角色不存在");
        }
        return R.ok(role);
    }

    /**
     * 新增角色
     */
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增角色")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysRole role) {
        return R.toAjax(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改角色")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysRole role) {
        return R.toAjax(roleService.updateRole(role));
    }

    /**
     * 删除角色
     */
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        return R.toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 状态修改
     */
    @Operation(summary = "修改角色状态")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRole role) {
        SysRole updateRole = new SysRole();
        updateRole.setRoleId(role.getRoleId());
        updateRole.setStatus(role.getStatus());
        return R.toAjax(roleService.updateById(updateRole));
    }
}
