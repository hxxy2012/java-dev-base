import request from '@/utils/request';

/**
 * 获取所有数据库表
 */
export function getTables() {
  return request({
    url: '/system/database/tables',
    method: 'get',
  });
}

/**
 * 获取表结构
 */
export function getTableInfo(tableName: string) {
  return request({
    url: `/system/database/table/${tableName}`,
    method: 'get',
  });
}

/**
 * 执行SQL查询
 */
export function executeQuery(sql: string) {
  return request({
    url: '/system/database/query',
    method: 'post',
    data: { sql },
  });
}

/**
 * 获取数据库统计信息
 */
export function getStatistics() {
  return request({
    url: '/system/database/statistics',
    method: 'get',
  });
}

/**
 * 获取慢查询信息
 */
export function getSlowQueries() {
  return request({
    url: '/system/database/slow-queries',
    method: 'get',
  });
}

/**
 * 优化表
 */
export function optimizeTable(tableName: string) {
  return request({
    url: `/system/database/optimize/${tableName}`,
    method: 'post',
  });
}

/**
 * 分析表
 */
export function analyzeTable(tableName: string) {
  return request({
    url: `/system/database/analyze/${tableName}`,
    method: 'post',
  });
}
