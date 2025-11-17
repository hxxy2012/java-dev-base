package com.enterprisex.system.quartz;

import com.enterprisex.system.domain.SysJob;
import com.enterprisex.system.domain.SysJobLog;
import com.enterprisex.system.service.SysJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 抽象quartz调用
 *
 * @author EnterpriseX
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {

    /**
     * 线程本地变量
     */
    private static ThreadLocal<LocalDateTime> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJob sysJob = new SysJob();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), sysJob);

        try {
            before(context, sysJob);
            doExecute(context, sysJob);
            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        threadLocal.set(LocalDateTime.now());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @param e       异常
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        LocalDateTime startTime = threadLocal.get();
        threadLocal.remove();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setCreateTime(LocalDateTime.now());

        long runMs = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
        sysJobLog.setJobMessage(sysJob.getJobName() + " 总共耗时：" + runMs + "毫秒");

        if (e != null) {
            sysJobLog.setStatus(0);
            String errorMsg = org.apache.commons.lang3.StringUtils.substring(e.getMessage(), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(1);
        }

        // 写入数据库当中
        SysJobLogService jobLogService = SpringUtils.getBean(SysJobLogService.class);
        jobLogService.save(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
