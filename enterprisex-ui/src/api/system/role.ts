import { get, post, put, del } from '@/utils/request';

/**
 * 角色类型定义
 */
export interface Role {
  roleId?: number;
  roleName: string;
  roleKey: string;
  roleSort?: number;
  dataScope?: number;
  status?: number;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
  remark?: string;
  menuIds?: number[];
  deptIds?: number[];
}

/**
 * 角色查询参数
 */
export interface RoleQuery {
  roleName?: string;
  roleKey?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 查询角色列表
 */
export const getRoleList = (params: RoleQuery) => {
  return get('/system/role/list', params);
};

/**
 * 获取角色详情
 */
export const getRole = (roleId: number) => {
  return get(`/system/role/${roleId}`);
};

/**
 * 新增角色
 */
export const addRole = (data: Role) => {
  return post('/system/role', data);
};

/**
 * 修改角色
 */
export const updateRole = (data: Role) => {
  return put('/system/role', data);
};

/**
 * 删除角色
 */
export const deleteRole = (roleIds: number[]) => {
  return del(`/system/role/${roleIds.join(',')}`);
};

/**
 * 修改角色状态
 */
export const changeRoleStatus = (roleId: number, status: number) => {
  return put('/system/role/changeStatus', { roleId, status });
};
