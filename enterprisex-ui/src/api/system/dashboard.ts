import request from '@/utils/request';

/**
 * Dashboard统计数据
 */
export interface DashboardStats {
  userCount: number;
  roleCount: number;
  deptCount: number;
  onlineUserCount: number;
  todayLoginCount: number;
  todayOperCount: number;
  unreadMessageCount: number;
  recentLogins: RecentLogin[];
  recentOperations: RecentOperation[];
  userStatusStats: Record<string, number>;
  weeklyLoginStats: WeeklyLoginStat[];
}

export interface RecentLogin {
  username: string;
  ip: string;
  time: string;
  status: string;
}

export interface RecentOperation {
  title: string;
  operator: string;
  time: string;
  status: string;
}

export interface WeeklyLoginStat {
  date: string;
  count: number;
}

export interface QuickLink {
  name: string;
  path: string;
  icon: string;
  color: string;
}

/**
 * 获取Dashboard统计数据
 */
export const getDashboardStats = () => {
  return request<DashboardStats>({
    url: '/system/dashboard/stats',
    method: 'get',
  });
};

/**
 * 获取快速访问链接
 */
export const getQuickLinks = () => {
  return request<QuickLink[]>({
    url: '/system/dashboard/quickLinks',
    method: 'get',
  });
};
