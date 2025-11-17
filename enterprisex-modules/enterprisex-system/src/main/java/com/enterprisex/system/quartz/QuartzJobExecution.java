package com.enterprisex.system.quartz;

import com.enterprisex.system.domain.SysJob;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;

/**
 * 定时任务处理（允许并发执行）
 *
 * @author EnterpriseX
 */
public class QuartzJobExecution extends AbstractQuartzJob {

    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
