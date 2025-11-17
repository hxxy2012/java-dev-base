import React from 'react';
import { Button, ButtonProps } from 'antd';

export interface AuthButtonProps extends ButtonProps {
  /**
   * 权限标识
   */
  permission?: string;
  /**
   * 权限列表（当前用户拥有的权限）
   */
  permissions?: string[];
}

/**
 * 权限按钮组件
 * 根据权限标识控制按钮的显示/隐藏
 */
const AuthButton: React.FC<AuthButtonProps> = ({
  permission,
  permissions = [],
  children,
  ...restProps
}) => {
  // 如果没有指定权限标识，直接显示按钮
  if (!permission) {
    return <Button {...restProps}>{children}</Button>;
  }

  // 检查权限
  const hasPermission = permissions.includes(permission) || permissions.includes('*:*:*');

  // 没有权限则不显示
  if (!hasPermission) {
    return null;
  }

  return <Button {...restProps}>{children}</Button>;
};

export default AuthButton;
