package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysNotice;
import com.enterprisex.system.service.ISysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "通知公告管理")
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @Operation(summary = "查询通知公告列表")
    @GetMapping("/list")
    public TableDataInfo<SysNotice> list(SysNotice notice) {
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @Operation(summary = "获取通知公告详情")
    @GetMapping("/{noticeId}")
    public R<SysNotice> getInfo(@PathVariable Long noticeId) {
        SysNotice notice = noticeService.getById(noticeId);
        if (notice == null) {
            return R.fail("通知公告不存在");
        }
        return R.ok(notice);
    }

    /**
     * 新增通知公告
     */
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @Operation(summary = "新增通知公告")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysNotice notice) {
        return R.toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改通知公告")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysNotice notice) {
        return R.toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     */
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        return R.toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}
