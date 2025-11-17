package com.enterprisex.auth.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户认证信息（用于登录验证）
 *
 * @author EnterpriseX
 */
@Data
@TableName("sys_user")
public class UserAuthInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：0禁用1正常
     */
    private Integer status;

    /**
     * 删除标志：0正常1删除
     */
    private Integer delFlag;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 角色ID列表（不存储到数据库）
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 角色标识列表（不存储到数据库）
     */
    @TableField(exist = false)
    private List<String> roleKeys;

    /**
     * 权限标识列表（不存储到数据库）
     */
    @TableField(exist = false)
    private List<String> permissions;

    /**
     * 是否是管理员
     */
    public boolean isAdmin() {
        return userId != null && 1L == userId;
    }
}
