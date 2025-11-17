import request from '@/utils/request';

/**
 * IP规则
 */
export interface IpRule {
  id?: string;
  ipAddress: string;
  ruleType: number; // 1=白名单 2=黑名单
  description?: string;
  status?: number; // 1=启用 0=禁用
  createTime?: string;
  createBy?: string;
}

/**
 * 访问统计
 */
export interface AccessStat {
  ipAddress: string;
  accessCount: number;
  lastAccessTime: string;
  blockedCount: number;
}

/**
 * 获取IP规则列表
 */
export const getIpRules = (ruleType?: number) => {
  return request<IpRule[]>({
    url: '/system/ipaccess/rules',
    method: 'get',
    params: { ruleType },
  });
};

/**
 * 添加IP规则
 */
export const addIpRule = (data: IpRule) => {
  return request({
    url: '/system/ipaccess/rules',
    method: 'post',
    data,
  });
};

/**
 * 修改IP规则
 */
export const updateIpRule = (data: IpRule) => {
  return request({
    url: '/system/ipaccess/rules',
    method: 'put',
    data,
  });
};

/**
 * 删除IP规则
 */
export const deleteIpRules = (ids: string[]) => {
  return request({
    url: `/system/ipaccess/rules/${ids.join(',')}`,
    method: 'delete',
  });
};

/**
 * 检查IP访问权限
 */
export const checkIpAccess = (ipAddress: string) => {
  return request({
    url: `/system/ipaccess/check/${ipAddress}`,
    method: 'get',
  });
};

/**
 * 获取访问统计
 */
export const getAccessStats = () => {
  return request<AccessStat[]>({
    url: '/system/ipaccess/stats',
    method: 'get',
  });
};

/**
 * 清空访问统计
 */
export const clearAccessStats = () => {
  return request({
    url: '/system/ipaccess/stats',
    method: 'delete',
  });
};
