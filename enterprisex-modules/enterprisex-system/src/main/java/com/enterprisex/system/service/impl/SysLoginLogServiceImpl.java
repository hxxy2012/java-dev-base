package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysLoginLog;
import com.enterprisex.system.mapper.SysLoginLogMapper;
import com.enterprisex.system.service.ISysLoginLogService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 登录日志业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

    @Override
    public List<SysLoginLog> selectLoginLogList(SysLoginLog loginLog) {
        LambdaQueryWrapper<SysLoginLog> queryWrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StringUtils.isNotEmpty(loginLog.getUsername())) {
            queryWrapper.like(SysLoginLog::getUsername, loginLog.getUsername());
        }

        // IP地址模糊查询
        if (StringUtils.isNotEmpty(loginLog.getIpaddr())) {
            queryWrapper.like(SysLoginLog::getIpaddr, loginLog.getIpaddr());
        }

        // 登录状态查询
        if (loginLog.getStatus() != null) {
            queryWrapper.eq(SysLoginLog::getStatus, loginLog.getStatus());
        }

        // 按照登录时间降序
        queryWrapper.orderByDesc(SysLoginLog::getInfoId);

        return list(queryWrapper);
    }

    @Override
    public void insertLoginLog(SysLoginLog loginLog) {
        baseMapper.insert(loginLog);
    }

    @Override
    public int deleteLoginLogByIds(Long[] infoIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    @Override
    public void cleanLoginLog() {
        baseMapper.delete(null);
    }
}
