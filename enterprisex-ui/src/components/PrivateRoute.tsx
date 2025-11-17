import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';

interface PrivateRouteProps {
  children: React.ReactElement;
}

/**
 * 私有路由组件 - 需要登录才能访问
 */
const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { token } = useAuthStore();

  if (!token) {
    // 未登录，重定向到登录页
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default PrivateRoute;
