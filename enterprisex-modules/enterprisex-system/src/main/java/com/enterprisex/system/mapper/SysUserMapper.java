package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
