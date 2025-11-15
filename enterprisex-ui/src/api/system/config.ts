import { get, post, put, del } from '@/utils/request';

/**
 * 参数配置定义
 */
export interface Config {
  configId?: number;
  configName: string;
  configKey: string;
  configValue: string;
  configType?: number;  // 0=非内置 1=内置
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 参数配置查询参数
 */
export interface ConfigQuery {
  configName?: string;
  configKey?: string;
  configType?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 查询参数配置列表
 */
export const getConfigList = (params: ConfigQuery) => {
  return get('/system/config/list', params);
};

/**
 * 获取参数配置详情
 */
export const getConfig = (configId: number) => {
  return get(`/system/config/${configId}`);
};

/**
 * 根据参数键名查询参数值
 */
export const getConfigKey = (configKey: string) => {
  return get(`/system/config/configKey/${configKey}`);
};

/**
 * 新增参数配置
 */
export const addConfig = (data: Config) => {
  return post('/system/config', data);
};

/**
 * 修改参数配置
 */
export const updateConfig = (data: Config) => {
  return put('/system/config', data);
};

/**
 * 删除参数配置
 */
export const deleteConfig = (configIds: number[]) => {
  return del(`/system/config/${configIds.join(',')}`);
};
