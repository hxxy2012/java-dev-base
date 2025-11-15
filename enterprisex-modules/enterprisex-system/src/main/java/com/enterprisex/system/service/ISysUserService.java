package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysUser;

import java.util.List;

/**
 * 用户业务层
 *
 * @author EnterpriseX
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 新增用户
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     */
    int updateUser(SysUser user);

    /**
     * 删除用户
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 校验用户名称是否唯一
     */
    boolean checkUserNameUnique(SysUser user);
}
