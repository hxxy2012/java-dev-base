package com.enterprisex.auth.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 登录日志DTO
 *
 * @author EnterpriseX
 */
@Data
@Builder
public class LoginLog {

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
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
     * 登录状态：0失败1成功
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private Date loginTime;
}
