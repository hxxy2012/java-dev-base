package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单权限表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
