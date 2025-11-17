import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { UserInfo, RouteMenu } from '@/api/auth';

interface AuthState {
  // 状态
  token: string | null;
  userInfo: UserInfo | null;
  permissions: string[];
  routes: RouteMenu[];

  // Actions
  setToken: (token: string | null) => void;
  setUserInfo: (userInfo: UserInfo | null) => void;
  setPermissions: (permissions: string[]) => void;
  setRoutes: (routes: RouteMenu[]) => void;
  clearAuth: () => void;

  // 权限检查
  hasPermission: (permission: string) => boolean;
  hasAnyPermission: (permissions: string[]) => boolean;
  hasAllPermissions: (permissions: string[]) => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // 初始状态
      token: null,
      userInfo: null,
      permissions: [],
      routes: [],

      // 设置Token
      setToken: (token) => set({ token }),

      // 设置用户信息
      setUserInfo: (userInfo) => {
        const permissions = userInfo?.permissions || [];
        set({ userInfo, permissions });
      },

      // 设置权限列表
      setPermissions: (permissions) => set({ permissions }),

      // 设置路由
      setRoutes: (routes) => set({ routes }),

      // 清除认证信息
      clearAuth: () =>
        set({
          token: null,
          userInfo: null,
          permissions: [],
          routes: [],
        }),

      // 检查是否有某个权限
      hasPermission: (permission) => {
        const { permissions } = get();
        // 超级管理员拥有所有权限
        if (permissions.includes('*:*:*')) {
          return true;
        }
        return permissions.includes(permission);
      },

      // 检查是否有任意一个权限
      hasAnyPermission: (perms) => {
        const { permissions } = get();
        if (permissions.includes('*:*:*')) {
          return true;
        }
        return perms.some((perm) => permissions.includes(perm));
      },

      // 检查是否拥有所有权限
      hasAllPermissions: (perms) => {
        const { permissions } = get();
        if (permissions.includes('*:*:*')) {
          return true;
        }
        return perms.every((perm) => permissions.includes(perm));
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        token: state.token,
        userInfo: state.userInfo,
        permissions: state.permissions,
        routes: state.routes,
      }),
    }
  )
);
