package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysLoginLog;

import java.util.List;

/**
 * 登录日志业务层
 *
 * @author EnterpriseX
 */
public interface ISysLoginLogService extends IService<SysLoginLog> {

    /**
     * 查询登录日志列表
     */
    List<SysLoginLog> selectLoginLogList(SysLoginLog loginLog);

    /**
     * 新增登录日志
     */
    void insertLoginLog(SysLoginLog loginLog);

    /**
     * 删除登录日志
     */
    int deleteLoginLogByIds(Long[] infoIds);

    /**
     * 清空登录日志
     */
    void cleanLoginLog();
}
