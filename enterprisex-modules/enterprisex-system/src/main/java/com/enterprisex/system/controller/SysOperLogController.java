package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysOperLog;
import com.enterprisex.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController {

    @Autowired
    private ISysOperLogService operLogService;

    /**
     * 获取操作日志列表
     */
    @Operation(summary = "查询操作日志列表")
    @GetMapping("/list")
    public TableDataInfo<SysOperLog> list(SysOperLog operLog) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据操作日志ID获取详细信息
     */
    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{operId}")
    public R<SysOperLog> getInfo(@PathVariable Long operId) {
        SysOperLog operLog = operLogService.getById(operId);
        if (operLog == null) {
            return R.fail("操作日志不存在");
        }
        return R.ok(operLog);
    }

    /**
     * 删除操作日志
     */
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        return R.toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }
}
