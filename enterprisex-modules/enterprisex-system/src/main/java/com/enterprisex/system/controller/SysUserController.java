package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取用户列表
     */
    @Operation(summary = "查询用户列表")
    @GetMapping("/list")
    public TableDataInfo<SysUser> list(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据用户ID获取详细信息
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public R<SysUser> getInfo(@PathVariable Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        return R.ok(user);
    }

    /**
     * 新增用户
     */
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增用户")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysUser user) {
        return R.toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改用户")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysUser user) {
        return R.toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除用户")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        return R.toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        // TODO: 实现密码重置逻辑（使用BCrypt加密）
        return R.ok("密码重置成功");
    }

    /**
     * 状态修改
     */
    @Operation(summary = "修改用户状态")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        SysUser updateUser = new SysUser();
        updateUser.setUserId(user.getUserId());
        updateUser.setStatus(user.getStatus());
        return R.toAjax(userService.updateById(updateUser));
    }
}
