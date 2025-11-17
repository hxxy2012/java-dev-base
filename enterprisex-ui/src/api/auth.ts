import { get, post } from '@/utils/request';

/**
 * 用户信息类型
 */
export interface UserInfo {
  userId?: number;
  username: string;
  nickName?: string;
  email?: string;
  phoneNumber?: string;
  sex?: number;
  avatar?: string;
  deptId?: number;
  deptName?: string;
  postIds?: number[];
  roleIds?: number[];
  roles?: any[];
  permissions?: string[];
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
  userInfo: UserInfo;
}

/**
 * 路由菜单类型
 */
export interface RouteMenu {
  menuId?: number;
  parentId?: number;
  menuName: string;
  menuType: string;  // M=目录 C=菜单 F=按钮
  path?: string;
  component?: string;
  perms?: string;
  icon?: string;
  orderNum?: number;
  visible?: number;  // 0=隐藏 1=显示
  status?: number;   // 0=禁用 1=正常
  children?: RouteMenu[];
}

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
  return post<LoginResponse>('/auth/login', data);
};

/**
 * 用户登出
 */
export const logout = () => {
  return post('/auth/logout');
};

/**
 * 获取当前用户信息
 */
export const getUserInfo = () => {
  return get<UserInfo>('/auth/info');
};

/**
 * 获取用户路由菜单
 */
export const getRouters = () => {
  return get<RouteMenu[]>('/auth/getRouters');
};

/**
 * 刷新Token
 */
export const refreshToken = (refreshToken: string) => {
  return post('/auth/refresh', { refreshToken });
};
