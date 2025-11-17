package com.enterprisex.common.core.constant;

/**
 * 错误码常量
 *
 * @author EnterpriseX
 */
public class ErrorCode {

    // ==================== 通用错误码 (1000-1999) ====================
    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;

    /**
     * 系统错误
     */
    public static final int ERROR = 500;

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 无权限
     */
    public static final int FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 请求方法不支持
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * 请求超时
     */
    public static final int REQUEST_TIMEOUT = 408;

    /**
     * 资源冲突
     */
    public static final int CONFLICT = 409;

    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    // ==================== 认证相关错误码 (2000-2999) ====================
    /**
     * 用户名或密码错误
     */
    public static final int AUTH_INVALID_CREDENTIALS = 2001;

    /**
     * Token无效
     */
    public static final int AUTH_INVALID_TOKEN = 2002;

    /**
     * Token已过期
     */
    public static final int AUTH_TOKEN_EXPIRED = 2003;

    /**
     * RefreshToken无效
     */
    public static final int AUTH_INVALID_REFRESH_TOKEN = 2004;

    /**
     * 账号已被停用
     */
    public static final int AUTH_ACCOUNT_DISABLED = 2005;

    /**
     * 账号已被删除
     */
    public static final int AUTH_ACCOUNT_DELETED = 2006;

    /**
     * 账号已被锁定
     */
    public static final int AUTH_ACCOUNT_LOCKED = 2007;

    /**
     * 验证码错误
     */
    public static final int AUTH_INVALID_CAPTCHA = 2008;

    /**
     * 验证码已过期
     */
    public static final int AUTH_CAPTCHA_EXPIRED = 2009;

    // ==================== 用户相关错误码 (3000-3999) ====================
    /**
     * 用户不存在
     */
    public static final int USER_NOT_FOUND = 3001;

    /**
     * 用户名已存在
     */
    public static final int USER_USERNAME_EXISTS = 3002;

    /**
     * 手机号已存在
     */
    public static final int USER_PHONE_EXISTS = 3003;

    /**
     * 邮箱已存在
     */
    public static final int USER_EMAIL_EXISTS = 3004;

    /**
     * 原密码错误
     */
    public static final int USER_OLD_PASSWORD_ERROR = 3005;

    /**
     * 不能删除自己
     */
    public static final int USER_CANNOT_DELETE_SELF = 3006;

    /**
     * 不能禁用自己
     */
    public static final int USER_CANNOT_DISABLE_SELF = 3007;

    // ==================== 角色相关错误码 (4000-4999) ====================
    /**
     * 角色不存在
     */
    public static final int ROLE_NOT_FOUND = 4001;

    /**
     * 角色名称已存在
     */
    public static final int ROLE_NAME_EXISTS = 4002;

    /**
     * 角色标识已存在
     */
    public static final int ROLE_KEY_EXISTS = 4003;

    /**
     * 角色已分配用户，不能删除
     */
    public static final int ROLE_HAS_USERS = 4004;

    // ==================== 部门相关错误码 (5000-5999) ====================
    /**
     * 部门不存在
     */
    public static final int DEPT_NOT_FOUND = 5001;

    /**
     * 部门名称已存在
     */
    public static final int DEPT_NAME_EXISTS = 5002;

    /**
     * 部门存在下级部门，不能删除
     */
    public static final int DEPT_HAS_CHILDREN = 5003;

    /**
     * 部门存在用户，不能删除
     */
    public static final int DEPT_HAS_USERS = 5004;

    // ==================== 菜单相关错误码 (6000-6999) ====================
    /**
     * 菜单不存在
     */
    public static final int MENU_NOT_FOUND = 6001;

    /**
     * 菜单名称已存在
     */
    public static final int MENU_NAME_EXISTS = 6002;

    /**
     * 菜单存在下级菜单，不能删除
     */
    public static final int MENU_HAS_CHILDREN = 6003;

    /**
     * 菜单已分配角色，不能删除
     */
    public static final int MENU_HAS_ROLES = 6004;

    // ==================== 文件相关错误码 (7000-7999) ====================
    /**
     * 文件不存在
     */
    public static final int FILE_NOT_FOUND = 7001;

    /**
     * 文件类型不支持
     */
    public static final int FILE_TYPE_NOT_SUPPORTED = 7002;

    /**
     * 文件大小超过限制
     */
    public static final int FILE_SIZE_EXCEEDED = 7003;

    /**
     * 文件上传失败
     */
    public static final int FILE_UPLOAD_FAILED = 7004;

    /**
     * 文件下载失败
     */
    public static final int FILE_DOWNLOAD_FAILED = 7005;

    /**
     * 文件删除失败
     */
    public static final int FILE_DELETE_FAILED = 7006;

    // ==================== 数据相关错误码 (8000-8999) ====================
    /**
     * 数据不存在
     */
    public static final int DATA_NOT_FOUND = 8001;

    /**
     * 数据已存在
     */
    public static final int DATA_EXISTS = 8002;

    /**
     * 数据正在使用中
     */
    public static final int DATA_IN_USE = 8003;

    /**
     * 数据状态错误
     */
    public static final int DATA_STATUS_ERROR = 8004;

    /**
     * 数据导入失败
     */
    public static final int DATA_IMPORT_FAILED = 8005;

    /**
     * 数据导出失败
     */
    public static final int DATA_EXPORT_FAILED = 8006;

    // ==================== 缓存相关错误码 (9000-9999) ====================
    /**
     * 缓存操作失败
     */
    public static final int CACHE_OPERATION_FAILED = 9001;

    /**
     * 缓存键不存在
     */
    public static final int CACHE_KEY_NOT_FOUND = 9002;
}
