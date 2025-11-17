import React from 'react';
import {
  UserOutlined,
  TeamOutlined,
  MenuOutlined,
  ApartmentOutlined,
  SolutionOutlined,
  BookOutlined,
  SettingOutlined,
  FileTextOutlined,
  MonitorOutlined,
  BellOutlined,
  DashboardOutlined,
  ToolOutlined,
} from '@ant-design/icons';

export interface MenuItem {
  key: string;
  label: string;
  icon?: React.ReactNode;
  path?: string;
  children?: MenuItem[];
}

/**
 * 菜单配置
 */
export const menuItems: MenuItem[] = [
  {
    key: 'dashboard',
    label: '系统首页',
    icon: <DashboardOutlined />,
    path: '/dashboard',
  },
  {
    key: 'system',
    label: '系统管理',
    icon: <SettingOutlined />,
    children: [
      {
        key: 'user',
        label: '用户管理',
        icon: <UserOutlined />,
        path: '/system/user',
      },
      {
        key: 'role',
        label: '角色管理',
        icon: <TeamOutlined />,
        path: '/system/role',
      },
      {
        key: 'menu',
        label: '菜单管理',
        icon: <MenuOutlined />,
        path: '/system/menu',
      },
      {
        key: 'dept',
        label: '部门管理',
        icon: <ApartmentOutlined />,
        path: '/system/dept',
      },
      {
        key: 'post',
        label: '岗位管理',
        icon: <SolutionOutlined />,
        path: '/system/post',
      },
      {
        key: 'dict',
        label: '字典管理',
        icon: <BookOutlined />,
        path: '/system/dict/type',
      },
      {
        key: 'config',
        label: '参数配置',
        icon: <SettingOutlined />,
        path: '/system/config',
      },
      {
        key: 'notice',
        label: '通知公告',
        icon: <BellOutlined />,
        path: '/system/notice',
      },
    ],
  },
  {
    key: 'monitor',
    label: '系统监控',
    icon: <MonitorOutlined />,
    children: [
      {
        key: 'online',
        label: '在线用户',
        icon: <FileTextOutlined />,
        path: '/monitor/online',
      },
      {
        key: 'job',
        label: '定时任务',
        icon: <FileTextOutlined />,
        path: '/monitor/job',
      },
      {
        key: 'cache',
        label: '缓存管理',
        icon: <FileTextOutlined />,
        path: '/monitor/cache',
      },
      {
        key: 'server',
        label: '服务监控',
        icon: <FileTextOutlined />,
        path: '/monitor/server',
      },
      {
        key: 'operlog',
        label: '操作日志',
        icon: <FileTextOutlined />,
        path: '/monitor/operlog',
      },
      {
        key: 'loginlog',
        label: '登录日志',
        icon: <FileTextOutlined />,
        path: '/monitor/loginlog',
      },
      {
        key: 'joblog',
        label: '任务日志',
        icon: <FileTextOutlined />,
        path: '/monitor/job/log',
      },
    ],
  },
  {
    key: 'tool',
    label: '系统工具',
    icon: <ToolOutlined />,
    children: [
      {
        key: 'gen',
        label: '代码生成',
        icon: <FileTextOutlined />,
        path: '/tool/gen',
      },
      {
        key: 'file',
        label: '文件管理',
        icon: <FileTextOutlined />,
        path: '/system/file',
      },
      {
        key: 'swagger',
        label: '系统接口',
        icon: <FileTextOutlined />,
        path: '/tool/swagger',
      },
      {
        key: 'systemtool',
        label: '系统工具',
        icon: <FileTextOutlined />,
        path: '/tool/system',
      },
    ],
  },
];

/**
 * 从菜单配置中提取所有路径
 */
export const getAllPaths = (items: MenuItem[]): string[] => {
  const paths: string[] = [];
  const extract = (menuList: MenuItem[]) => {
    menuList.forEach((item) => {
      if (item.path) {
        paths.push(item.path);
      }
      if (item.children) {
        extract(item.children);
      }
    });
  };
  extract(items);
  return paths;
};
