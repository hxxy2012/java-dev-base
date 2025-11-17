import React, { lazy, Suspense } from 'react';
import { RouteObject } from 'react-router-dom';
import { RouteMenu } from '@/api/auth';
import PageLoading from '@/components/PageLoading';

// 导入所有页面组件
const Dashboard = lazy(() => import('@/pages/dashboard'));
const Profile = lazy(() => import('@/pages/profile'));
const User = lazy(() => import('@/pages/system/user'));
const Role = lazy(() => import('@/pages/system/role'));
const Menu = lazy(() => import('@/pages/system/menu'));
const Dept = lazy(() => import('@/pages/system/dept'));
const Post = lazy(() => import('@/pages/system/post'));
const DictType = lazy(() => import('@/pages/system/dict/type'));
const DictData = lazy(() => import('@/pages/system/dict/data'));
const Config = lazy(() => import('@/pages/system/config'));
const Notice = lazy(() => import('@/pages/system/notice'));
const OperLog = lazy(() => import('@/pages/monitor/operlog'));
const LoginLog = lazy(() => import('@/pages/monitor/loginlog'));
const OnlineUser = lazy(() => import('@/pages/monitor/online'));
const CacheManage = lazy(() => import('@/pages/monitor/cache'));
const JobManage = lazy(() => import('@/pages/monitor/job'));
const JobLog = lazy(() => import('@/pages/monitor/job/log'));
const ServerMonitor = lazy(() => import('@/pages/monitor/server'));
const CodeGen = lazy(() => import('@/pages/tool/gen'));
const FileManage = lazy(() => import('@/pages/system/file'));
const DatabasePage = lazy(() => import('@/pages/tool/database'));
const SwaggerDoc = lazy(() => import('@/pages/tool/swagger'));
const SystemTool = lazy(() => import('@/pages/tool/system'));
const HealthCheckPage = lazy(() => import('@/pages/monitor/health'));

// 组件映射表
const componentMap: Record<string, React.LazyExoticComponent<React.FC<any>>> = {
  'dashboard/index': Dashboard,
  'profile/index': Profile,
  'system/user/index': User,
  'system/role/index': Role,
  'system/menu/index': Menu,
  'system/dept/index': Dept,
  'system/post/index': Post,
  'system/dict/type/index': DictType,
  'system/dict/data/index': DictData,
  'system/config/index': Config,
  'system/notice/index': Notice,
  'monitor/operlog/index': OperLog,
  'monitor/loginlog/index': LoginLog,
  'monitor/online/index': OnlineUser,
  'monitor/job/index': JobManage,
  'monitor/job/log/index': JobLog,
  'monitor/cache/index': CacheManage,
  'monitor/server/index': ServerMonitor,
  'tool/gen/index': CodeGen,
  'system/file/index': FileManage,
  'tool/database/index': DatabasePage,
  'tool/swagger/index': SwaggerDoc,
  'tool/system/index': SystemTool,
  'monitor/health/index': HealthCheckPage,
};

/**
 * 将组件路径转换为React组件
 */
const getComponent = (componentPath?: string): React.ReactNode => {
  if (!componentPath) {
    return null;
  }

  const Component = componentMap[componentPath];
  if (!Component) {
    console.warn(`Component not found for path: ${componentPath}`);
    return null;
  }

  return (
    <Suspense fallback={<PageLoading />}>
      <Component />
    </Suspense>
  );
};

/**
 * 将后端菜单数据转换为前端路由配置
 */
export const transformMenuToRoutes = (menus: RouteMenu[]): RouteObject[] => {
  const routes: RouteObject[] = [];

  const traverse = (menuList: RouteMenu[]) => {
    menuList.forEach((menu) => {
      // 只处理菜单类型（C）的项，目录（M）和按钮（F）不生成路由
      if (menu.menuType === 'C' && menu.path && menu.component) {
        // 只有显示且正常状态的菜单才添加到路由
        if (menu.visible === 1 && menu.status === 1) {
          routes.push({
            path: menu.path.startsWith('/') ? menu.path.slice(1) : menu.path,
            element: getComponent(menu.component),
          });
        }
      }

      // 递归处理子菜单
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return routes;
};

/**
 * 从路由菜单中提取所有权限标识
 */
export const extractPermissions = (menus: RouteMenu[]): string[] => {
  const permissions: string[] = [];

  const traverse = (menuList: RouteMenu[]) => {
    menuList.forEach((menu) => {
      if (menu.perms) {
        permissions.push(menu.perms);
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return permissions;
};

/**
 * 构建面包屑导航数据
 */
export const buildBreadcrumb = (
  path: string,
  menus: RouteMenu[]
): { name: string; path?: string }[] => {
  const breadcrumb: { name: string; path?: string }[] = [];

  const findPath = (menuList: RouteMenu[], targetPath: string, parents: RouteMenu[] = []): boolean => {
    for (const menu of menuList) {
      if (menu.path === targetPath && menu.menuType === 'C') {
        // 找到目标菜单，构建面包屑
        parents.forEach((parent) => {
          breadcrumb.push({
            name: parent.menuName,
            path: parent.menuType === 'C' ? parent.path : undefined,
          });
        });
        breadcrumb.push({
          name: menu.menuName,
          path: menu.path,
        });
        return true;
      }

      if (menu.children && menu.children.length > 0) {
        if (findPath(menu.children, targetPath, [...parents, menu])) {
          return true;
        }
      }
    }
    return false;
  };

  findPath(menus, path);
  return breadcrumb;
};
