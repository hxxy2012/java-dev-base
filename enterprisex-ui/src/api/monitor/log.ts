import { get, del } from '@/utils/request';

/**
 * 操作日志类型定义
 */
export interface OperLog {
  operId?: number;
  title?: string;
  businessType?: number;
  method?: string;
  requestMethod?: string;
  operatorType?: number;
  operName?: string;
  deptName?: string;
  operUrl?: string;
  operIp?: string;
  operLocation?: string;
  operParam?: string;
  jsonResult?: string;
  status?: number;
  errorMsg?: string;
  operTime?: string;
  costTime?: number;
}

/**
 * 登录日志类型定义
 */
export interface LoginLog {
  infoId?: number;
  username?: string;
  ipaddr?: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  status?: number;
  msg?: string;
  loginTime?: string;
}

/**
 * 操作日志查询参数
 */
export interface OperLogQuery {
  title?: string;
  businessType?: number;
  operName?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 登录日志查询参数
 */
export interface LoginLogQuery {
  username?: string;
  ipaddr?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

// ==================== 操作日志 API ====================

/**
 * 查询操作日志列表
 */
export const getOperLogList = (params: OperLogQuery) => {
  return get('/system/operlog/list', params);
};

/**
 * 获取操作日志详情
 */
export const getOperLog = (operId: number) => {
  return get(`/system/operlog/${operId}`);
};

/**
 * 删除操作日志
 */
export const deleteOperLog = (operIds: number[]) => {
  return del(`/system/operlog/${operIds.join(',')}`);
};

/**
 * 清空操作日志
 */
export const cleanOperLog = () => {
  return del('/system/operlog/clean');
};

// ==================== 登录日志 API ====================

/**
 * 查询登录日志列表
 */
export const getLoginLogList = (params: LoginLogQuery) => {
  return get('/system/loginlog/list', params);
};

/**
 * 获取登录日志详情
 */
export const getLoginLog = (infoId: number) => {
  return get(`/system/loginlog/${infoId}`);
};

/**
 * 删除登录日志
 */
export const deleteLoginLog = (infoIds: number[]) => {
  return del(`/system/loginlog/${infoIds.join(',')}`);
};

/**
 * 清空登录日志
 */
export const cleanLoginLog = () => {
  return del('/system/loginlog/clean');
};
