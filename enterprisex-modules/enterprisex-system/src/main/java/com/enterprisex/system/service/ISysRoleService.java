package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysRole;

import java.util.List;

/**
 * 角色业务层
 *
 * @author EnterpriseX
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 新增角色
     */
    int insertRole(SysRole role);

    /**
     * 修改角色
     */
    int updateRole(SysRole role);

    /**
     * 删除角色
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限字符串是否唯一
     */
    boolean checkRoleKeyUnique(SysRole role);
}
