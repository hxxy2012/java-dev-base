import React from 'react';
import { Layout, Avatar, Dropdown, Space } from 'antd';
import {
  UserOutlined,
  LogoutOutlined,
  SettingOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useNavigate } from 'react-router-dom';

const { Header: AntHeader } = Layout;

interface HeaderProps {
  collapsed: boolean;
  onToggle: () => void;
}

const Header: React.FC<HeaderProps> = ({ collapsed, onToggle }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // 清除token
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    // 跳转到登录页
    navigate('/login');
  };

  const items: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
      onClick: () => {
        // TODO: 跳转到个人中心页面
        console.log('个人中心');
      },
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '个人设置',
      onClick: () => {
        // TODO: 跳转到设置页面
        console.log('个人设置');
      },
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <AntHeader
      style={{
        padding: '0 16px',
        background: '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        borderBottom: '1px solid #f0f0f0',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
          className: 'trigger',
          onClick: onToggle,
          style: { fontSize: '18px', cursor: 'pointer' },
        })}
        <h2 style={{ margin: '0 0 0 24px', fontSize: '18px', fontWeight: 600 }}>
          EnterpriseX 管理系统
        </h2>
      </div>

      <Dropdown menu={{ items }} placement="bottomRight">
        <Space style={{ cursor: 'pointer' }}>
          <Avatar icon={<UserOutlined />} />
          <span>管理员</span>
        </Space>
      </Dropdown>
    </AntHeader>
  );
};

export default Header;
