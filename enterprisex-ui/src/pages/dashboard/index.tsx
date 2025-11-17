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
  Progress,
  List,
  Avatar,
  Button,
} from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  FileTextOutlined,
  SafetyOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  ApartmentOutlined,
  BellOutlined,
  LoginOutlined,
  ToolOutlined,
  RightOutlined,
} from '@ant-design/icons';
import * as Icons from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getDashboardStats, getQuickLinks, type DashboardStats, type QuickLink } from '@/api/system/dashboard';
import { formatDateTime } from '@/utils/date';

const { Title, Paragraph, Text } = Typography;

/**
 * Dashboard仪表盘
 */
const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [quickLinks, setQuickLinks] = useState<QuickLink[]>([]);
  const [loading, setLoading] = useState(false);

  // 加载统计数据
  const loadStats = async () => {
    setLoading(true);
    try {
      const response = await getDashboardStats();
      setStats(response.data);
    } catch (error) {
      console.error('加载统计数据失败', error);
    } finally {
      setLoading(false);
    }
  };

  // 加载快速访问链接
  const loadQuickLinks = async () => {
    try {
      const response = await getQuickLinks();
      setQuickLinks(response.data || []);
    } catch (error) {
      console.error('加载快速链接失败', error);
    }
  };

  useEffect(() => {
    loadStats();
    loadQuickLinks();
  }, []);

  // 获取Icon组件
  const getIcon = (iconName: string) => {
    const IconComponent = (Icons as any)[iconName];
    return IconComponent ? <IconComponent /> : <ToolOutlined />;
  };

  return (
    <div style={{ padding: '24px', background: '#f0f2f5', minHeight: 'calc(100vh - 112px)' }}>
      {/* 欢迎信息 */}
      <Card style={{ marginBottom: 24, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
        <div style={{ color: 'white' }}>
          <Title level={2} style={{ color: 'white', marginBottom: 8 }}>
            欢迎使用 EnterpriseX 企业级开发框架 🎉
          </Title>
          <Paragraph style={{ color: 'rgba(255,255,255,0.9)', fontSize: 16, marginBottom: 0 }}>
            这是一个基于 Spring Cloud + Spring Boot 3.2 + React 18 的微服务架构企业级开发框架，
            提供完善的权限管理、系统管理、监控管理等功能模块。
          </Paragraph>
        </div>
      </Card>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="总用户数"
              value={stats?.userCount || 0}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} /> 12% 较上周
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="角色数量"
              value={stats?.roleCount || 0}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} /> 5% 较上周
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="部门数量"
              value={stats?.deptCount || 0}
              prefix={<ApartmentOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              稳定运行
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="今日登录"
              value={stats?.todayLoginCount || 0}
              prefix={<LoginOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              <ArrowDownOutlined style={{ color: '#cf1322' }} /> 3% 较昨日
            </div>
          </Card>
        </Col>
      </Row>

      {/* 第二行统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="在线用户"
              value={stats?.onlineUserCount || 0}
              prefix={<SafetyOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="今日操作"
              value={stats?.todayOperCount || 0}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} loading={loading}>
            <Statistic
              title="未读消息"
              value={stats?.unreadMessageCount || 0}
              prefix={<BellOutlined />}
              valueStyle={{ color: '#eb2f96' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false}>
            <div>
              <div style={{ fontSize: 14, color: '#999', marginBottom: 8 }}>系统健康度</div>
              <Progress percent={98.5} status="active" strokeColor="#52c41a" />
              <div style={{ marginTop: 4, fontSize: 12, color: '#999' }}>运行正常</div>
            </div>
          </Card>
        </Col>
      </Row>

      {/* 中间内容区 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        {/* 快速访问 */}
        <Col xs={24} lg={12}>
          <Card title={<Space><ToolOutlined /> 快速访问</Space>} bordered={false}>
            <Row gutter={[16, 16]}>
              {quickLinks.map((link) => (
                <Col span={6} key={link.path}>
                  <Card
                    hoverable
                    style={{ textAlign: 'center', cursor: 'pointer' }}
                    bodyStyle={{ padding: '16px 8px' }}
                    onClick={() => navigate(link.path)}
                  >
                    <div style={{ fontSize: 32, color: link.color, marginBottom: 8 }}>
                      {getIcon(link.icon)}
                    </div>
                    <div style={{ fontSize: 12 }}>{link.name}</div>
                  </Card>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>

        {/* 用户状态统计 */}
        <Col xs={24} lg={12}>
          <Card title={<Space><UserOutlined /> 用户状态统计</Space>} bordered={false}>
            <div style={{ padding: '20px 0' }}>
              {stats?.userStatusStats && Object.entries(stats.userStatusStats).map(([status, count]) => (
                <div key={status} style={{ marginBottom: 16 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                    <Text>{status}用户</Text>
                    <Text strong>{count}</Text>
                  </div>
                  <Progress
                    percent={Number(((count / (stats.userCount || 1)) * 100).toFixed(1))}
                    strokeColor={status === '正常' ? '#52c41a' : '#ff4d4f'}
                  />
                </div>
              ))}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 最近登录和操作 */}
      <Row gutter={16}>
        <Col xs={24} lg={12}>
          <Card
            title={<Space><LoginOutlined /> 最近登录</Space>}
            bordered={false}
            extra={<Button type="link" icon={<RightOutlined />} onClick={() => navigate('/monitor/loginlog')}>更多</Button>}
          >
            <List
              dataSource={stats?.recentLogins || []}
              loading={loading}
              renderItem={(item: any) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Avatar icon={<UserOutlined />} style={{ backgroundColor: '#1890ff' }} />}
                    title={<Text strong>{item.username}</Text>}
                    description={
                      <Space direction="vertical" size={0}>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          IP: {item.ip}
                        </Text>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          {formatDateTime(item.time)}
                        </Text>
                      </Space>
                    }
                  />
                  <Tag color={item.status === '成功' ? 'success' : 'error'}>{item.status}</Tag>
                </List.Item>
              )}
            />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card
            title={<Space><FileTextOutlined /> 最近操作</Space>}
            bordered={false}
            extra={<Button type="link" icon={<RightOutlined />} onClick={() => navigate('/monitor/operlog')}>更多</Button>}
          >
            <List
              dataSource={stats?.recentOperations || []}
              loading={loading}
              renderItem={(item: any) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Avatar icon={<FileTextOutlined />} style={{ backgroundColor: '#52c41a' }} />}
                    title={<Text strong>{item.title}</Text>}
                    description={
                      <Space direction="vertical" size={0}>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          操作人: {item.operator}
                        </Text>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          {formatDateTime(item.time)}
                        </Text>
                      </Space>
                    }
                  />
                  <Tag color={item.status === '成功' ? 'success' : 'error'}>{item.status}</Tag>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
