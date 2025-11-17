package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.system.domain.SysJobLog;
import com.enterprisex.system.mapper.SysJobLogMapper;
import com.enterprisex.system.service.SysJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 定时任务日志服务实现
 *
 * @author EnterpriseX
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

    @Autowired
    private SysJobLogMapper jobLogMapper;

    @Override
    public void cleanJobLog() {
        jobLogMapper.delete(null);
    }
}
