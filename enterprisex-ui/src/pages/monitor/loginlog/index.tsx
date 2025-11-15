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
} from 'antd';
import {
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  ClearOutlined,
} from '@ant-design/icons';
import {
  getLoginLogList,
  deleteLoginLog,
  cleanLoginLog,
  LoginLog,
  LoginLogQuery,
} from '@/api/monitor/log';

const LoginLogManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<LoginLog[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<LoginLogQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [searchForm] = Form.useForm();

  // 加载登录日志列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getLoginLogList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载登录日志列表失败', error);
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

  // 删除
  const handleDelete = async (infoId: number) => {
    try {
      await deleteLoginLog([infoId]);
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
      message.warning('请选择要删除的登录日志');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 条登录日志吗？`,
      onOk: async () => {
        try {
          await deleteLoginLog(selectedRowKeys as number[]);
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
      content: '确定清空所有登录日志吗？此操作不可恢复！',
      okType: 'danger',
      onOk: async () => {
        try {
          await cleanLoginLog();
          message.success('清空成功');
          loadData();
        } catch (error) {
          console.error('清空失败', error);
        }
      },
    });
  };

  // 表格列定义
  const columns = [
    {
      title: '日志ID',
      dataIndex: 'infoId',
      width: 80,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 150,
    },
    {
      title: '登录IP',
      dataIndex: 'ipaddr',
      width: 150,
    },
    {
      title: '登录地点',
      dataIndex: 'loginLocation',
      width: 200,
      ellipsis: true,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      width: 150,
      ellipsis: true,
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      width: 150,
      ellipsis: true,
    },
    {
      title: '登录状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'success' : 'error'}>{status === 1 ? '成功' : '失败'}</Tag>
      ),
    },
    {
      title: '提示消息',
      dataIndex: 'msg',
      width: 200,
      ellipsis: true,
    },
    {
      title: '访问时间',
      dataIndex: 'loginTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right' as const,
      render: (_: any, record: LoginLog) => (
        <Popconfirm
          title="确定删除该登录日志吗？"
          onConfirm={() => handleDelete(record.infoId!)}
        >
          <Button type="link" danger size="small" icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      {/* 搜索栏 */}
      <Form form={searchForm} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="username" label="用户名">
          <Input placeholder="请输入用户名" allowClear />
        </Form.Item>
        <Form.Item name="ipaddr" label="登录IP">
          <Input placeholder="请输入登录IP" allowClear />
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
        rowKey="infoId"
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
        scroll={{ x: 1500 }}
      />
    </div>
  );
};

export default LoginLogManage;
