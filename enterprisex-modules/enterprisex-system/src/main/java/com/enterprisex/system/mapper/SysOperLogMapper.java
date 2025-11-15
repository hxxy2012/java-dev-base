package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
