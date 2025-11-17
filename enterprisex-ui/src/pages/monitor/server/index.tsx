import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Descriptions,
  Progress,
  Spin,
  Button,
  message,
  Statistic,
  Space,
} from 'antd';
import { ReloadOutlined, DatabaseOutlined, CloudServerOutlined } from '@ant-design/icons';
import { getServerInfo } from '@/api/monitor/server';

const ServerMonitor: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [serverData, setServerData] = useState<any>(null);

  const fetchServerInfo = async () => {
    setLoading(true);
    try {
      const res = await getServerInfo();
      setServerData(res.data);
    } catch (error) {
      message.error('获取服务器信息失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServerInfo();
  }, []);

  if (loading || !serverData) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  const { cpu, mem, jvm, sys, sysFiles } = serverData;

  return (
    <div>
      <Card
        title="服务器监控"
        extra={
          <Button icon={<ReloadOutlined />} onClick={fetchServerInfo}>
            刷新
          </Button>
        }
      >
        {/* CPU信息 */}
        <Card type="inner" title="CPU信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={8}>
              <Statistic title="核心数" value={cpu.cpuNum} suffix="核" />
            </Col>
            <Col span={8}>
              <Statistic
                title="用户使用率"
                value={cpu.used ? cpu.used.toFixed(2) : 0}
                suffix="%"
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="系统使用率"
                value={cpu.sys ? cpu.sys.toFixed(2) : 0}
                suffix="%"
              />
            </Col>
          </Row>
        </Card>

        {/* 内存信息 */}
        <Card type="inner" title="内存信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={8}>
              <Statistic
                title="总内存"
                value={mem.total ? mem.total.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="已用内存"
                value={mem.used ? mem.used.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="剩余内存"
                value={mem.free ? mem.free.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
          </Row>
          <div style={{ marginTop: 16 }}>
            <Progress
              percent={mem.usage ? Number(mem.usage.toFixed(2)) : 0}
              status={mem.usage > 80 ? 'exception' : 'active'}
            />
          </div>
        </Card>

        {/* JVM信息 */}
        <Card type="inner" title="JVM信息" style={{ marginBottom: 16 }}>
          <Row gutter={16} style={{ marginBottom: 16 }}>
            <Col span={8}>
              <Statistic
                title="当前JVM内存"
                value={jvm.total ? jvm.total.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="JVM最大内存"
                value={jvm.max ? jvm.max.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="JVM空闲内存"
                value={jvm.free ? jvm.free.toFixed(2) : 0}
                suffix="MB"
              />
            </Col>
          </Row>
          <div style={{ marginBottom: 16 }}>
            <Progress
              percent={jvm.usage ? Number(jvm.usage.toFixed(2)) : 0}
              status={jvm.usage > 80 ? 'exception' : 'active'}
            />
          </div>
          <Descriptions column={2} bordered size="small">
            <Descriptions.Item label="Java版本">{jvm.version}</Descriptions.Item>
            <Descriptions.Item label="Java路径">{jvm.home}</Descriptions.Item>
            <Descriptions.Item label="启动时间">{jvm.startTime}</Descriptions.Item>
            <Descriptions.Item label="运行时长">{jvm.runTime}</Descriptions.Item>
          </Descriptions>
        </Card>

        {/* 服务器信息 */}
        <Card type="inner" title="服务器信息" style={{ marginBottom: 16 }}>
          <Descriptions column={2} bordered size="small">
            <Descriptions.Item label="服务器名称">{sys.computerName}</Descriptions.Item>
            <Descriptions.Item label="服务器IP">{sys.computerIp}</Descriptions.Item>
            <Descriptions.Item label="操作系统">{sys.osName}</Descriptions.Item>
            <Descriptions.Item label="系统架构">{sys.osArch}</Descriptions.Item>
            <Descriptions.Item label="项目路径" span={2}>
              {sys.userDir}
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* 磁盘信息 */}
        <Card type="inner" title="磁盘信息">
          {sysFiles && sysFiles.length > 0 ? (
            <Row gutter={16}>
              {sysFiles.map((disk: any, index: number) => (
                <Col span={12} key={index} style={{ marginBottom: 16 }}>
                  <Card
                    size="small"
                    title={
                      <Space>
                        <DatabaseOutlined />
                        {disk.dirName}
                      </Space>
                    }
                  >
                    <Descriptions column={1} size="small">
                      <Descriptions.Item label="文件系统">{disk.sysTypeName}</Descriptions.Item>
                      <Descriptions.Item label="盘符类型">{disk.typeName}</Descriptions.Item>
                      <Descriptions.Item label="总大小">{disk.total}</Descriptions.Item>
                      <Descriptions.Item label="可用大小">{disk.free}</Descriptions.Item>
                      <Descriptions.Item label="已用大小">{disk.used}</Descriptions.Item>
                    </Descriptions>
                    <div style={{ marginTop: 8 }}>
                      <Progress
                        percent={disk.usage ? Number(disk.usage.toFixed(2)) : 0}
                        status={disk.usage > 80 ? 'exception' : 'active'}
                      />
                    </div>
                  </Card>
                </Col>
              ))}
            </Row>
          ) : (
            <div style={{ textAlign: 'center', padding: '20px' }}>暂无磁盘信息</div>
          )}
        </Card>
      </Card>
    </div>
  );
};

export default ServerMonitor;
