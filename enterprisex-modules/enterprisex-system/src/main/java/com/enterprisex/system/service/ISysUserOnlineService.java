package com.enterprisex.system.service;

import com.enterprisex.system.domain.SysUserOnline;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author EnterpriseX
 */
public interface ISysUserOnlineService {

    /**
     * 查询所有在线用户
     *
     * @return 在线用户列表
     */
    List<SysUserOnline> selectOnlineList();

    /**
     * 通过会话ID查询在线用户
     *
     * @param tokenId 会话ID
     * @return 在线用户
     */
    SysUserOnline selectOnlineByToken(String tokenId);

    /**
     * 通过用户名查询在线用户
     *
     * @param username 用户名
     * @return 在线用户列表
     */
    List<SysUserOnline> selectOnlineByUserName(String username);

    /**
     * 强退用户
     *
     * @param tokenId 会话ID
     * @return 是否成功
     */
    boolean forceLogout(String tokenId);

    /**
     * 批量强退用户
     *
     * @param tokenIds 会话ID数组
     * @return 强退数量
     */
    int batchForceLogout(String[] tokenIds);
}
