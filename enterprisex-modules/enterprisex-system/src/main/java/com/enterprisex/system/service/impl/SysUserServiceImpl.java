package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.mapper.SysUserMapper;
import com.enterprisex.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StringUtils.isNotEmpty(user.getUsername())) {
            queryWrapper.like(SysUser::getUsername, user.getUsername());
        }

        // 昵称模糊查询
        if (StringUtils.isNotEmpty(user.getNickname())) {
            queryWrapper.like(SysUser::getNickname, user.getNickname());
        }

        // 手机号查询
        if (StringUtils.isNotEmpty(user.getPhone())) {
            queryWrapper.eq(SysUser::getPhone, user.getPhone());
        }

        // 部门ID查询
        if (user.getDeptId() != null) {
            queryWrapper.eq(SysUser::getDeptId, user.getDeptId());
        }

        // 状态查询
        if (user.getStatus() != null) {
            queryWrapper.eq(SysUser::getStatus, user.getStatus());
        }

        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        // 校验用户名唯一性
        if (!checkUserNameUnique(user)) {
            throw new ServiceException("新增用户'" + user.getUsername() + "'失败，用户名已存在");
        }

        return baseMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        // 不能修改超级管理员
        if (user.getUserId() != null && user.getUserId() == 1L) {
            throw new ServiceException("不允许修改超级管理员");
        }

        return baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        // 不能删除超级管理员
        if (Arrays.asList(userIds).contains(1L)) {
            throw new ServiceException("不允许删除超级管理员");
        }

        return baseMapper.deleteBatchIds(Arrays.asList(userIds));
    }

    @Override
    public boolean checkUserNameUnique(SysUser user) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, user.getUsername());

        if (user.getUserId() != null) {
            queryWrapper.ne(SysUser::getUserId, user.getUserId());
        }

        return count(queryWrapper) == 0;
    }
}
