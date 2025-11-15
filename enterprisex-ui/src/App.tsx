import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Login from '@/pages/login';
import MainLayout from '@/layouts/MainLayout';
import PrivateRoute from '@/components/PrivateRoute';

// Dashboard
import Dashboard from '@/pages/dashboard';

// Profile
import Profile from '@/pages/profile';

// System pages
import User from '@/pages/system/user';
import Role from '@/pages/system/role';
import Menu from '@/pages/system/menu';
import Dept from '@/pages/system/dept';
import Post from '@/pages/system/post';
import DictType from '@/pages/system/dict/type';
import DictData from '@/pages/system/dict/data';
import Config from '@/pages/system/config';
import Notice from '@/pages/system/notice';

// Monitor pages
import OperLog from '@/pages/monitor/operlog';
import LoginLog from '@/pages/monitor/loginlog';

// Error pages
import NotFound from '@/pages/error/NotFound';
import Forbidden from '@/pages/error/Forbidden';

import 'dayjs/locale/zh-cn';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
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

            {/* Dashboard */}
            <Route path="dashboard" element={<Dashboard />} />

            {/* Profile */}
            <Route path="profile" element={<Profile />} />

            {/* System management routes */}
            <Route path="system/user" element={<User />} />
            <Route path="system/role" element={<Role />} />
            <Route path="system/menu" element={<Menu />} />
            <Route path="system/dept" element={<Dept />} />
            <Route path="system/post" element={<Post />} />
            <Route path="system/dict/type" element={<DictType />} />
            <Route path="system/dict/data" element={<DictData />} />
            <Route path="system/config" element={<Config />} />
            <Route path="system/notice" element={<Notice />} />

            {/* System monitoring routes */}
            <Route path="monitor/operlog" element={<OperLog />} />
            <Route path="monitor/loginlog" element={<LoginLog />} />
          </Route>

          {/* Catch all - 404 page */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
