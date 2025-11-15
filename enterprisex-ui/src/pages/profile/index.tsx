import React, { useState } from 'react';
import {
  Card,
  Descriptions,
  Avatar,
  Button,
  Form,
  Input,
  message,
  Modal,
  Tabs,
  Row,
  Col,
} from 'antd';
import {
  UserOutlined,
  EditOutlined,
  LockOutlined,
  MailOutlined,
  PhoneOutlined,
} from '@ant-design/icons';

const { TabPane } = Tabs;

/**
 * 个人中心页面
 */
const Profile: React.FC = () => {
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [editForm] = Form.useForm();
  const [passwordForm] = Form.useForm();

  // 模拟当前用户信息
  const [userInfo, setUserInfo] = useState({
    userId: 1,
    username: 'admin',
    nickname: '系统管理员',
    email: 'admin@enterprisex.com',
    phonenumber: '15888888888',
    sex: '男',
    dept: '研发部',
    roles: '超级管理员',
    createTime: '2024-01-01 00:00:00',
  });

  // 打开编辑弹窗
  const handleEdit = () => {
    editForm.setFieldsValue({
      nickname: userInfo.nickname,
      email: userInfo.email,
      phonenumber: userInfo.phonenumber,
      sex: userInfo.sex,
    });
    setEditModalVisible(true);
  };

  // 提交编辑
  const handleEditSubmit = async () => {
    try {
      const values = await editForm.validateFields();
      // TODO: 调用API更新用户信息
      setUserInfo({ ...userInfo, ...values });
      message.success('个人信息修改成功');
      setEditModalVisible(false);
    } catch (error) {
      console.error('Validate Failed:', error);
    }
  };

  // 打开修改密码弹窗
  const handleChangePassword = () => {
    passwordForm.resetFields();
    setPasswordModalVisible(true);
  };

  // 提交修改密码
  const handlePasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (values.newPassword !== values.confirmPassword) {
        message.error('两次输入的密码不一致');
        return;
      }
      // TODO: 调用API修改密码
      message.success('密码修改成功，请重新登录');
      setPasswordModalVisible(false);
      passwordForm.resetFields();
    } catch (error) {
      console.error('Validate Failed:', error);
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={24}>
        {/* 左侧个人信息卡片 */}
        <Col xs={24} lg={8}>
          <Card>
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <Avatar size={100} icon={<UserOutlined />} />
              <h2 style={{ marginTop: 16, marginBottom: 8 }}>{userInfo.nickname}</h2>
              <p style={{ color: '#666' }}>@{userInfo.username}</p>
            </div>
            <Descriptions column={1} size="small">
              <Descriptions.Item label="部门">{userInfo.dept}</Descriptions.Item>
              <Descriptions.Item label="角色">{userInfo.roles}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{userInfo.createTime}</Descriptions.Item>
            </Descriptions>
            <div style={{ marginTop: 24, textAlign: 'center' }}>
              <Button type="primary" icon={<EditOutlined />} onClick={handleEdit} style={{ marginRight: 8 }}>
                编辑资料
              </Button>
              <Button icon={<LockOutlined />} onClick={handleChangePassword}>
                修改密码
              </Button>
            </div>
          </Card>
        </Col>

        {/* 右侧详细信息 */}
        <Col xs={24} lg={16}>
          <Card>
            <Tabs defaultActiveKey="basic">
              <TabPane tab="基本资料" key="basic">
                <Descriptions bordered column={2}>
                  <Descriptions.Item label="用户编号" span={2}>
                    {userInfo.userId}
                  </Descriptions.Item>
                  <Descriptions.Item label="用户名称" span={2}>
                    {userInfo.username}
                  </Descriptions.Item>
                  <Descriptions.Item label="用户昵称" span={2}>
                    {userInfo.nickname}
                  </Descriptions.Item>
                  <Descriptions.Item label="性别" span={2}>
                    {userInfo.sex}
                  </Descriptions.Item>
                  <Descriptions.Item label="手机号码" span={2}>
                    <PhoneOutlined /> {userInfo.phonenumber}
                  </Descriptions.Item>
                  <Descriptions.Item label="用户邮箱" span={2}>
                    <MailOutlined /> {userInfo.email}
                  </Descriptions.Item>
                  <Descriptions.Item label="所属部门" span={2}>
                    {userInfo.dept}
                  </Descriptions.Item>
                  <Descriptions.Item label="所属角色" span={2}>
                    {userInfo.roles}
                  </Descriptions.Item>
                  <Descriptions.Item label="创建时间" span={2}>
                    {userInfo.createTime}
                  </Descriptions.Item>
                </Descriptions>
              </TabPane>
            </Tabs>
          </Card>
        </Col>
      </Row>

      {/* 编辑资料弹窗 */}
      <Modal
        title="编辑资料"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        width={600}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item
            name="nickname"
            label="用户昵称"
            rules={[{ required: true, message: '请输入用户昵称' }]}
          >
            <Input placeholder="请输入用户昵称" />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item
            name="phonenumber"
            label="手机号码"
            rules={[
              { required: true, message: '请输入手机号码' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码' },
            ]}
          >
            <Input prefix={<PhoneOutlined />} placeholder="请输入手机号码" />
          </Form.Item>
          <Form.Item name="sex" label="性别">
            <Input placeholder="请输入性别" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 修改密码弹窗 */}
      <Modal
        title="修改密码"
        open={passwordModalVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordModalVisible(false)}
        width={500}
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            name="oldPassword"
            label="旧密码"
            rules={[{ required: true, message: '请输入旧密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入旧密码" />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度不能少于6位' },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认密码"
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请确认新密码" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Profile;
