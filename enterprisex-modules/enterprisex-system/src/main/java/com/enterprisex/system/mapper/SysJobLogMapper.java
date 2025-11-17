package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
}
