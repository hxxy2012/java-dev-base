import request from '@/utils/request';

/**
 * 消息类型
 */
export interface Message {
  messageId?: number;
  title: string;
  content: string;
  messageType?: number; // 1=系统消息 2=通知消息 3=待办消息
  level?: number; // 1=普通 2=重要 3=紧急
  userId?: number;
  isRead?: number;
  readTime?: string;
  senderId?: number;
  senderName?: string;
  createTime?: string;
}

/**
 * 消息查询参数
 */
export interface MessageQuery {
  messageType?: number;
  isRead?: number;
  level?: number;
}

/**
 * 获取消息列表
 */
export const getMessageList = (params?: MessageQuery) => {
  return request({
    url: '/system/message/list',
    method: 'get',
    params,
  });
};

/**
 * 获取未读消息数量
 */
export const getUnreadCount = () => {
  return request<number>({
    url: '/system/message/unreadCount',
    method: 'get',
  });
};

/**
 * 标记消息为已读
 */
export const markAsRead = (messageId: number) => {
  return request({
    url: `/system/message/read/${messageId}`,
    method: 'put',
  });
};

/**
 * 批量标记消息为已读
 */
export const batchMarkAsRead = (messageIds: number[]) => {
  return request({
    url: '/system/message/read/batch',
    method: 'put',
    data: messageIds,
  });
};

/**
 * 全部标记为已读
 */
export const markAllAsRead = () => {
  return request({
    url: '/system/message/read/all',
    method: 'put',
  });
};

/**
 * 删除消息
 */
export const deleteMessage = (messageIds: number[]) => {
  return request({
    url: `/system/message/${messageIds.join(',')}`,
    method: 'delete',
  });
};

/**
 * 发送消息
 */
export const sendMessage = (data: Message) => {
  return request({
    url: '/system/message/send',
    method: 'post',
    data,
  });
};

/**
 * 发送广播消息
 */
export const broadcastMessage = (data: Message) => {
  return request({
    url: '/system/message/broadcast',
    method: 'post',
    data,
  });
};
