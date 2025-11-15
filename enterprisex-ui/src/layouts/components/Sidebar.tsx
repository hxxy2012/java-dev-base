import React, { useEffect, useState } from 'react';
import { Layout, Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { menuItems, MenuItem } from '@/config/menu';
import type { MenuProps } from 'antd';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
}

type MenuItemType = Required<MenuProps>['items'][number];

const Sidebar: React.FC<SidebarProps> = ({ collapsed }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const [openKeys, setOpenKeys] = useState<string[]>([]);

  // 将菜单配置转换为Ant Design Menu组件需要的格式
  const convertToAntdMenuItems = (items: MenuItem[]): MenuItemType[] => {
    return items.map((item) => {
      if (item.children) {
        return {
          key: item.key,
          icon: item.icon,
          label: item.label,
          children: convertToAntdMenuItems(item.children),
        };
      }
      return {
        key: item.key,
        icon: item.icon,
        label: item.label,
        onClick: () => {
          if (item.path) {
            navigate(item.path);
          }
        },
      };
    });
  };

  // 根据当前路径查找菜单key和父级key
  const findMenuKeys = (path: string, items: MenuItem[]): { selectedKey: string; parentKey?: string } | null => {
    for (const item of items) {
      if (item.path === path) {
        return { selectedKey: item.key };
      }
      if (item.children) {
        const found = findMenuKeys(path, item.children);
        if (found) {
          return { ...found, parentKey: item.key };
        }
      }
    }
    return null;
  };

  // 监听路由变化，更新选中的菜单项
  useEffect(() => {
    const result = findMenuKeys(location.pathname, menuItems);
    if (result) {
      setSelectedKeys([result.selectedKey]);
      if (result.parentKey) {
        setOpenKeys([result.parentKey]);
      }
    }
  }, [location.pathname]);

  const handleOpenChange = (keys: string[]) => {
    setOpenKeys(keys);
  };

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
      }}
    >
      <div
        style={{
          height: '64px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: 'rgba(255, 255, 255, 0.2)',
          margin: '0',
        }}
      >
        <h1 style={{ color: '#fff', margin: 0, fontSize: collapsed ? '18px' : '20px' }}>
          {collapsed ? 'EX' : 'EnterpriseX'}
        </h1>
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={selectedKeys}
        openKeys={openKeys}
        onOpenChange={handleOpenChange}
        items={convertToAntdMenuItems(menuItems)}
      />
    </Sider>
  );
};

export default Sidebar;
