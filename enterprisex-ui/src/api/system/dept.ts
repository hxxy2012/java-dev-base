import { get, post, put, del } from '@/utils/request';

/**
 * 部门类型定义
 */
export interface Dept {
  deptId?: number;
  parentId?: number;
  ancestors?: string;
  deptName: string;
  orderNum?: number;
  leader?: string;
  phone?: string;
  email?: string;
  status?: number;   // 0=禁用 1=正常
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
  remark?: string;
  children?: Dept[];
}

/**
 * 部门查询参数
 */
export interface DeptQuery {
  deptName?: string;
  status?: number;
  parentId?: number;
}

/**
 * 查询部门列表
 */
export const getDeptList = (params: DeptQuery) => {
  return get('/system/dept/list', params);
};

/**
 * 查询部门树
 */
export const getDeptTree = (params: DeptQuery) => {
  return get('/system/dept/tree', params);
};

/**
 * 获取部门详情
 */
export const getDept = (deptId: number) => {
  return get(`/system/dept/${deptId}`);
};

/**
 * 新增部门
 */
export const addDept = (data: Dept) => {
  return post('/system/dept', data);
};

/**
 * 修改部门
 */
export const updateDept = (data: Dept) => {
  return put('/system/dept', data);
};

/**
 * 删除部门
 */
export const deleteDept = (deptId: number) => {
  return del(`/system/dept/${deptId}`);
};
