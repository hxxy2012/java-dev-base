package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysOperLog;

import java.util.List;

/**
 * 操作日志业务层
 *
 * @author EnterpriseX
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 查询操作日志列表
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 新增操作日志
     */
    void insertOperLog(SysOperLog operLog);

    /**
     * 删除操作日志
     */
    int deleteOperLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
