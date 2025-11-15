package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
}
