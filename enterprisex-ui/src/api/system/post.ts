import { get, post, put, del } from '@/utils/request';

/**
 * 岗位类型定义
 */
export interface Post {
  postId?: number;
  postCode: string;
  postName: string;
  postSort?: number;
  status?: number;   // 0=禁用 1=正常
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 岗位查询参数
 */
export interface PostQuery {
  postCode?: string;
  postName?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 查询岗位列表
 */
export const getPostList = (params: PostQuery) => {
  return get('/system/post/list', params);
};

/**
 * 获取岗位详情
 */
export const getPost = (postId: number) => {
  return get(`/system/post/${postId}`);
};

/**
 * 新增岗位
 */
export const addPost = (data: Post) => {
  return post('/system/post', data);
};

/**
 * 修改岗位
 */
export const updatePost = (data: Post) => {
  return put('/system/post', data);
};

/**
 * 删除岗位
 */
export const deletePost = (postIds: number[]) => {
  return del(`/system/post/${postIds.join(',')}`);
};

/**
 * 修改岗位状态
 */
export const changePostStatus = (postId: number, status: number) => {
  return put('/system/post/changeStatus', { postId, status });
};
