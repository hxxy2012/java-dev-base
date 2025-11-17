import { Suspense, lazy, useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { useAuthStore } from '@/store/authStore';
import { transformMenuToRoutes } from '@/utils/routeUtils';
import PrivateRoute from '@/components/PrivateRoute';
import PageLoading from '@/components/PageLoading';
import 'dayjs/locale/zh-cn';

// Lazy load pages
const Login = lazy(() => import('@/pages/login'));
const MainLayout = lazy(() => import('@/layouts/MainLayout'));
const Dashboard = lazy(() => import('@/pages/dashboard'));
const Profile = lazy(() => import('@/pages/profile'));
const NotFound = lazy(() => import('@/pages/error/NotFound'));
const Forbidden = lazy(() => import('@/pages/error/Forbidden'));

// Static pages
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
const ServerMonitor = lazy(() => import('@/pages/monitor/server'));

function App() {
  const { routes } = useAuthStore();
  const [dynamicRoutes, setDynamicRoutes] = useState<any[]>([]);

  useEffect(() => {
    // 将菜单转换为路由
    if (routes && routes.length > 0) {
      const generatedRoutes = transformMenuToRoutes(routes);
      setDynamicRoutes(generatedRoutes);
    } else {
      // 如果没有动态路由，使用默认静态路由
      setDynamicRoutes([
        { path: 'dashboard', element: <Dashboard /> },
        { path: 'profile', element: <Profile /> },
        { path: 'system/user', element: <User /> },
        { path: 'system/role', element: <Role /> },
        { path: 'system/menu', element: <Menu /> },
        { path: 'system/dept', element: <Dept /> },
        { path: 'system/post', element: <Post /> },
        { path: 'system/dict/type', element: <DictType /> },
        { path: 'system/dict/data', element: <DictData /> },
        { path: 'system/config', element: <Config /> },
        { path: 'system/notice', element: <Notice /> },
        { path: 'monitor/operlog', element: <OperLog /> },
        { path: 'monitor/loginlog', element: <LoginLog /> },
        { path: 'monitor/online', element: <OnlineUser /> },
        { path: 'monitor/job', element: <JobManage /> },
        { path: 'monitor/cache', element: <CacheManage /> },
        { path: 'monitor/server', element: <ServerMonitor /> },
      ]);
    }
  }, [routes]);

  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Suspense fallback={<PageLoading />}>
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/403" element={<Forbidden />} />
            <Route path="/404" element={<NotFound />} />

            {/* Protected routes with main layout */}
            <Route
              path="/"
              element={
                <PrivateRoute>
                  <MainLayout />
                </PrivateRoute>
              }
            >
              {/* Default redirect to dashboard */}
              <Route index element={<Navigate to="/dashboard" replace />} />

              {/* Fixed routes */}
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="profile" element={<Profile />} />

              {/* Dynamic routes */}
              {dynamicRoutes.map((route: any, index: number) => (
                <Route key={index} path={route.path} element={route.element} />
              ))}
            </Route>

            {/* Catch all - 404 page */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
