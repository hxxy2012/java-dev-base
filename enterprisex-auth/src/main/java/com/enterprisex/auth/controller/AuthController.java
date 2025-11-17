package com.enterprisex.auth.controller;

import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;
import com.enterprisex.auth.service.AuthService;
import com.enterprisex.auth.service.LoginLogService;
import com.enterprisex.auth.util.RequestUtil;
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

    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("用户登录: {}", request.getUsername());

        try {
            // 获取IP和UserAgent
            String ipaddr = RequestUtil.getIpAddr(httpRequest);
            String userAgent = RequestUtil.getUserAgent(httpRequest);

            // 调用认证服务进行登录
            LoginResponse response = authService.login(request, ipaddr, userAgent);

            // 记录登录成功日志
            loginLogService.recordLoginLog(request.getUsername(), 1, "登录成功", httpRequest);

            return R.ok("登录成功", response);

        } catch (Exception e) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(request.getUsername(), 0, e.getMessage(), httpRequest);
            log.error("用户登录失败: {}", request.getUsername(), e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                           @RequestHeader(value = "X-Username", required = false) String username,
                           @RequestHeader(value = "Authorization", required = false) String authorization,
                           HttpServletRequest httpRequest) {
        try {
            // 提取token
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }

            // 将token加入黑名单并删除在线用户信息
            if (token != null && userId != null) {
                authService.addTokenToBlacklist(token, userId);
                authService.removeOnlineUser(userId, token);
            }

            // 记录登出日志
            if (username != null && !username.isEmpty()) {
                loginLogService.recordLoginLog(username, 1, "登出成功", httpRequest);
            }

            log.info("用户登出成功: userId={}, username={}", userId, username);
            return R.ok("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return R.ok("登出成功"); // 即使失败也返回成功，避免暴露系统信息
        }
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
        LoginResponse.UserInfo userInfo = authService.getUserInfo(userId);
        return R.ok(userInfo);
    }

    /**
     * 获取用户路由菜单
     */
    @Operation(summary = "获取用户路由菜单")
    @GetMapping("/getRouters")
    public R<?> getRouters(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                            @RequestHeader(value = "X-Username", required = false) String username) {
        log.info("获取用户路由菜单: userId={}, username={}", userId, username);

        if (userId == null) {
            log.warn("用户未登录，返回空路由");
            return R.ok(new java.util.ArrayList<>());
        }

        try {
            return R.ok(authService.getRouters(userId));
        } catch (Exception e) {
            log.error("获取用户路由菜单失败: userId={}", userId, e);
            return R.ok(new java.util.ArrayList<>());
        }
    }
}
