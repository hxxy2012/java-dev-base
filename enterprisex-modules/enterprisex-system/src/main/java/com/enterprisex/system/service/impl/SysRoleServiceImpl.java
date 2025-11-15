package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysRole;
import com.enterprisex.system.mapper.SysRoleMapper;
import com.enterprisex.system.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        // 角色名称模糊查询
        if (StringUtils.isNotEmpty(role.getRoleName())) {
            queryWrapper.like(SysRole::getRoleName, role.getRoleName());
        }

        // 角色权限字符串查询
        if (StringUtils.isNotEmpty(role.getRoleKey())) {
            queryWrapper.like(SysRole::getRoleKey, role.getRoleKey());
        }

        // 状态查询
        if (role.getStatus() != null) {
            queryWrapper.eq(SysRole::getStatus, role.getStatus());
        }

        queryWrapper.orderByAsc(SysRole::getRoleSort);

        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 校验角色名称唯一性
        if (!checkRoleNameUnique(role)) {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }

        // 校验角色权限字符串唯一性
        if (!checkRoleKeyUnique(role)) {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色权限字符串已存在");
        }

        return baseMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        // 不能修改超级管理员角色
        if (role.getRoleId() != null && role.getRoleId() == 1L) {
            throw new ServiceException("不允许修改超级管理员角色");
        }

        // 校验角色名称唯一性
        if (!checkRoleNameUnique(role)) {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }

        // 校验角色权限字符串唯一性
        if (!checkRoleKeyUnique(role)) {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色权限字符串已存在");
        }

        return baseMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        // 不能删除超级管理员角色
        if (Arrays.asList(roleIds).contains(1L)) {
            throw new ServiceException("不允许删除超级管理员角色");
        }

        return baseMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleName, role.getRoleName());

        if (role.getRoleId() != null) {
            queryWrapper.ne(SysRole::getRoleId, role.getRoleId());
        }

        return count(queryWrapper) == 0;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, role.getRoleKey());

        if (role.getRoleId() != null) {
            queryWrapper.ne(SysRole::getRoleId, role.getRoleId());
        }

        return count(queryWrapper) == 0;
    }
}
