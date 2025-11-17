import React, { useEffect, useState } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  Table,
  Button,
  Space,
  Modal,
  message,
  Descriptions,
  Tag,
} from 'antd';
import {
  ReloadOutlined,
  DeleteOutlined,
  DatabaseOutlined,
  KeyOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  getCacheInfo,
  getCacheNames,
  getCacheKeys,
  getCacheValue,
  clearCacheByPrefix,
  clearAllCache,
  RedisInfo,
  CacheInfo,
} from '@/api/monitor/cache';

interface CacheItem {
  cacheName: string;
  count: number;
}

const CacheManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [redisInfo, setRedisInfo] = useState<RedisInfo | null>(null);
  const [cacheList, setCacheList] = useState<CacheItem[]>([]);
  const [selectedCache, setSelectedCache] = useState<string>('');
  const [cacheKeys, setCacheKeys] = useState<string[]>([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [cacheDetail, setCacheDetail] = useState<CacheInfo | null>(null);

  // 获取Redis信息
  const fetchRedisInfo = async () => {
    setLoading(true);
    try {
      const response = await getCacheInfo();
      if (response.code === 200) {
        setRedisInfo(response.data);

        // 转换缓存统计为列表
        const list: CacheItem[] = Object.entries(response.data.cacheStats || {}).map(
          ([name, count]) => ({
            cacheName: name,
            count: count as number,
          })
        );
        setCacheList(list);
      }
    } catch (error) {
      console.error('获取Redis信息失败：', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRedisInfo();
  }, []);

  // 查看缓存键
  const handleViewKeys = async (cacheName: string) => {
    setSelectedCache(cacheName);
    try {
      const response = await getCacheKeys(cacheName);
      if (response.code === 200) {
        setCacheKeys(response.data || []);
      }
    } catch (error) {
      console.error('获取缓存键失败：', error);
    }
  };

  // 查看缓存值
  const handleViewValue = async (cacheKey: string) => {
    try {
      const response = await getCacheValue(cacheKey);
      if (response.code === 200) {
        setCacheDetail(response.data);
        setDetailVisible(true);
      }
    } catch (error) {
      console.error('获取缓存值失败：', error);
    }
  };

  // 清除缓存
  const handleClearCache = (cacheName: string) => {
    Modal.confirm({
      title: '确认清除',
      content: `确定要清除"${cacheName}"的所有缓存吗？`,
      onOk: async () => {
        try {
          const response = await clearCacheByPrefix(cacheName);
          if (response.code === 200) {
            message.success('清除成功');
            fetchRedisInfo();
            setCacheKeys([]);
            setSelectedCache('');
          }
        } catch (error) {
          console.error('清除缓存失败：', error);
        }
      },
    });
  };

  // 清空所有缓存
  const handleClearAll = () => {
    Modal.confirm({
      title: '危险操作',
      content: '确定要清空所有缓存吗？此操作不可恢复！',
      okType: 'danger',
      onOk: async () => {
        try {
          const response = await clearAllCache();
          if (response.code === 200) {
            message.success('清空成功');
            fetchRedisInfo();
            setCacheKeys([]);
            setSelectedCache('');
          }
        } catch (error) {
          console.error('清空缓存失败：', error);
        }
      },
    });
  };

  const columns: ColumnsType<CacheItem> = [
    {
      title: '缓存名称',
      dataIndex: 'cacheName',
      key: 'cacheName',
    },
    {
      title: '缓存数量',
      dataIndex: 'count',
      key: 'count',
      render: (count: number) => <Tag color="blue">{count}</Tag>,
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<KeyOutlined />}
            onClick={() => handleViewKeys(record.cacheName)}
          >
            查看键
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleClearCache(record.cacheName)}
          >
            清除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      {/* Redis统计信息 */}
      <Row gutter={16}>
        <Col span={12}>
          <Card>
            <Statistic
              title="缓存总数"
              value={redisInfo?.dbSize || 0}
              prefix={<DatabaseOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card>
            <Statistic
              title="缓存类型"
              value={cacheList.length}
              prefix={<KeyOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 缓存列表 */}
      <Card style={{ marginTop: 16 }} title="缓存列表">
        <Space style={{ marginBottom: 16 }}>
          <Button
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={handleClearAll}
          >
            清空所有
          </Button>
          <Button icon={<ReloadOutlined />} onClick={fetchRedisInfo}>
            刷新
          </Button>
        </Space>

        <Table
          rowKey="cacheName"
          columns={columns}
          dataSource={cacheList}
          loading={loading}
          pagination={false}
        />
      </Card>

      {/* 缓存键列表 */}
      {selectedCache && (
        <Card style={{ marginTop: 16 }} title={`缓存键 - ${selectedCache}`}>
          <Table
            rowKey={(record) => record}
            columns={[
              {
                title: '键名',
                dataIndex: 'key',
                key: 'key',
                render: (_, key: string) => key,
              },
              {
                title: '操作',
                key: 'action',
                render: (_, key: string) => (
                  <Button
                    type="link"
                    size="small"
                    icon={<EyeOutlined />}
                    onClick={() => handleViewValue(key)}
                  >
                    查看
                  </Button>
                ),
              },
            ]}
            dataSource={cacheKeys}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条`,
            }}
          />
        </Card>
      )}

      {/* 缓存详情弹窗 */}
      <Modal
        title="缓存详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        {cacheDetail && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="键名">
              {cacheDetail.cacheKey}
            </Descriptions.Item>
            <Descriptions.Item label="过期时间（秒）">
              {cacheDetail.expireTime === -1
                ? '永久'
                : cacheDetail.expireTime}
            </Descriptions.Item>
            <Descriptions.Item label="值">
              <pre style={{ maxHeight: 400, overflow: 'auto' }}>
                {cacheDetail.cacheValue}
              </pre>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default CacheManage;
