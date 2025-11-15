package com.enterprisex.auth.controller;

import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;
import com.enterprisex.auth.service.LoginLogService;
import com.enterprisex.common.core.constant.Constants;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.security.service.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author EnterpriseX
 */
@Slf4j
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("用户登录: {}", request.getUsername());

        // TODO: 实际项目中应该从数据库验证用户名密码
        // 这里为了演示，直接验证 admin/admin123
        if (!"admin".equals(request.getUsername()) || !"admin123".equals(request.getPassword())) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(request.getUsername(), 0, "用户名或密码错误", httpRequest);
            return R.fail("用户名或密码错误");
        }

        // 模拟用户信息
        Long userId = 1L;
        String username = "admin";

        // 生成Token
        String accessToken = tokenProvider.generateAccessToken(userId, username, "admin");
        String refreshToken = tokenProvider.generateRefreshToken(userId);

        // 构建响应
        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(Constants.TOKEN_PREFIX.trim())
                .expiresIn(tokenProvider.getExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(userId)
                        .username(username)
                        .nickname("系统管理员")
                        .avatar(null)
                        .build())
                .build();

        // 记录登录成功日志
        loginLogService.recordLoginLog(username, 1, "登录成功", httpRequest);

        return R.ok("登录成功", response);
    }

    /**
     * 登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "X-Username", required = false) String username,
                           HttpServletRequest httpRequest) {
        // TODO: 清除Redis中的用户信息

        // 记录登出日志
        if (username != null && !username.isEmpty()) {
            loginLogService.recordLoginLog(username, 1, "登出成功", httpRequest);
        }

        return R.ok("登出成功");
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public R<LoginResponse> refresh(@RequestParam String refreshToken) {
        try {
            // 验证RefreshToken
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);

            // 生成新的AccessToken
            String newAccessToken = tokenProvider.refreshToken(refreshToken);

            LoginResponse response = LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType(Constants.TOKEN_PREFIX.trim())
                    .expiresIn(tokenProvider.getExpiration())
                    .build();

            return R.ok("刷新成功", response);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return R.fail("RefreshToken无效或已过期");
        }
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<LoginResponse.UserInfo> getInfo(@RequestHeader("X-User-Id") Long userId,
                                               @RequestHeader("X-Username") String username) {
        // TODO: 从数据库查询完整的用户信息
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .userId(userId)
                .username(username)
                .nickname("系统管理员")
                .avatar(null)
                .build();

        return R.ok(userInfo);
    }
}
