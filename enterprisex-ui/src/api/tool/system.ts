import request from '@/utils/request';

/**
 * 获取系统信息
 */
export function getSystemInfo() {
  return request({
    url: '/system/tool/info',
    method: 'get',
  });
}

/**
 * 获取客户端信息
 */
export function getClientInfo() {
  return request({
    url: '/system/tool/client',
    method: 'get',
  });
}

/**
 * 获取系统时间
 */
export function getSystemTime() {
  return request({
    url: '/system/tool/time',
    method: 'get',
  });
}

/**
 * 获取环境变量
 */
export function getEnv() {
  return request({
    url: '/system/tool/env',
    method: 'get',
  });
}

/**
 * 触发垃圾回收
 */
export function triggerGC() {
  return request({
    url: '/system/tool/gc',
    method: 'post',
  });
}
