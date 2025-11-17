package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysJob;
import org.quartz.SchedulerException;

/**
 * 定时任务服务接口
 *
 * @author EnterpriseX
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int pauseJob(SysJob job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int resumeJob(SysJob job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    int deleteJob(SysJob job) throws SchedulerException;

    /**
     * 批量删除调度信息
     *
     * @param jobIds 需要删除的任务ID
     * @return 结果
     */
    void deleteJobByIds(Long[] jobIds) throws SchedulerException;

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     * @return 结果
     */
    int changeStatus(SysJob job) throws SchedulerException;

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void run(SysJob job) throws SchedulerException;

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     * @return 结果
     */
    int insertJob(SysJob job) throws SchedulerException;

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     * @return 结果
     */
    int updateJob(SysJob job) throws SchedulerException;

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    boolean checkCronExpressionIsValid(String cronExpression);
}
