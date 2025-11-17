package com.enterprisex.system.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 在线用户信息
 *
 * @author EnterpriseX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserOnline implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话编号
     */
    private String tokenId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 最后访问时间
     */
    private Long lastAccessTime;

    /**
     * Token过期时间
     */
    private Long expireTime;
}
