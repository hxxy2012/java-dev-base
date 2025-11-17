package com.enterprisex.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprisex.auth.domain.LoginRequest;
import com.enterprisex.auth.domain.LoginResponse;
import com.enterprisex.auth.domain.RouterVo;
import com.enterprisex.auth.domain.UserAuthInfo;
import com.enterprisex.auth.mapper.UserAuthMapper;
import com.enterprisex.auth.service.AuthService;
import com.enterprisex.common.core.constant.CacheConstants;
import com.enterprisex.common.core.constant.Constants;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.redis.service.RedisCache;
import com.enterprisex.common.security.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private RedisCache redisCache;

    /**
     * Token黑名单缓存Key前缀
     */
    private static final String TOKEN_BLACKLIST_KEY = "token_blacklist:";

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

        // 存储在线用户信息到Redis
        storeOnlineUser(user.getUserId(), username, ipaddr, userAgent, accessToken);

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

    /**
     * 存储在线用户信息到Redis
     */
    private void storeOnlineUser(Long userId, String username, String ipaddr, String userAgent, String token) {
        try {
            // 构建在线用户信息
            java.util.Map<String, Object> onlineUser = new java.util.HashMap<>();
            onlineUser.put("userId", userId);
            onlineUser.put("username", username);
            onlineUser.put("ipaddr", ipaddr);
            onlineUser.put("userAgent", userAgent);
            onlineUser.put("loginTime", System.currentTimeMillis());
            onlineUser.put("token", token);

            // 存储到Redis，使用用户ID作为key的一部分
            String key = CacheConstants.ONLINE_TOKEN_KEY + userId + ":" + token.substring(0, Math.min(8, token.length()));

            // 过期时间设置为token过期时间
            Long expiration = tokenProvider.getExpiration();
            redisCache.setCacheObject(key, onlineUser, expiration.intValue(), TimeUnit.SECONDS);

            log.info("用户登录成功，已存储在线状态: userId={}, username={}", userId, username);
        } catch (Exception e) {
            log.error("存储在线用户信息失败: userId={}", userId, e);
            // 不抛出异常，避免影响登录流程
        }
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

    @Override
    public void addTokenToBlacklist(String token, Long userId) {
        try {
            // 获取token的剩余有效期
            Long expiration = tokenProvider.getExpirationFromToken(token);
            if (expiration == null || expiration <= 0) {
                log.warn("Token已过期或无效，无需加入黑名单: userId={}", userId);
                return;
            }

            // 将token加入黑名单，过期时间与token一致
            String key = TOKEN_BLACKLIST_KEY + token;
            redisCache.setCacheObject(key, userId, expiration.intValue(), TimeUnit.SECONDS);
            log.info("Token已加入黑名单: userId={}, expiration={}秒", userId, expiration);
        } catch (Exception e) {
            log.error("Token加入黑名单失败: userId={}", userId, e);
            // 不抛出异常，避免影响登出流程
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String key = TOKEN_BLACKLIST_KEY + token;
            return redisCache.hasKey(key);
        } catch (Exception e) {
            log.error("检查Token黑名单失败", e);
            return false;
        }
    }

    @Override
    public List<RouterVo> getRouters(Long userId) {
        if (userId == null) {
            log.warn("获取路由菜单失败: userId为空");
            return new ArrayList<>();
        }

        try {
            List<RouterVo> menus;

            // 管理员返回所有菜单
            if (userId == 1L) {
                menus = userAuthMapper.selectAllMenus();
                log.info("管理员获取所有菜单: userId={}, 菜单数={}", userId, menus.size());
            } else {
                menus = userAuthMapper.selectMenusByUserId(userId);
                log.info("用户获取菜单: userId={}, 菜单数={}", userId, menus.size());
            }

            // 构建菜单树
            List<RouterVo> routerTree = buildRouterTree(menus);

            // 设置路由元信息
            setRouterMeta(routerTree);

            return routerTree;
        } catch (Exception e) {
            log.error("获取路由菜单失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 构建路由菜单树
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    private List<RouterVo> buildRouterTree(List<RouterVo> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        // 获取所有根菜单（parentId为0或null）
        List<RouterVo> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0L)
                .collect(Collectors.toList());

        // 为每个根菜单递归设置子菜单
        for (RouterVo rootMenu : rootMenus) {
            rootMenu.setChildren(getChildren(rootMenu.getMenuId(), menus));
        }

        return rootMenus;
    }

    /**
     * 递归获取子菜单
     *
     * @param parentId 父菜单ID
     * @param allMenus 所有菜单
     * @return 子菜单列表
     */
    private List<RouterVo> getChildren(Long parentId, List<RouterVo> allMenus) {
        List<RouterVo> children = allMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .collect(Collectors.toList());

        // 递归设置子菜单的子菜单
        for (RouterVo child : children) {
            child.setChildren(getChildren(child.getMenuId(), allMenus));
        }

        return children;
    }

    /**
     * 设置路由元信息
     *
     * @param routers 路由列表
     */
    private void setRouterMeta(List<RouterVo> routers) {
        if (CollectionUtils.isEmpty(routers)) {
            return;
        }

        for (RouterVo router : routers) {
            RouterVo.MetaVo meta = new RouterVo.MetaVo();
            meta.setTitle(router.getName());
            meta.setIcon(router.getIcon());
            meta.setHidden(router.getVisible() != null && router.getVisible() == 0);
            router.setMeta(meta);

            // 递归处理子菜单
            if (!CollectionUtils.isEmpty(router.getChildren())) {
                setRouterMeta(router.getChildren());
            }
        }
    }

    @Override
    public void removeOnlineUser(Long userId, String token) {
        try {
            // 构建Redis key（与存储时保持一致）
            String key = CacheConstants.ONLINE_TOKEN_KEY + userId + ":" + token.substring(0, Math.min(8, token.length()));

            // 从Redis删除在线用户信息
            redisCache.deleteObject(key);

            log.info("用户登出，已删除在线状态: userId={}", userId);
        } catch (Exception e) {
            log.error("删除在线用户信息失败: userId={}", userId, e);
            // 不抛出异常，避免影响登出流程
        }
    }
}
