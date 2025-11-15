import React from 'react';
import { Spin } from 'antd';

/**
 * 页面加载组件
 */
const PageLoading: React.FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: '#f0f2f5',
      }}
    >
      <Spin size="large" tip="加载中..." />
    </div>
  );
};

export default PageLoading;
