import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  Select,
  Tag,
  Switch,
  message,
  Tabs,
  Statistic,
  Row,
  Col,
  Popconfirm,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyOutlined,
  BarChartOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  StopOutlined,
} from '@ant-design/icons';
import {
  getIpRules,
  addIpRule,
  updateIpRule,
  deleteIpRules,
  getAccessStats,
  clearAccessStats,
  type IpRule,
  type AccessStat,
} from '@/api/system/ipaccess';
import { formatDateTime } from '@/utils/date';

const { TabPane } = Tabs;

const IpAccessPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [whiteList, setWhiteList] = useState<IpRule[]>([]);
  const [blackList, setBlackList] = useState<IpRule[]>([]);
  const [accessStats, setAccessStats] = useState<AccessStat[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRule, setEditingRule] = useState<IpRule | null>(null);
  const [form] = Form.useForm();

  // 加载规则列表
  const loadRules = async () => {
    setLoading(true);
    try {
      const [whiteResponse, blackResponse] = await Promise.all([
        getIpRules(1),
        getIpRules(2),
      ]);
      setWhiteList(whiteResponse.data || []);
      setBlackList(blackResponse.data || []);
    } catch (error) {
      message.error('加载规则列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 加载访问统计
  const loadAccessStats = async () => {
    try {
      const response = await getAccessStats();
      setAccessStats(response.data || []);
    } catch (error) {
      message.error('加载统计数据失败');
    }
  };

  useEffect(() => {
    loadRules();
    loadAccessStats();
  }, []);

  // 添加/编辑规则
  const handleSubmit = async (values: any) => {
    try {
      if (editingRule) {
        await updateIpRule({ ...editingRule, ...values });
        message.success('更新成功');
      } else {
        await addIpRule(values);
        message.success('添加成功');
      }
      setModalVisible(false);
      form.resetFields();
      setEditingRule(null);
      loadRules();
    } catch (error) {
      message.error('操作失败');
    }
  };

  // 打开编辑弹窗
  const handleEdit = (record: IpRule) => {
    setEditingRule(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  // 删除规则
  const handleDelete = async (id: string) => {
    try {
      await deleteIpRules([id]);
      message.success('删除成功');
      loadRules();
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 清空统计
  const handleClearStats = async () => {
    try {
      await clearAccessStats();
      message.success('统计数据已清空');
      loadAccessStats();
    } catch (error) {
      message.error('操作失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<IpRule> = [
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      width: 200,
      render: (text) => <span style={{ fontFamily: 'monospace' }}>{text}</span>,
    },
    {
      title: '规则类型',
      dataIndex: 'ruleType',
      key: 'ruleType',
      width: 120,
      render: (type: number) =>
        type === 1 ? (
          <Tag color="green" icon={<CheckCircleOutlined />}>
            白名单
          </Tag>
        ) : (
          <Tag color="red" icon={<StopOutlined />}>
            黑名单
          </Tag>
        ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'success' : 'default'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (text) => formatDateTime(text),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除此规则吗？"
            onConfirm={() => handleDelete(record.id!)}
          >
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 统计表格列定义
  const statColumns: ColumnsType<AccessStat> = [
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      render: (text) => <span style={{ fontFamily: 'monospace' }}>{text}</span>,
    },
    {
      title: '访问次数',
      dataIndex: 'accessCount',
      key: 'accessCount',
      sorter: (a, b) => a.accessCount - b.accessCount,
    },
    {
      title: '拦截次数',
      dataIndex: 'blockedCount',
      key: 'blockedCount',
      sorter: (a, b) => a.blockedCount - b.blockedCount,
      render: (count: number) => (
        <Tag color={count > 0 ? 'red' : 'default'}>{count}</Tag>
      ),
    },
    {
      title: '最后访问时间',
      dataIndex: 'lastAccessTime',
      key: 'lastAccessTime',
      render: (text) => formatDateTime(text),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Tabs defaultActiveKey="rules">
        <TabPane
          tab={
            <span>
              <SafetyOutlined />
              访问规则
            </span>
          }
          key="rules"
        >
          <Card
            title="IP访问控制"
            extra={
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => {
                  setEditingRule(null);
                  form.resetFields();
                  setModalVisible(true);
                }}
              >
                添加规则
              </Button>
            }
          >
            <Tabs>
              <TabPane tab={`白名单 (${whiteList.length})`} key="whitelist">
                <Table
                  columns={columns}
                  dataSource={whiteList}
                  rowKey="id"
                  loading={loading}
                  pagination={{ pageSize: 10 }}
                />
              </TabPane>
              <TabPane tab={`黑名单 (${blackList.length})`} key="blacklist">
                <Table
                  columns={columns}
                  dataSource={blackList}
                  rowKey="id"
                  loading={loading}
                  pagination={{ pageSize: 10 }}
                />
              </TabPane>
            </Tabs>
          </Card>
        </TabPane>

        <TabPane
          tab={
            <span>
              <BarChartOutlined />
              访问统计
            </span>
          }
          key="stats"
        >
          <Card
            title="访问统计"
            extra={
              <Space>
                <Button icon={<ReloadOutlined />} onClick={loadAccessStats}>
                  刷新
                </Button>
                <Popconfirm
                  title="确定清空所有统计数据吗？"
                  onConfirm={handleClearStats}
                >
                  <Button danger icon={<DeleteOutlined />}>
                    清空统计
                  </Button>
                </Popconfirm>
              </Space>
            }
          >
            <Row gutter={16} style={{ marginBottom: 24 }}>
              <Col span={8}>
                <Card>
                  <Statistic
                    title="总访问IP数"
                    value={accessStats.length}
                    prefix={<SafetyOutlined />}
                  />
                </Card>
              </Col>
              <Col span={8}>
                <Card>
                  <Statistic
                    title="总访问次数"
                    value={accessStats.reduce((sum, s) => sum + s.accessCount, 0)}
                    prefix={<BarChartOutlined />}
                  />
                </Card>
              </Col>
              <Col span={8}>
                <Card>
                  <Statistic
                    title="总拦截次数"
                    value={accessStats.reduce((sum, s) => sum + s.blockedCount, 0)}
                    valueStyle={{ color: '#cf1322' }}
                    prefix={<StopOutlined />}
                  />
                </Card>
              </Col>
            </Row>

            <Table
              columns={statColumns}
              dataSource={accessStats}
              rowKey="ipAddress"
              pagination={{ pageSize: 10 }}
            />
          </Card>
        </TabPane>
      </Tabs>

      {/* 添加/编辑规则弹窗 */}
      <Modal
        title={editingRule ? '编辑规则' : '添加规则'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
          setEditingRule(null);
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item
            name="ipAddress"
            label="IP地址"
            rules={[
              { required: true, message: '请输入IP地址' },
              {
                pattern: /^(\d{1,3}|\*)\.(\d{1,3}|\*)\.(\d{1,3}|\*)\.(\d{1,3}|\*)$/,
                message: 'IP地址格式不正确（支持*通配符）',
              },
            ]}
          >
            <Input placeholder="例如: 192.168.1.100 或 192.168.1.*" />
          </Form.Item>

          <Form.Item
            name="ruleType"
            label="规则类型"
            rules={[{ required: true, message: '请选择规则类型' }]}
            initialValue={1}
          >
            <Select>
              <Select.Option value={1}>白名单</Select.Option>
              <Select.Option value={2}>黑名单</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="status" label="状态" initialValue={1} valuePropName="checked">
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>

          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="请输入规则描述" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default IpAccessPage;
