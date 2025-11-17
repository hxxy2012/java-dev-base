import { get, post, put, del } from '@/utils/request';

/**
 * 用户类型定义
 */
export interface User {
  userId?: number;
  deptId?: number;
  username: string;
  nickname?: string;
  email?: string;
  phone?: string;
  gender?: number;
  avatar?: string;
  password?: string;
  status?: number;
  delFlag?: number;
  loginIp?: string;
  loginDate?: string;
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 用户查询参数
 */
export interface UserQuery {
  username?: string;
  nickname?: string;
  phone?: string;
  deptId?: number;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 查询用户列表
 */
export const getUserList = (params: UserQuery) => {
  return get('/system/user/list', params);
};

/**
 * 获取用户详情
 */
export const getUser = (userId: number) => {
  return get(`/system/user/${userId}`);
};

/**
 * 新增用户
 */
export const addUser = (data: User) => {
  return post('/system/user', data);
};

/**
 * 修改用户
 */
export const updateUser = (data: User) => {
  return put('/system/user', data);
};

/**
 * 删除用户
 */
export const deleteUser = (userIds: number[]) => {
  return del(`/system/user/${userIds.join(',')}`);
};

/**
 * 重置密码
 */
export const resetUserPwd = (userId: number, password: string) => {
  return put('/system/user/resetPwd', { userId, password });
};

/**
 * 修改用户状态
 */
export const changeUserStatus = (userId: number, status: number) => {
  return put('/system/user/changeStatus', { userId, status });
};

/**
 * 导出用户数据
 */
export const exportUser = (params: UserQuery) => {
  return `/api/system/user/export?${new URLSearchParams(params as any).toString()}`;
};

/**
 * 下载用户导入模板
 */
export const downloadTemplate = () => {
  return '/api/system/user/importTemplate';
};

/**
 * 导入用户数据
 */
export const importUser = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return post('/system/user/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
