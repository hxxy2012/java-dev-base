import { get, post, put, del } from '@/utils/request';

/**
 * 在线用户类型定义
 */
export interface UserOnline {
  tokenId: string;
  userId: number;
  username: string;
  deptName?: string;
  ipaddr: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  loginTime: number;
  lastAccessTime: number;
  expireTime: number;
}

/**
 * 在线用户查询参数
 */
export interface UserOnlineQuery {
  username?: string;
  ipaddr?: string;
}

/**
 * 查询在线用户列表
 */
export const getOnlineList = (params: UserOnlineQuery) => {
  return get('/monitor/online/list', params);
};

/**
 * 强退用户
 */
export const forceLogout = (tokenId: string) => {
  return del(`/monitor/online/${tokenId}`);
};

/**
 * 批量强退用户
 */
export const batchForceLogout = (tokenIds: string[]) => {
  return del(`/monitor/online/batch/${tokenIds.join(',')}`);
};
