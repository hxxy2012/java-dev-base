import request from '@/utils/request';

/**
 * 数据库表类型
 */
export interface GenTable {
  tableName: string;
  tableComment: string;
  className?: string;
  packageName?: string;
  moduleName?: string;
  businessName?: string;
  functionName?: string;
  functionAuthor?: string;
  createTime?: string;
}

/**
 * 表字段类型
 */
export interface GenTableColumn {
  columnName: string;
  columnComment: string;
  columnType: string;
  javaType: string;
  javaField: string;
  isPk: string;
  isIncrement: string;
  isRequired: string;
}

/**
 * 查询数据库表列表
 */
export function listDbTable(params: any) {
  return request({
    url: '/tool/gen/db/list',
    method: 'get',
    params,
  });
}

/**
 * 查询数据库表列信息
 */
export function getDbTableColumns(tableName: string) {
  return request({
    url: `/tool/gen/column/${tableName}`,
    method: 'get',
  });
}

/**
 * 预览代码
 */
export function previewTable(tableName: string) {
  return request({
    url: `/tool/gen/preview/${tableName}`,
    method: 'get',
  });
}

/**
 * 生成代码（下载方式）
 */
export function downloadCode(tableName: string) {
  return request({
    url: `/tool/gen/download/${tableName}`,
    method: 'get',
    responseType: 'blob',
  });
}
