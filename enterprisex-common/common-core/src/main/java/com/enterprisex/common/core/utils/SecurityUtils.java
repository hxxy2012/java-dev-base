package com.enterprisex.common.core.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 *
 * @author EnterpriseX
 */
public class SecurityUtils {

    /**
     * 用户ID Header名称
     */
    private static final String HEADER_USER_ID = "X-User-Id";

    /**
     * 用户名 Header名称
     */
    private static final String HEADER_USERNAME = "X-Username";

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录返回null
     */
    public static Long getUserId() {
        String userId = getHeader(HEADER_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，如果未登录返回null
     */
    public static String getUsername() {
        return getHeader(HEADER_USERNAME);
    }

    /**
     * 从请求头中获取值
     *
     * @param headerName 请求头名称
     * @return 请求头值
     */
    private static String getHeader(String headerName) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getHeader(headerName);
        }
        return null;
    }

    /**
     * 获取当前HTTP请求
     *
     * @return HttpServletRequest
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isAuthenticated() {
        return getUserId() != null;
    }

    /**
     * 检查当前用户ID是否匹配
     *
     * @param userId 要检查的用户ID
     * @return true-匹配，false-不匹配
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}
