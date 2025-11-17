import React, { useEffect } from 'react';
import { Card, Alert, Button, Space } from 'antd';
import { ApiOutlined, ReloadOutlined } from '@ant-design/icons';

/**
 * 系统接口文档页面
 */
const SwaggerPage: React.FC = () => {
  const apiDocUrl = `${import.meta.env.VITE_API_URL}/doc.html`;

  const handleOpenNewTab = () => {
    window.open(apiDocUrl, '_blank');
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <ApiOutlined />
            <span>系统接口文档</span>
          </Space>
        }
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={() => window.location.reload()}>
              刷新
            </Button>
            <Button type="primary" icon={<ApiOutlined />} onClick={handleOpenNewTab}>
              新窗口打开
            </Button>
          </Space>
        }
      >
        <Alert
          message="接口文档说明"
          description="本页面集成了Knife4j接口文档，提供了系统所有API接口的在线查看和测试功能。您可以查看接口详情、参数说明，并进行在线调试。"
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
        />

        <div style={{ height: 'calc(100vh - 280px)', border: '1px solid #d9d9d9', borderRadius: '4px' }}>
          <iframe
            src={apiDocUrl}
            style={{
              width: '100%',
              height: '100%',
              border: 'none',
            }}
            title="API Documentation"
          />
        </div>
      </Card>
    </div>
  );
};

export default SwaggerPage;
