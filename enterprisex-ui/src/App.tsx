import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Login from '@/pages/login';
import MainLayout from '@/layouts/MainLayout';
import PrivateRoute from '@/components/PrivateRoute';

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

import 'dayjs/locale/zh-cn';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<Login />} />

          {/* Protected routes with main layout */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                <MainLayout />
              </PrivateRoute>
            }
          >
            {/* Default redirect to user management */}
            <Route index element={<Navigate to="/system/user" replace />} />

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

          {/* Catch all - redirect to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
