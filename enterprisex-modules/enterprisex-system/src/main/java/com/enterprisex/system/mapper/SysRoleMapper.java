package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
