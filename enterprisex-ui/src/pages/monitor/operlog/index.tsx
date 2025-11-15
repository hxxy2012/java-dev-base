import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  Select,
  message,
  Popconfirm,
  Tag,
  Descriptions,
} from 'antd';
import {
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  ClearOutlined,
} from '@ant-design/icons';
import {
  getOperLogList,
  getOperLog,
  deleteOperLog,
  cleanOperLog,
  OperLog,
  OperLogQuery,
} from '@/api/monitor/log';

const OperLogManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<OperLog[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<OperLogQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentLog, setCurrentLog] = useState<OperLog | null>(null);
  const [searchForm] = Form.useForm();

  // 加载操作日志列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getOperLogList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载操作日志列表失败', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [queryParams]);

  // 搜索
  const handleSearch = (values: any) => {
    setQueryParams({ ...queryParams, ...values, pageNum: 1 });
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    setQueryParams({ pageNum: 1, pageSize: 10 });
  };

  // 查看详情
  const handleViewDetail = async (record: OperLog) => {
    try {
      const res = await getOperLog(record.operId!);
      if (res.code === 200) {
        setCurrentLog(res.data);
        setDetailVisible(true);
      }
    } catch (error) {
      console.error('获取操作日志详情失败', error);
    }
  };

  // 删除
  const handleDelete = async (operId: number) => {
    try {
      await deleteOperLog([operId]);
      message.success('删除成功');
      loadData();
    } catch (error) {
      console.error('删除失败', error);
    }
  };

  // 批量删除
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的操作日志');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 条操作日志吗？`,
      onOk: async () => {
        try {
          await deleteOperLog(selectedRowKeys as number[]);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          loadData();
        } catch (error) {
          console.error('批量删除失败', error);
        }
      },
    });
  };

  // 清空日志
  const handleClean = () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定清空所有操作日志吗？此操作不可恢复！',
      okType: 'danger',
      onOk: async () => {
        try {
          await cleanOperLog();
          message.success('清空成功');
          loadData();
        } catch (error) {
          console.error('清空失败', error);
        }
      },
    });
  };

  // 业务类型映射
  const businessTypeMap: { [key: number]: { text: string; color: string } } = {
    0: { text: '其它', color: 'default' },
    1: { text: '新增', color: 'green' },
    2: { text: '修改', color: 'blue' },
    3: { text: '删除', color: 'red' },
    4: { text: '授权', color: 'purple' },
    5: { text: '导出', color: 'orange' },
    6: { text: '导入', color: 'cyan' },
    7: { text: '强退', color: 'magenta' },
    8: { text: '清空', color: 'volcano' },
  };

  // 表格列定义
  const columns = [
    {
      title: '日志ID',
      dataIndex: 'operId',
      width: 80,
    },
    {
      title: '模块标题',
      dataIndex: 'title',
      width: 120,
    },
    {
      title: '业务类型',
      dataIndex: 'businessType',
      width: 100,
      render: (type: number) => {
        const typeInfo = businessTypeMap[type] || { text: '未知', color: 'default' };
        return <Tag color={typeInfo.color}>{typeInfo.text}</Tag>;
      },
    },
    {
      title: '请求方式',
      dataIndex: 'requestMethod',
      width: 100,
    },
    {
      title: '操作人员',
      dataIndex: 'operName',
      width: 120,
    },
    {
      title: '主机地址',
      dataIndex: 'operIp',
      width: 150,
    },
    {
      title: '操作地点',
      dataIndex: 'operLocation',
      width: 150,
      ellipsis: true,
    },
    {
      title: '操作状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'success' : 'error'}>{status === 1 ? '成功' : '失败'}</Tag>
      ),
    },
    {
      title: '消耗时间',
      dataIndex: 'costTime',
      width: 120,
      render: (time: number) => `${time}ms`,
    },
    {
      title: '操作时间',
      dataIndex: 'operTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right' as const,
      render: (_: any, record: OperLog) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            详情
          </Button>
          <Popconfirm
            title="确定删除该操作日志吗？"
            onConfirm={() => handleDelete(record.operId!)}
          >
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      {/* 搜索栏 */}
      <Form form={searchForm} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="title" label="模块标题">
          <Input placeholder="请输入模块标题" allowClear />
        </Form.Item>
        <Form.Item name="operName" label="操作人员">
          <Input placeholder="请输入操作人员" allowClear />
        </Form.Item>
        <Form.Item name="businessType" label="业务类型">
          <Select placeholder="请选择业务类型" allowClear style={{ width: 120 }}>
            <Select.Option value={0}>其它</Select.Option>
            <Select.Option value={1}>新增</Select.Option>
            <Select.Option value={2}>修改</Select.Option>
            <Select.Option value={3}>删除</Select.Option>
            <Select.Option value={4}>授权</Select.Option>
            <Select.Option value={5}>导出</Select.Option>
            <Select.Option value={6}>导入</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="status" label="状态">
          <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
            <Select.Option value={1}>成功</Select.Option>
            <Select.Option value={0}>失败</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" icon={<SearchOutlined />} htmlType="submit">
              搜索
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      {/* 工具栏 */}
      <Space style={{ marginBottom: 16 }}>
        <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
          批量删除
        </Button>
        <Button danger icon={<ClearOutlined />} onClick={handleClean}>
          清空日志
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="operId"
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          current: queryParams.pageNum,
          pageSize: queryParams.pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setQueryParams({ ...queryParams, pageNum: page, pageSize });
          },
        }}
        scroll={{ x: 1600 }}
      />

      {/* 详情弹窗 */}
      <Modal
        title="操作日志详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {currentLog && (
          <Descriptions column={2} bordered>
            <Descriptions.Item label="日志ID">{currentLog.operId}</Descriptions.Item>
            <Descriptions.Item label="模块标题">{currentLog.title}</Descriptions.Item>
            <Descriptions.Item label="业务类型">
              <Tag color={businessTypeMap[currentLog.businessType || 0]?.color}>
                {businessTypeMap[currentLog.businessType || 0]?.text}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="请求方式">{currentLog.requestMethod}</Descriptions.Item>
            <Descriptions.Item label="操作人员">{currentLog.operName}</Descriptions.Item>
            <Descriptions.Item label="部门名称">{currentLog.deptName || '-'}</Descriptions.Item>
            <Descriptions.Item label="主机地址">{currentLog.operIp}</Descriptions.Item>
            <Descriptions.Item label="操作地点">{currentLog.operLocation || '-'}</Descriptions.Item>
            <Descriptions.Item label="操作状态">
              <Tag color={currentLog.status === 1 ? 'success' : 'error'}>
                {currentLog.status === 1 ? '成功' : '失败'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="消耗时间">{currentLog.costTime}ms</Descriptions.Item>
            <Descriptions.Item label="操作时间" span={2}>
              {currentLog.operTime}
            </Descriptions.Item>
            <Descriptions.Item label="请求URL" span={2}>
              {currentLog.operUrl}
            </Descriptions.Item>
            <Descriptions.Item label="方法名称" span={2}>
              {currentLog.method}
            </Descriptions.Item>
            <Descriptions.Item label="请求参数" span={2}>
              <pre style={{ maxHeight: '200px', overflow: 'auto' }}>
                {currentLog.operParam || '-'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="返回参数" span={2}>
              <pre style={{ maxHeight: '200px', overflow: 'auto' }}>
                {currentLog.jsonResult || '-'}
              </pre>
            </Descriptions.Item>
            {currentLog.errorMsg && (
              <Descriptions.Item label="错误消息" span={2}>
                <pre style={{ maxHeight: '200px', overflow: 'auto', color: 'red' }}>
                  {currentLog.errorMsg}
                </pre>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default OperLogManage;
