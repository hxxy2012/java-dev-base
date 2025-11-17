package com.enterprisex.auth.service;

import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;

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
}
