import React, { useState, useEffect } from 'react';
import {
  Card,
  Space,
  Button,
  Row,
  Col,
  Tag,
  Progress,
  Statistic,
  Alert,
} from 'antd';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  WarningOutlined,
  ReloadOutlined,
  DashboardOutlined,
} from '@ant-design/icons';
import { healthCheck } from '@/api/tool/health';

const HealthCheckPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [healthData, setHealthData] = useState<any>(null);
  const [autoRefresh, setAutoRefresh] = useState(false);

  useEffect(() => {
    fetchHealth();

    // 自动刷新
    let interval: NodeJS.Timeout;
    if (autoRefresh) {
      interval = setInterval(() => {
        fetchHealth();
      }, 30000); // 30秒刷新一次
    }

    return () => {
      if (interval) {
        clearInterval(interval);
      }
    };
  }, [autoRefresh]);

  const fetchHealth = async () => {
    setLoading(true);
    try {
      const res = await healthCheck();
      setHealthData(res.data);
    } catch (error) {
      console.error('健康检查失败', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'UP':
        return <CheckCircleOutlined style={{ color: '#52c41a', fontSize: 24 }} />;
      case 'WARNING':
        return <WarningOutlined style={{ color: '#faad14', fontSize: 24 }} />;
      case 'DOWN':
        return <CloseCircleOutlined style={{ color: '#f5222d', fontSize: 24 }} />;
      default:
        return null;
    }
  };

  const getStatusTag = (status: string) => {
    switch (status) {
      case 'UP':
        return <Tag color="success">正常</Tag>;
      case 'WARNING':
        return <Tag color="warning">警告</Tag>;
      case 'DOWN':
        return <Tag color="error">异常</Tag>;
      default:
        return <Tag>未知</Tag>;
    }
  };

  const getUsagePercent = (str: string) => {
    const match = str?.match(/([\d.]+)%/);
    return match ? parseFloat(match[1]) : 0;
  };

  const getUsageColor = (percent: number) => {
    if (percent < 70) return '#52c41a';
    if (percent < 85) return '#faad14';
    return '#f5222d';
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <DashboardOutlined />
            <span>系统健康检查</span>
          </Space>
        }
        extra={
          <Space>
            <Button
              type={autoRefresh ? 'primary' : 'default'}
              onClick={() => setAutoRefresh(!autoRefresh)}
            >
              {autoRefresh ? '停止自动刷新' : '自动刷新'}
            </Button>
            <Button icon={<ReloadOutlined />} onClick={fetchHealth} loading={loading}>
              刷新
            </Button>
          </Space>
        }
      >
        {healthData && (
          <>
            <Alert
              message={
                <Space>
                  {getStatusIcon(healthData.status)}
                  <span style={{ fontSize: 18, fontWeight: 'bold' }}>
                    系统整体状态: {getStatusTag(healthData.status)}
                  </span>
                </Space>
              }
              type={healthData.status === 'UP' ? 'success' : 'error'}
              style={{ marginBottom: 24 }}
            />

            <Row gutter={[16, 16]}>
              {/* 数据库健康 */}
              <Col xs={24} sm={12} lg={6}>
                <Card
                  title={
                    <Space>
                      {getStatusIcon(healthData.database?.status)}
                      <span>数据库</span>
                    </Space>
                  }
                  size="small"
                >
                  <Space direction="vertical" style={{ width: '100%' }}>
                    {getStatusTag(healthData.database?.status)}
                    <div>{healthData.database?.message}</div>
                    {healthData.database?.responseTime && (
                      <Statistic
                        title="响应时间"
                        value={healthData.database.responseTime}
                        valueStyle={{ fontSize: 20 }}
                      />
                    )}
                    {healthData.database?.connections && (
                      <Statistic
                        title="连接数"
                        value={healthData.database.connections}
                        valueStyle={{ fontSize: 20 }}
                      />
                    )}
                  </Space>
                </Card>
              </Col>

              {/* Redis健康 */}
              <Col xs={24} sm={12} lg={6}>
                <Card
                  title={
                    <Space>
                      {getStatusIcon(healthData.redis?.status)}
                      <span>Redis</span>
                    </Space>
                  }
                  size="small"
                >
                  <Space direction="vertical" style={{ width: '100%' }}>
                    {getStatusTag(healthData.redis?.status)}
                    <div>{healthData.redis?.message}</div>
                    {healthData.redis?.responseTime && (
                      <Statistic
                        title="响应时间"
                        value={healthData.redis.responseTime}
                        valueStyle={{ fontSize: 20 }}
                      />
                    )}
                  </Space>
                </Card>
              </Col>

              {/* 磁盘空间 */}
              <Col xs={24} sm={12} lg={6}>
                <Card
                  title={
                    <Space>
                      {getStatusIcon(healthData.disk?.status)}
                      <span>磁盘空间</span>
                    </Space>
                  }
                  size="small"
                >
                  <Space direction="vertical" style={{ width: '100%' }}>
                    {getStatusTag(healthData.disk?.status)}
                    <div>{healthData.disk?.message}</div>
                    {healthData.disk?.usagePercent && (
                      <>
                        <Progress
                          percent={getUsagePercent(healthData.disk.usagePercent)}
                          strokeColor={getUsageColor(getUsagePercent(healthData.disk.usagePercent))}
                        />
                        <div>
                          <div>总容量: {healthData.disk.total}</div>
                          <div>已使用: {healthData.disk.used}</div>
                          <div>可用: {healthData.disk.free}</div>
                        </div>
                      </>
                    )}
                  </Space>
                </Card>
              </Col>

              {/* 内存使用 */}
              <Col xs={24} sm={12} lg={6}>
                <Card
                  title={
                    <Space>
                      {getStatusIcon(healthData.memory?.status)}
                      <span>内存使用</span>
                    </Space>
                  }
                  size="small"
                >
                  <Space direction="vertical" style={{ width: '100%' }}>
                    {getStatusTag(healthData.memory?.status)}
                    <div>{healthData.memory?.message}</div>
                    {healthData.memory?.usagePercent && (
                      <>
                        <Progress
                          percent={getUsagePercent(healthData.memory.usagePercent)}
                          strokeColor={getUsageColor(getUsagePercent(healthData.memory.usagePercent))}
                        />
                        <div>
                          <div>最大内存: {healthData.memory.max}</div>
                          <div>总内存: {healthData.memory.total}</div>
                          <div>已使用: {healthData.memory.used}</div>
                          <div>空闲: {healthData.memory.free}</div>
                        </div>
                      </>
                    )}
                  </Space>
                </Card>
              </Col>
            </Row>

            <div style={{ marginTop: 16, textAlign: 'right', color: '#999' }}>
              最后更新时间: {new Date(healthData.timestamp).toLocaleString('zh-CN')}
            </div>
          </>
        )}
      </Card>
    </div>
  );
};

export default HealthCheckPage;
