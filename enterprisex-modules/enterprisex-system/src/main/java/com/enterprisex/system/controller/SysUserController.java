package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.common.excel.utils.ExcelUtil;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        if (user.getUserId() == null) {
            return R.fail("用户ID不能为空");
        }

        // 检查用户是否存在
        SysUser existUser = userService.getById(user.getUserId());
        if (existUser == null) {
            return R.fail("用户不存在");
        }

        // 检查是否为超级管理员（ID=1）
        if (existUser.getUserId() == 1L) {
            return R.fail("不允许重置超级管理员密码");
        }

        // 获取新密码，如果未提供则使用默认密码
        String newPassword = user.getPassword();
        if (newPassword == null || newPassword.isEmpty()) {
            newPassword = "123456"; // 默认密码
        }

        // 使用BCrypt加密密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        existUser.setPassword(encodedPassword);

        // 更新用户密码
        boolean success = userService.updateById(existUser);
        return R.toAjax(success);
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

    /**
     * 导出用户数据
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出用户数据")
    @GetMapping("/export")
    public void export(SysUser user, HttpServletResponse response) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil.exportExcel(response, list, SysUser.class, "用户数据");
    }

    /**
     * 下载用户导入模板
     */
    @Operation(summary = "下载用户导入模板")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        List<SysUser> list = new ArrayList<>();
        ExcelUtil.exportExcel(response, list, SysUser.class, "用户导入模板");
    }

    /**
     * 导入用户数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @Operation(summary = "导入用户数据")
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<SysUser> userList = ExcelUtil.importExcel(file.getInputStream(), SysUser.class);
            if (userList == null || userList.isEmpty()) {
                return R.fail("导入数据为空");
            }

            int successCount = 0;
            int failCount = 0;
            StringBuilder failMsg = new StringBuilder();

            for (SysUser user : userList) {
                try {
                    // 设置默认密码（如果为空）
                    if (user.getPassword() == null || user.getPassword().isEmpty()) {
                        user.setPassword("123456");
                    }
                    // 设置默认状态（如果为空）
                    if (user.getStatus() == null) {
                        user.setStatus(1);
                    }
                    userService.insertUser(user);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failMsg.append("用户名: ").append(user.getUserName())
                           .append(" - ").append(e.getMessage())
                           .append("<br/>");
                }
            }

            if (failCount > 0) {
                return R.fail(String.format("导入完成，成功%d条，失败%d条。<br/>失败原因：<br/>%s",
                    successCount, failCount, failMsg.toString()));
            } else {
                return R.ok(String.format("导入成功，共导入%d条数据", successCount));
            }
        } catch (Exception e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }
}
