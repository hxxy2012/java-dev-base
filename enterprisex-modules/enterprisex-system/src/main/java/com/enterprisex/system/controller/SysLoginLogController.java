package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysLoginLog;
import com.enterprisex.system.service.ISysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/system/loginlog")
public class SysLoginLogController {

    @Autowired
    private ISysLoginLogService loginLogService;

    /**
     * 获取登录日志列表
     */
    @Operation(summary = "查询登录日志列表")
    @GetMapping("/list")
    public TableDataInfo<SysLoginLog> list(SysLoginLog loginLog) {
        List<SysLoginLog> list = loginLogService.selectLoginLogList(loginLog);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 新增登录日志
     */
    @Operation(summary = "新增登录日志")
    @PostMapping
    public R<Void> add(@RequestBody SysLoginLog loginLog) {
        loginLogService.insertLoginLog(loginLog);
        return R.ok();
    }

    /**
     * 根据登录日志ID获取详细信息
     */
    @Operation(summary = "获取登录日志详情")
    @GetMapping("/{infoId}")
    public R<SysLoginLog> getInfo(@PathVariable Long infoId) {
        SysLoginLog loginLog = loginLogService.getById(infoId);
        if (loginLog == null) {
            return R.fail("登录日志不存在");
        }
        return R.ok(loginLog);
    }

    /**
     * 删除登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return R.toAjax(loginLogService.deleteLoginLogByIds(infoIds));
    }

    /**
     * 清空登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        loginLogService.cleanLoginLog();
        return R.ok();
    }
}
