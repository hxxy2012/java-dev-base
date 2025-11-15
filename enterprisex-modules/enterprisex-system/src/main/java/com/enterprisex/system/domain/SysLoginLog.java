package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录日志对象 sys_login_log
 *
 * @author EnterpriseX
 */
@Data
@TableName("sys_login_log")
@Schema(description = "登录日志对象")
public class SysLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "info_id", type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long infoId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录IP")
    private String ipaddr;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录状态：0失败1成功")
    private Integer status;

    @Schema(description = "提示消息")
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "访问时间")
    private Date loginTime;
}
