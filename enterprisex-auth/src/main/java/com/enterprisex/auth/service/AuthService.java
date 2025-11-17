package com.enterprisex.auth.service;

import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;
import com.enterprisex.auth.domain.RouterVo;

import java.util.List;

/**
 * 认证服务接口
 *
 * @author EnterpriseX
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @param ipaddr 登录IP
     * @param userAgent 用户代理
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request, String ipaddr, String userAgent);

    /**
     * 验证用户密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 是否验证成功
     */
    boolean verifyPassword(String username, String password);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    LoginResponse.UserInfo getUserInfo(Long userId);

    /**
     * 将token加入黑名单
     *
     * @param token JWT token
     * @param userId 用户ID
     */
    void addTokenToBlacklist(String token, Long userId);

    /**
     * 检查token是否在黑名单中
     *
     * @param token JWT token
     * @return true-在黑名单中 false-不在黑名单中
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 获取用户路由菜单
     *
     * @param userId 用户ID
     * @return 路由菜单列表
     */
    List<RouterVo> getRouters(Long userId);
}
