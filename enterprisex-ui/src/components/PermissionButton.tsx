import React from 'react';
import { Button, ButtonProps } from 'antd';
import { usePermission } from '@/hooks/usePermission';

interface PermissionButtonProps extends ButtonProps {
  /**
   * 需要的权限标识
   * 支持字符串或字符串数组
   */
  permission?: string | string[];
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
  children?: React.ReactNode;
}

/**
 * 权限按钮组件
 * 根据用户权限自动控制按钮的显示/隐藏
 */
const PermissionButton: React.FC<PermissionButtonProps> = ({
  permission,
  mode = 'any',
  children,
  ...buttonProps
}) => {
  const { hasPermission, hasAnyPermission, hasAllPermissions } = usePermission();

  // 如果没有指定权限，直接显示
  if (!permission) {
    return <Button {...buttonProps}>{children}</Button>;
  }

  // 检查权限
  let hasAuth = false;

  if (typeof permission === 'string') {
    // 单个权限
    hasAuth = hasPermission(permission);
  } else if (Array.isArray(permission)) {
    // 多个权限
    if (mode === 'all') {
      hasAuth = hasAllPermissions(permission);
    } else {
      hasAuth = hasAnyPermission(permission);
    }
  }

  // 没有权限则不显示
  if (!hasAuth) {
    return null;
  }

  return <Button {...buttonProps}>{children}</Button>;
};

export default PermissionButton;
