import { get, del } from '@/utils/request';

/**
 * 缓存信息类型
 */
export interface CacheInfo {
  cacheName?: string;
  cacheKey: string;
  cacheValue: string;
  expireTime: number;
  remark?: string;
}

/**
 * Redis信息类型
 */
export interface RedisInfo {
  dbSize: number;
  cacheStats: Record<string, number>;
}

/**
 * 获取缓存监控信息
 */
export const getCacheInfo = () => {
  return get<RedisInfo>('/monitor/cache/info');
};

/**
 * 获取所有缓存名称
 */
export const getCacheNames = () => {
  return get<string[]>('/monitor/cache/names');
};

/**
 * 获取缓存键名列表
 */
export const getCacheKeys = (cacheName: string) => {
  return get<string[]>('/monitor/cache/keys', { cacheName });
};

/**
 * 获取缓存值
 */
export const getCacheValue = (cacheKey: string) => {
  return get<CacheInfo>('/monitor/cache/value', { cacheKey });
};

/**
 * 清除指定缓存
 */
export const clearCache = (cacheKey: string) => {
  return del('/monitor/cache/clear', { cacheKey });
};

/**
 * 清除指定前缀的缓存
 */
export const clearCacheByPrefix = (cachePrefix: string) => {
  return del('/monitor/cache/clearPrefix', { cachePrefix });
};

/**
 * 清除所有缓存
 */
export const clearAllCache = () => {
  return del('/monitor/cache/clearAll');
};
