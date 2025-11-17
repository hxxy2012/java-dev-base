package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysJobLog;

/**
 * 定时任务日志服务接口
 *
 * @author EnterpriseX
 */
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 清空任务日志
     */
    void cleanJobLog();
}
