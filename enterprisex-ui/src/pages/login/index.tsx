import React, { useState } from 'react';
import { Form, Input, Button, Checkbox, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { post } from '@/utils/request';
import './index.less';

interface LoginForm {
  username: string;
  password: string;
  remember: boolean;
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      const response = await post('/auth/login', {
        username: values.username,
        password: values.password,
      });

      if (response.code === 200) {
        const { accessToken, userInfo } = response.data;

        // 保存token和用户信息
        localStorage.setItem('token', accessToken);
        localStorage.setItem('userInfo', JSON.stringify(userInfo));

        if (values.remember) {
          localStorage.setItem('username', values.username);
        } else {
          localStorage.removeItem('username');
        }

        message.success('登录成功');
        navigate('/');
      }
    } catch (error) {
      console.error('登录失败：', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-logo">
          <h1>EnterpriseX</h1>
          <p>企业级开发框架</p>
        </div>

        <Form
          name="login"
          className="login-form"
          initialValues={{
            remember: true,
            username: localStorage.getItem('username') || '',
          }}
          onFinish={onFinish}
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名: admin"
              size="large"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码: admin123"
              size="large"
            />
          </Form.Item>

          <Form.Item>
            <Form.Item name="remember" valuePropName="checked" noStyle>
              <Checkbox>记住用户名</Checkbox>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              className="login-button"
              size="large"
              loading={loading}
              block
            >
              登录
            </Button>
          </Form.Item>
        </Form>

        <div className="login-footer">
          <p>默认账号: admin / admin123</p>
          <p>Copyright © 2024 EnterpriseX. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default Login;
