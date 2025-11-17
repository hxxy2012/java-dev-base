import request from '@/utils/request';

/**
 * 综合健康检查
 */
export function healthCheck() {
  return request({
    url: '/system/health/check',
    method: 'get',
  });
}

/**
 * 数据库健康检查
 */
export function databaseHealth() {
  return request({
    url: '/system/health/database',
    method: 'get',
  });
}

/**
 * Redis健康检查
 */
export function redisHealth() {
  return request({
    url: '/system/health/redis',
    method: 'get',
  });
}

/**
 * 磁盘空间检查
 */
export function diskHealth() {
  return request({
    url: '/system/health/disk',
    method: 'get',
  });
}

/**
 * 内存检查
 */
export function memoryHealth() {
  return request({
    url: '/system/health/memory',
    method: 'get',
  });
}
