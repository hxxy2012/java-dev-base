package com.enterprisex.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;
import com.enterprisex.auth.domain.UserAuthInfo;
import com.enterprisex.auth.mapper.UserAuthMapper;
import com.enterprisex.auth.service.AuthService;
import com.enterprisex.common.core.constant.Constants;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.security.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 认证服务实现
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request, String ipaddr, String userAgent) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 查询用户信息
        UserAuthInfo user = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserAuthInfo>()
                        .eq(UserAuthInfo::getUsername, username)
        );

        // 验证用户存在性
        if (user == null) {
            throw new ServiceException("用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new ServiceException("账号已被停用，请联系管理员");
        }

        // 验证删除标志
        if (user.getDelFlag() != null && user.getDelFlag() == 1) {
            throw new ServiceException("账号已被删除");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("用户名或密码错误");
        }

        // 查询用户角色
        List<String> roleKeys = userAuthMapper.selectRoleKeysByUserId(user.getUserId());
        String roleKey = CollectionUtils.isEmpty(roleKeys) ? "common" : roleKeys.get(0);

        // 生成Token
        String accessToken = tokenProvider.generateAccessToken(user.getUserId(), username, roleKey);
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        // 构建响应
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(Constants.TOKEN_PREFIX.trim())
                .expiresIn(tokenProvider.getExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    @Override
    public boolean verifyPassword(String username, String password) {
        // 查询用户信息
        UserAuthInfo user = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserAuthInfo>()
                        .eq(UserAuthInfo::getUsername, username)
        );

        if (user == null) {
            return false;
        }

        // 验证用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            return false;
        }

        // 验证删除标志
        if (user.getDelFlag() != null && user.getDelFlag() == 1) {
            return false;
        }

        // 验证密码
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public LoginResponse.UserInfo getUserInfo(Long userId) {
        // 查询用户信息
        UserAuthInfo user = userAuthMapper.selectById(userId);

        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 构建用户信息
        return LoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }
}
