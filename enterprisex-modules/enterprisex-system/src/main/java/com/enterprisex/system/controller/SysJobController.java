package com.enterprisex.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.page.TableDataInfo;
import com.enterprisex.system.domain.SysJob;
import com.enterprisex.system.domain.SysJobLog;
import com.enterprisex.system.service.SysJobLogService;
import com.enterprisex.system.service.SysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务控制器
 *
 * @author EnterpriseX
 */
@Slf4j
@Tag(name = "定时任务管理")
@RestController
@RequestMapping("/monitor/job")
public class SysJobController {

    @Autowired
    private SysJobService jobService;

    @Autowired
    private SysJobLogService jobLogService;

    /**
     * 查询定时任务列表
     */
    @Operation(summary = "查询定时任务列表")
    @GetMapping("/list")
    public TableDataInfo<SysJob> list(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String jobGroup,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<SysJob> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(jobName), SysJob::getJobName, jobName)
                .eq(StringUtils.isNotBlank(jobGroup), SysJob::getJobGroup, jobGroup)
                .eq(status != null, SysJob::getStatus, status)
                .orderByDesc(SysJob::getCreateTime);

        IPage<SysJob> result = jobService.page(page, queryWrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 获取定时任务详细信息
     */
    @Operation(summary = "获取定时任务详细信息")
    @GetMapping("/{jobId}")
    public R<SysJob> getInfo(@PathVariable Long jobId) {
        return R.ok(jobService.getById(jobId));
    }

    /**
     * 新增定时任务
     */
    @Operation(summary = "新增定时任务")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysJob job) {
        if (!jobService.checkCronExpressionIsValid(job.getCronExpression())) {
            return R.fail("Cron表达式不正确");
        }
        try {
            job.setCreateBy("admin");
            jobService.insertJob(job);
            return R.ok("新增成功");
        } catch (SchedulerException e) {
            log.error("新增定时任务失败", e);
            return R.fail("新增定时任务失败：" + e.getMessage());
        }
    }

    /**
     * 修改定时任务
     */
    @Operation(summary = "修改定时任务")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysJob job) {
        if (!jobService.checkCronExpressionIsValid(job.getCronExpression())) {
            return R.fail("Cron表达式不正确");
        }
        try {
            job.setUpdateBy("admin");
            jobService.updateJob(job);
            return R.ok("修改成功");
        } catch (SchedulerException e) {
            log.error("修改定时任务失败", e);
            return R.fail("修改定时任务失败：" + e.getMessage());
        }
    }

    /**
     * 定时任务状态修改
     */
    @Operation(summary = "定时任务状态修改")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysJob job) {
        try {
            jobService.changeStatus(job);
            return R.ok("操作成功");
        } catch (SchedulerException e) {
            log.error("修改定时任务状态失败", e);
            return R.fail("修改定时任务状态失败：" + e.getMessage());
        }
    }

    /**
     * 定时任务立即执行一次
     */
    @Operation(summary = "定时任务立即执行一次")
    @PostMapping("/run")
    public R<Void> run(@RequestBody SysJob job) {
        try {
            jobService.run(job);
            return R.ok("执行成功");
        } catch (SchedulerException e) {
            log.error("立即执行定时任务失败", e);
            return R.fail("立即执行定时任务失败：" + e.getMessage());
        }
    }

    /**
     * 删除定时任务
     */
    @Operation(summary = "删除定时任务")
    @DeleteMapping("/{jobIds}")
    public R<Void> remove(@PathVariable Long[] jobIds) {
        try {
            jobService.deleteJobByIds(jobIds);
            return R.ok("删除成功");
        } catch (SchedulerException e) {
            log.error("删除定时任务失败", e);
            return R.fail("删除定时任务失败：" + e.getMessage());
        }
    }

    /**
     * 查询定时任务日志列表
     */
    @Operation(summary = "查询定时任务日志列表")
    @GetMapping("/log/list")
    public TableDataInfo<SysJobLog> logList(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String jobGroup,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<SysJobLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(jobName), SysJobLog::getJobName, jobName)
                .eq(StringUtils.isNotBlank(jobGroup), SysJobLog::getJobGroup, jobGroup)
                .eq(status != null, SysJobLog::getStatus, status)
                .orderByDesc(SysJobLog::getCreateTime);

        IPage<SysJobLog> result = jobLogService.page(page, queryWrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 删除定时任务日志
     */
    @Operation(summary = "删除定时任务日志")
    @DeleteMapping("/log/{jobLogIds}")
    public R<Void> removeLog(@PathVariable Long[] jobLogIds) {
        for (Long jobLogId : jobLogIds) {
            jobLogService.removeById(jobLogId);
        }
        return R.ok("删除成功");
    }

    /**
     * 清空定时任务日志
     */
    @Operation(summary = "清空定时任务日志")
    @DeleteMapping("/log/clean")
    public R<Void> cleanLog() {
        jobLogService.cleanJobLog();
        return R.ok("清空成功");
    }
}
