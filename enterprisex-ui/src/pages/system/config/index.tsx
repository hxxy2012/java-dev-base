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
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import {
  getConfigList,
  addConfig,
  updateConfig,
  deleteConfig,
  Config,
  ConfigQuery,
} from '@/api/system/config';

const ConfigManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Config[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<ConfigQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载参数配置列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getConfigList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载参数配置列表失败', error);
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

  // 新增/编辑提交
  const handleSubmit = async (values: Config) => {
    try {
      if (values.configId) {
        await updateConfig(values);
        message.success('修改成功');
      } else {
        await addConfig(values);
        message.success('新增成功');
      }
      setVisible(false);
      loadData();
    } catch (error) {
      console.error('操作失败', error);
    }
  };

  // 新增
  const handleAdd = () => {
    form.resetFields();
    form.setFieldsValue({ configType: 0 });
    setVisible(true);
  };

  // 编辑
  const handleEdit = (record: Config) => {
    form.setFieldsValue(record);
    setVisible(true);
  };

  // 删除
  const handleDelete = async (configId: number) => {
    try {
      await deleteConfig([configId]);
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
      message.warning('请选择要删除的参数配置');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 个参数配置吗？`,
      onOk: async () => {
        try {
          await deleteConfig(selectedRowKeys as number[]);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          loadData();
        } catch (error) {
          console.error('批量删除失败', error);
        }
      },
    });
  };

  // 表格列定义
  const columns = [
    {
      title: '参数主键',
      dataIndex: 'configId',
      width: 100,
    },
    {
      title: '参数名称',
      dataIndex: 'configName',
      width: 200,
    },
    {
      title: '参数键名',
      dataIndex: 'configKey',
      width: 200,
    },
    {
      title: '参数键值',
      dataIndex: 'configValue',
      width: 200,
      ellipsis: true,
    },
    {
      title: '系统内置',
      dataIndex: 'configType',
      width: 100,
      render: (configType: number) => (
        <Tag color={configType === 1 ? 'red' : 'default'}>{configType === 1 ? '是' : '否'}</Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 200,
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right' as const,
      render: (_: any, record: Config) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            disabled={record.configType === 1}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除该参数配置吗？"
            onConfirm={() => handleDelete(record.configId!)}
            disabled={record.configType === 1}
          >
            <Button
              type="link"
              danger
              size="small"
              icon={<DeleteOutlined />}
              disabled={record.configType === 1}
            >
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
        <Form.Item name="configName" label="参数名称">
          <Input placeholder="请输入参数名称" allowClear />
        </Form.Item>
        <Form.Item name="configKey" label="参数键名">
          <Input placeholder="请输入参数键名" allowClear />
        </Form.Item>
        <Form.Item name="configType" label="系统内置">
          <Select placeholder="请选择" allowClear style={{ width: 120 }}>
            <Select.Option value={1}>是</Select.Option>
            <Select.Option value={0}>否</Select.Option>
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
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增参数
        </Button>
        <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
          批量删除
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="configId"
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
          getCheckboxProps: (record) => ({
            disabled: record.configType === 1,
          }),
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
        scroll={{ x: 1400 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('configId') ? '编辑参数配置' : '新增参数配置'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item name="configId" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            name="configName"
            label="参数名称"
            rules={[{ required: true, message: '请输入参数名称' }]}
          >
            <Input placeholder="请输入参数名称" />
          </Form.Item>
          <Form.Item
            name="configKey"
            label="参数键名"
            rules={[{ required: true, message: '请输入参数键名' }]}
          >
            <Input placeholder="请输入参数键名（如：sys.user.initPassword）" />
          </Form.Item>
          <Form.Item
            name="configValue"
            label="参数键值"
            rules={[{ required: true, message: '请输入参数键值' }]}
          >
            <Input placeholder="请输入参数键值" />
          </Form.Item>
          <Form.Item name="configType" label="系统内置" initialValue={0}>
            <Select disabled={form.getFieldValue('configId')}>
              <Select.Option value={1}>是</Select.Option>
              <Select.Option value={0}>否</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ConfigManage;
