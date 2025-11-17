package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
}
