import React from 'react';
import { Result, Button } from 'antd';
import { useNavigate } from 'react-router-dom';

/**
 * 403无权限页面
 */
const Forbidden: React.FC = () => {
  const navigate = useNavigate();

  const handleBackHome = () => {
    navigate('/dashboard');
  };

  const handleGoBack = () => {
    navigate(-1);
  };

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
      <Result
        status="403"
        title="403"
        subTitle="抱歉，您没有权限访问此页面"
        extra={
          <>
            <Button type="primary" onClick={handleBackHome}>
              返回首页
            </Button>
            <Button onClick={handleGoBack} style={{ marginLeft: 8 }}>
              返回上一页
            </Button>
          </>
        }
      />
    </div>
  );
};

export default Forbidden;
