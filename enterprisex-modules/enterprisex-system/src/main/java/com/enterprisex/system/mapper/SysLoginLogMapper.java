package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
