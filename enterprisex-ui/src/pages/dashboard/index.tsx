import React, { useEffect, useState } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  Table,
  Tag,
  Space,
  Typography,
} from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  FileTextOutlined,
  SafetyOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons';
import { getOperLogList, OperLog } from '@/api/monitor/log';
import { getLoginLogList, LoginLog } from '@/api/monitor/log';

const { Title, Paragraph } = Typography;

/**
 * 仪表盘
 */
const Dashboard: React.FC = () => {
  const [operLogs, setOperLogs] = useState<OperLog[]>([]);
  const [loginLogs, setLoginLogs] = useState<LoginLog[]>([]);
  const [loading, setLoading] = useState(false);

  // 加载最近操作日志
  const loadOperLogs = async () => {
    try {
      setLoading(true);
      const response = await getOperLogList({ pageNum: 1, pageSize: 5 });
      setOperLogs(response.rows || []);
    } catch (error) {
      console.error('加载操作日志失败', error);
    } finally {
      setLoading(false);
    }
  };

  // 加载最近登录日志
  const loadLoginLogs = async () => {
    try {
      setLoading(true);
      const response = await getLoginLogList({ pageNum: 1, pageSize: 5 });
      setLoginLogs(response.rows || []);
    } catch (error) {
      console.error('加载登录日志失败', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOperLogs();
    loadLoginLogs();
  }, []);

  // 操作日志表格列
  const operLogColumns = [
    {
      title: '操作模块',
      dataIndex: 'title',
      key: 'title',
      width: 150,
    },
    {
      title: '操作人员',
      dataIndex: 'operName',
      key: 'operName',
      width: 120,
    },
    {
      title: '操作地址',
      dataIndex: 'operIp',
      key: 'operIp',
      width: 150,
    },
    {
      title: '操作状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) =>
        status === 0 ? (
          <Tag color="success">成功</Tag>
        ) : (
          <Tag color="error">失败</Tag>
        ),
    },
    {
      title: '操作时间',
      dataIndex: 'operTime',
      key: 'operTime',
      width: 180,
    },
  ];

  // 登录日志表格列
  const loginLogColumns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '登录地址',
      dataIndex: 'ipaddr',
      key: 'ipaddr',
      width: 150,
    },
    {
      title: '登录地点',
      dataIndex: 'loginLocation',
      key: 'loginLocation',
      width: 120,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      key: 'browser',
      width: 100,
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      key: 'os',
      width: 100,
    },
    {
      title: '登录状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) =>
        status === 1 ? (
          <Tag color="success">成功</Tag>
        ) : (
          <Tag color="error">失败</Tag>
        ),
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
      width: 180,
    },
  ];

  return (
    <div style={{ padding: '24px', background: '#f0f2f5', minHeight: 'calc(100vh - 112px)' }}>
      {/* 欢迎信息 */}
      <Card style={{ marginBottom: 24 }}>
        <Title level={3}>欢迎使用 EnterpriseX 企业级开发框架</Title>
        <Paragraph>
          这是一个基于 Spring Cloud + Spring Boot 3.2 + React 18 的微服务架构企业级开发框架，
          提供完善的权限管理、系统管理、日志管理等功能模块。
        </Paragraph>
      </Card>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="总用户数"
              value={1128}
              prefix={<UserOutlined />}
              suffix="人"
              valueStyle={{ color: '#3f8600' }}
            />
            <div style={{ marginTop: 8 }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} /> 12% 较上周
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="活跃角色"
              value={23}
              prefix={<TeamOutlined />}
              suffix="个"
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: 8 }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} /> 5% 较上周
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日访问"
              value={8459}
              prefix={<FileTextOutlined />}
              suffix="次"
              valueStyle={{ color: '#cf1322' }}
            />
            <div style={{ marginTop: 8 }}>
              <ArrowDownOutlined style={{ color: '#cf1322' }} /> 3% 较昨日
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="系统安全"
              value={98.5}
              prefix={<SafetyOutlined />}
              suffix="%"
              precision={1}
              valueStyle={{ color: '#3f8600' }}
            />
            <div style={{ marginTop: 8 }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} /> 1.2% 较上周
            </div>
          </Card>
        </Col>
      </Row>

      {/* 最近操作日志 */}
      <Card
        title={
          <Space>
            <FileTextOutlined />
            <span>最近操作日志</span>
          </Space>
        }
        style={{ marginBottom: 24 }}
      >
        <Table
          columns={operLogColumns}
          dataSource={operLogs}
          rowKey="operId"
          loading={loading}
          pagination={false}
          size="small"
        />
      </Card>

      {/* 最近登录日志 */}
      <Card
        title={
          <Space>
            <SafetyOutlined />
            <span>最近登录日志</span>
          </Space>
        }
      >
        <Table
          columns={loginLogColumns}
          dataSource={loginLogs}
          rowKey="infoId"
          loading={loading}
          pagination={false}
          size="small"
        />
      </Card>
    </div>
  );
};

export default Dashboard;
