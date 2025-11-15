import { get, post, put, del } from '@/utils/request';

/**
 * 字典类型定义
 */
export interface DictType {
  dictId?: number;
  dictName: string;
  dictType: string;
  status?: number;   // 0=禁用 1=正常
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 字典数据定义
 */
export interface DictData {
  dictCode?: number;
  dictSort?: number;
  dictLabel: string;
  dictValue: string;
  dictType: string;
  cssClass?: string;
  listClass?: string;
  isDefault?: number; // 0=否 1=是
  status?: number;    // 0=禁用 1=正常
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 字典类型查询参数
 */
export interface DictTypeQuery {
  dictName?: string;
  dictType?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 字典数据查询参数
 */
export interface DictDataQuery {
  dictType?: string;
  dictLabel?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

// ==================== 字典类型 API ====================

/**
 * 查询字典类型列表
 */
export const getDictTypeList = (params: DictTypeQuery) => {
  return get('/system/dict/type/list', params);
};

/**
 * 获取字典类型详情
 */
export const getDictType = (dictId: number) => {
  return get(`/system/dict/type/${dictId}`);
};

/**
 * 新增字典类型
 */
export const addDictType = (data: DictType) => {
  return post('/system/dict/type', data);
};

/**
 * 修改字典类型
 */
export const updateDictType = (data: DictType) => {
  return put('/system/dict/type', data);
};

/**
 * 删除字典类型
 */
export const deleteDictType = (dictIds: number[]) => {
  return del(`/system/dict/type/${dictIds.join(',')}`);
};

// ==================== 字典数据 API ====================

/**
 * 查询字典数据列表
 */
export const getDictDataList = (params: DictDataQuery) => {
  return get('/system/dict/data/list', params);
};

/**
 * 根据字典类型查询字典数据
 */
export const getDictDataByType = (dictType: string) => {
  return get(`/system/dict/data/type/${dictType}`);
};

/**
 * 获取字典数据详情
 */
export const getDictData = (dictCode: number) => {
  return get(`/system/dict/data/${dictCode}`);
};

/**
 * 新增字典数据
 */
export const addDictData = (data: DictData) => {
  return post('/system/dict/data', data);
};

/**
 * 修改字典数据
 */
export const updateDictData = (data: DictData) => {
  return put('/system/dict/data', data);
};

/**
 * 删除字典数据
 */
export const deleteDictData = (dictCodes: number[]) => {
  return del(`/system/dict/data/${dictCodes.join(',')}`);
};
