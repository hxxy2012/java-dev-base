/**
 * 系统常量定义
 */

/**
 * 用户状态
 */
export const USER_STATUS = {
  NORMAL: 1,
  DISABLED: 0,
} as const;

export const USER_STATUS_TEXT = {
  [USER_STATUS.NORMAL]: '正常',
  [USER_STATUS.DISABLED]: '停用',
} as const;

/**
 * 性别
 */
export const GENDER = {
  MALE: 0,
  FEMALE: 1,
  UNKNOWN: 2,
} as const;

export const GENDER_TEXT = {
  [GENDER.MALE]: '男',
  [GENDER.FEMALE]: '女',
  [GENDER.UNKNOWN]: '未知',
} as const;

/**
 * 菜单类型
 */
export const MENU_TYPE = {
  DIRECTORY: 'M',
  MENU: 'C',
  BUTTON: 'F',
} as const;

export const MENU_TYPE_TEXT = {
  [MENU_TYPE.DIRECTORY]: '目录',
  [MENU_TYPE.MENU]: '菜单',
  [MENU_TYPE.BUTTON]: '按钮',
} as const;

/**
 * 菜单状态
 */
export const MENU_STATUS = {
  SHOW: 1,
  HIDE: 0,
} as const;

export const MENU_STATUS_TEXT = {
  [MENU_STATUS.SHOW]: '显示',
  [MENU_STATUS.HIDE]: '隐藏',
} as const;

/**
 * 数据范围（数据权限）
 */
export const DATA_SCOPE = {
  ALL: 1,
  CUSTOM: 2,
  DEPT: 3,
  DEPT_AND_CHILD: 4,
  SELF: 5,
} as const;

export const DATA_SCOPE_TEXT = {
  [DATA_SCOPE.ALL]: '全部数据权限',
  [DATA_SCOPE.CUSTOM]: '自定义数据权限',
  [DATA_SCOPE.DEPT]: '本部门数据权限',
  [DATA_SCOPE.DEPT_AND_CHILD]: '本部门及以下数据权限',
  [DATA_SCOPE.SELF]: '仅本人数据权限',
} as const;

/**
 * 通用状态
 */
export const COMMON_STATUS = {
  SUCCESS: 0,
  FAIL: 1,
} as const;

export const COMMON_STATUS_TEXT = {
  [COMMON_STATUS.SUCCESS]: '成功',
  [COMMON_STATUS.FAIL]: '失败',
} as const;

/**
 * 业务类型
 */
export const BUSINESS_TYPE = {
  OTHER: 0,
  INSERT: 1,
  UPDATE: 2,
  DELETE: 3,
  GRANT: 4,
  EXPORT: 5,
  IMPORT: 6,
  FORCE: 7,
  CLEAN: 8,
} as const;

export const BUSINESS_TYPE_TEXT = {
  [BUSINESS_TYPE.OTHER]: '其他',
  [BUSINESS_TYPE.INSERT]: '新增',
  [BUSINESS_TYPE.UPDATE]: '修改',
  [BUSINESS_TYPE.DELETE]: '删除',
  [BUSINESS_TYPE.GRANT]: '授权',
  [BUSINESS_TYPE.EXPORT]: '导出',
  [BUSINESS_TYPE.IMPORT]: '导入',
  [BUSINESS_TYPE.FORCE]: '强退',
  [BUSINESS_TYPE.CLEAN]: '清空',
} as const;

/**
 * 通知公告类型
 */
export const NOTICE_TYPE = {
  NOTICE: 1,
  ANNOUNCEMENT: 2,
} as const;

export const NOTICE_TYPE_TEXT = {
  [NOTICE_TYPE.NOTICE]: '通知',
  [NOTICE_TYPE.ANNOUNCEMENT]: '公告',
} as const;

/**
 * 系统内置
 */
export const SYSTEM_BUILT_IN = {
  YES: 1,
  NO: 0,
} as const;

export const SYSTEM_BUILT_IN_TEXT = {
  [SYSTEM_BUILT_IN.YES]: '是',
  [SYSTEM_BUILT_IN.NO]: '否',
} as const;

/**
 * 是否默认
 */
export const IS_DEFAULT = {
  YES: 1,
  NO: 0,
} as const;

/**
 * Token相关
 */
export const TOKEN_KEY = 'token';
export const REFRESH_TOKEN_KEY = 'refreshToken';
export const USER_INFO_KEY = 'userInfo';

/**
 * 本地存储键名
 */
export const STORAGE_KEYS = {
  TOKEN: TOKEN_KEY,
  REFRESH_TOKEN: REFRESH_TOKEN_KEY,
  USER_INFO: USER_INFO_KEY,
  THEME: 'theme',
  LANGUAGE: 'language',
  SIDEBAR_COLLAPSED: 'sidebarCollapsed',
} as const;

/**
 * 分页默认配置
 */
export const PAGE_SIZE = 10;
export const PAGE_SIZE_OPTIONS = ['10', '20', '50', '100'];

/**
 * 日期格式
 */
export const DATE_FORMAT = 'YYYY-MM-DD';
export const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';
export const TIME_FORMAT = 'HH:mm:ss';

/**
 * 文件上传限制
 */
export const FILE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MB
export const IMAGE_SIZE_LIMIT = 5 * 1024 * 1024; // 5MB
export const ACCEPT_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif'];
export const ACCEPT_FILE_TYPES = [
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
];
