import { get, post, put, del } from '@/utils/request';

/**
 * 通知公告定义
 */
export interface Notice {
  noticeId?: number;
  noticeTitle: string;
  noticeType?: number;  // 1=通知 2=公告
  noticeContent?: string;
  status?: number;      // 0=正常 1=关闭
  createBy?: string;
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 通知公告查询参数
 */
export interface NoticeQuery {
  noticeTitle?: string;
  noticeType?: number;
  status?: number;
  createBy?: string;
  pageNum?: number;
  pageSize?: number;
}

/**
 * 查询通知公告列表
 */
export const getNoticeList = (params: NoticeQuery) => {
  return get('/system/notice/list', params);
};

/**
 * 获取通知公告详情
 */
export const getNotice = (noticeId: number) => {
  return get(`/system/notice/${noticeId}`);
};

/**
 * 新增通知公告
 */
export const addNotice = (data: Notice) => {
  return post('/system/notice', data);
};

/**
 * 修改通知公告
 */
export const updateNotice = (data: Notice) => {
  return put('/system/notice', data);
};

/**
 * 删除通知公告
 */
export const deleteNotice = (noticeIds: number[]) => {
  return del(`/system/notice/${noticeIds.join(',')}`);
};
