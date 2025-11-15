package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysOperLog;
import com.enterprisex.system.mapper.SysOperLogMapper;
import com.enterprisex.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 操作日志业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();

        // 模块标题模糊查询
        if (StringUtils.isNotEmpty(operLog.getTitle())) {
            queryWrapper.like(SysOperLog::getTitle, operLog.getTitle());
        }

        // 业务类型查询
        if (operLog.getBusinessType() != null) {
            queryWrapper.eq(SysOperLog::getBusinessType, operLog.getBusinessType());
        }

        // 操作人员模糊查询
        if (StringUtils.isNotEmpty(operLog.getOperName())) {
            queryWrapper.like(SysOperLog::getOperName, operLog.getOperName());
        }

        // 操作状态查询
        if (operLog.getStatus() != null) {
            queryWrapper.eq(SysOperLog::getStatus, operLog.getStatus());
        }

        // 按照操作时间降序
        queryWrapper.orderByDesc(SysOperLog::getOperId);

        return list(queryWrapper);
    }

    @Override
    public void insertOperLog(SysOperLog operLog) {
        baseMapper.insert(operLog);
    }

    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(operIds));
    }

    @Override
    public void cleanOperLog() {
        baseMapper.delete(null);
    }
}
