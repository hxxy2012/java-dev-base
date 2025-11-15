package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprisex.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author EnterpriseX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户对象")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度不能超过50个字符")
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 性别：0未知1男2女
     */
    @Schema(description = "性别：0未知1男2女")
    private Integer gender;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 密码
     */
    @JsonIgnore
    @Schema(description = "密码")
    private String password;

    /**
     * 状态：0禁用1正常
     */
    @Schema(description = "状态：0禁用1正常")
    private Integer status;

    /**
     * 删除标志：0正常1删除
     */
    @JsonIgnore
    @Schema(description = "删除标志：0正常1删除")
    private Integer delFlag;

    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    /**
     * 部门对象（不存储到数据库）
     */
    @TableField(exist = false)
    @Schema(description = "部门对象")
    private SysDept dept;

    /**
     * 角色ID列表（不存储到数据库）
     */
    @TableField(exist = false)
    @Schema(description = "角色ID列表")
    private Long[] roleIds;

    /**
     * 岗位ID列表（不存储到数据库）
     */
    @TableField(exist = false)
    @Schema(description = "岗位ID列表")
    private Long[] postIds;

    /**
     * 角色列表（不存储到数据库）
     */
    @TableField(exist = false)
    @Schema(description = "角色列表")
    private List<SysRole> roles;

    /**
     * 是否是管理员
     */
    public boolean isAdmin() {
        return userId != null && 1L == userId;
    }
}
