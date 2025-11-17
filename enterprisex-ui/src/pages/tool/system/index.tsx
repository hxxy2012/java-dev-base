import React, { useState, useEffect } from 'react';
import {
  Card,
  Descriptions,
  Button,
  Space,
  message,
  Tabs,
  Table,
  Tag,
  Progress,
} from 'antd';
import {
  ReloadOutlined,
  DeleteOutlined,
  InfoCircleOutlined,
  DesktopOutlined,
  ClockCircleOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import type { DescriptionsProps } from 'antd';
import { getSystemInfo, getClientInfo, getSystemTime, getEnv, triggerGC } from '@/api/tool/system';

const { TabPane } = Tabs;

const SystemToolPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [systemInfo, setSystemInfo] = useState<any>(null);
  const [clientInfo, setClientInfo] = useState<any>(null);
  const [timeInfo, setTimeInfo] = useState<any>(null);
  const [envData, setEnvData] = useState<any[]>([]);

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    setLoading(true);
    try {
      await Promise.all([
        fetchSystemInfo(),
        fetchClientInfo(),
        fetchTimeInfo(),
        fetchEnv(),
      ]);
    } finally {
      setLoading(false);
    }
  };

  const fetchSystemInfo = async () => {
    try {
      const res = await getSystemInfo();
      setSystemInfo(res.data);
    } catch (error) {
      message.error('获取系统信息失败');
    }
  };

  const fetchClientInfo = async () => {
    try {
      const res = await getClientInfo();
      setClientInfo(res.data);
    } catch (error) {
      message.error('获取客户端信息失败');
    }
  };

  const fetchTimeInfo = async () => {
    try {
      const res = await getSystemTime();
      setTimeInfo(res.data);
    } catch (error) {
      message.error('获取系统时间失败');
    }
  };

  const fetchEnv = async () => {
    try {
      const res = await getEnv();
      const data = Object.keys(res.data).map((key, index) => ({
        key: index,
        name: key,
        value: res.data[key],
      }));
      setEnvData(data);
    } catch (error) {
      message.error('获取环境变量失败');
    }
  };

  const handleGC = async () => {
    try {
      await triggerGC();
      message.success('垃圾回收已触发');
      setTimeout(fetchSystemInfo, 1000); // 1秒后刷新数据
    } catch (error) {
      message.error('触发垃圾回收失败');
    }
  };

  const systemItems: DescriptionsProps['items'] = systemInfo
    ? [
        {
          key: 'os',
          label: '操作系统',
          children: systemInfo.system?.os,
        },
        {
          key: 'arch',
          label: '系统架构',
          children: systemInfo.system?.arch,
        },
        {
          key: 'version',
          label: '系统版本',
          children: systemInfo.system?.version,
        },
        {
          key: 'hostname',
          label: '主机名',
          children: systemInfo.system?.hostname,
        },
        {
          key: 'ip',
          label: 'IP地址',
          children: systemInfo.system?.ip,
        },
      ]
    : [];

  const jvmItems: DescriptionsProps['items'] = systemInfo
    ? [
        {
          key: 'name',
          label: 'JVM名称',
          children: systemInfo.jvm?.name,
        },
        {
          key: 'version',
          label: 'Java版本',
          children: systemInfo.jvm?.version,
        },
        {
          key: 'vendor',
          label: 'JVM供应商',
          children: systemInfo.jvm?.vendor,
        },
        {
          key: 'totalMemory',
          label: '最大内存',
          children: systemInfo.jvm?.totalMemory,
        },
        {
          key: 'usedMemory',
          label: '已用内存',
          children: (
            <Space>
              {systemInfo.jvm?.usedMemory}
              <Progress
                percent={Math.round(
                  (parseInt(systemInfo.jvm?.usedMemory) / parseInt(systemInfo.jvm?.totalMemory)) * 100
                )}
                size="small"
                style={{ width: 100 }}
              />
            </Space>
          ),
        },
        {
          key: 'freeMemory',
          label: '空闲内存',
          children: systemInfo.jvm?.freeMemory,
        },
      ]
    : [];

  const threadItems: DescriptionsProps['items'] = systemInfo
    ? [
        {
          key: 'total',
          label: '当前线程数',
          children: <Tag color="blue">{systemInfo.thread?.total}</Tag>,
        },
        {
          key: 'daemon',
          label: '守护线程数',
          children: <Tag color="green">{systemInfo.thread?.daemon}</Tag>,
        },
        {
          key: 'peak',
          label: '峰值线程数',
          children: <Tag color="orange">{systemInfo.thread?.peak}</Tag>,
        },
      ]
    : [];

  const runtimeItems: DescriptionsProps['items'] = systemInfo
    ? [
        {
          key: 'uptime',
          label: '运行时长',
          children: systemInfo.runtime?.uptime,
        },
        {
          key: 'startTime',
          label: '启动时间',
          children: new Date(systemInfo.runtime?.startTime).toLocaleString('zh-CN'),
        },
      ]
    : [];

  const clientItems: DescriptionsProps['items'] = clientInfo
    ? [
        {
          key: 'ip',
          label: '客户端IP',
          children: clientInfo.ip,
        },
        {
          key: 'browser',
          label: '浏览器',
          children: clientInfo.browser,
        },
        {
          key: 'os',
          label: '操作系统',
          children: clientInfo.os,
        },
        {
          key: 'userAgent',
          label: 'User-Agent',
          children: <div style={{ wordBreak: 'break-all' }}>{clientInfo.userAgent}</div>,
          span: 3,
        },
      ]
    : [];

  const timeItems: DescriptionsProps['items'] = timeInfo
    ? [
        {
          key: 'date',
          label: '日期',
          children: timeInfo.date,
        },
        {
          key: 'time',
          label: '时间',
          children: timeInfo.time,
        },
        {
          key: 'dateTime',
          label: '日期时间',
          children: timeInfo.dateTime,
        },
        {
          key: 'timestamp',
          label: '时间戳',
          children: timeInfo.timestamp,
        },
        {
          key: 'timezone',
          label: '时区',
          children: timeInfo.timezone,
        },
      ]
    : [];

  const envColumns = [
    {
      title: '属性名',
      dataIndex: 'name',
      key: 'name',
      width: '40%',
    },
    {
      title: '属性值',
      dataIndex: 'value',
      key: 'value',
      ellipsis: true,
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <InfoCircleOutlined />
            <span>系统工具</span>
          </Space>
        }
        extra={
          <Space>
            <Button icon={<DeleteOutlined />} onClick={handleGC}>
              垃圾回收
            </Button>
            <Button icon={<ReloadOutlined />} onClick={fetchAllData} loading={loading}>
              刷新
            </Button>
          </Space>
        }
      >
        <Tabs defaultActiveKey="1">
          <TabPane
            tab={
              <span>
                <DesktopOutlined />
                系统信息
              </span>
            }
            key="1"
          >
            <Card title="操作系统" size="small" style={{ marginBottom: 16 }}>
              <Descriptions bordered items={systemItems} column={2} />
            </Card>

            <Card title="JVM信息" size="small" style={{ marginBottom: 16 }}>
              <Descriptions bordered items={jvmItems} column={2} />
            </Card>

            <Card title="线程信息" size="small" style={{ marginBottom: 16 }}>
              <Descriptions bordered items={threadItems} column={3} />
            </Card>

            <Card title="运行时信息" size="small">
              <Descriptions bordered items={runtimeItems} column={2} />
            </Card>
          </TabPane>

          <TabPane
            tab={
              <span>
                <DesktopOutlined />
                客户端信息
              </span>
            }
            key="2"
          >
            <Descriptions bordered items={clientItems} column={2} />
          </TabPane>

          <TabPane
            tab={
              <span>
                <ClockCircleOutlined />
                系统时间
              </span>
            }
            key="3"
          >
            <Descriptions bordered items={timeItems} column={2} />
          </TabPane>

          <TabPane
            tab={
              <span>
                <SettingOutlined />
                环境变量
              </span>
            }
            key="4"
          >
            <Table
              columns={envColumns}
              dataSource={envData}
              pagination={{
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条`,
              }}
              size="small"
            />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};

export default SystemToolPage;
