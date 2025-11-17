import React from 'react';
import { usePermission } from '@/hooks/usePermission';

interface PermissionProps {
  /**
   * 需要的权限标识
   * 支持字符串或字符串数组
   */
  value: string | string[];
  /**
   * 权限验证模式
   * any: 拥有任意一个权限即可显示
   * all: 需要拥有所有权限才显示
   * 默认: any
   */
  mode?: 'any' | 'all';
  /**
   * 子元素
   */
  children: React.ReactNode;
  /**
   * 无权限时显示的内容
   */
  fallback?: React.ReactNode;
}

/**
 * 权限控制组件
 * 根据用户权限自动控制内容的显示/隐藏
 *
 * @example
 * // 单个权限
 * <Permission value="system:user:add">
 *   <Button>新增</Button>
 * </Permission>
 *
 * // 多个权限（任意一个）
 * <Permission value={['system:user:add', 'system:user:edit']} mode="any">
 *   <Button>操作</Button>
 * </Permission>
 *
 * // 多个权限（全部拥有）
 * <Permission value={['system:user:add', 'system:user:edit']} mode="all">
 *   <Button>操作</Button>
 * </Permission>
 *
 * // 无权限时显示备用内容
 * <Permission value="system:user:add" fallback={<span>无权限</span>}>
 *   <Button>新增</Button>
 * </Permission>
 */
const Permission: React.FC<PermissionProps> = ({
  value,
  mode = 'any',
  children,
  fallback = null,
}) => {
  const { hasPermission, hasAnyPermission, hasAllPermissions } = usePermission();

  // 检查权限
  let hasAuth = false;

  if (typeof value === 'string') {
    // 单个权限
    hasAuth = hasPermission(value);
  } else if (Array.isArray(value)) {
    // 多个权限
    if (mode === 'all') {
      hasAuth = hasAllPermissions(value);
    } else {
      hasAuth = hasAnyPermission(value);
    }
  }

  // 返回相应内容
  return <>{hasAuth ? children : fallback}</>;
};

export default Permission;
