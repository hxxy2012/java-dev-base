import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  InputNumber,
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
  ArrowLeftOutlined,
} from '@ant-design/icons';
import {
  getDictDataList,
  addDictData,
  updateDictData,
  deleteDictData,
  DictData,
  DictDataQuery,
} from '@/api/system/dict';
import { useNavigate, useSearchParams } from 'react-router-dom';

const DictDataManage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const dictType = searchParams.get('dictType') || '';

  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<DictData[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<DictDataQuery>({
    dictType: dictType,
    pageNum: 1,
    pageSize: 10,
  });
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载字典数据列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getDictDataList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载字典数据列表失败', error);
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
    setQueryParams({ dictType: dictType, pageNum: 1, pageSize: 10 });
  };

  // 新增/编辑提交
  const handleSubmit = async (values: DictData) => {
    try {
      // 设置字典类型
      values.dictType = dictType;

      if (values.dictCode) {
        await updateDictData(values);
        message.success('修改成功');
      } else {
        await addDictData(values);
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
    setVisible(true);
  };

  // 编辑
  const handleEdit = (record: DictData) => {
    form.setFieldsValue(record);
    setVisible(true);
  };

  // 删除
  const handleDelete = async (dictCode: number) => {
    try {
      await deleteDictData([dictCode]);
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
      message.warning('请选择要删除的字典数据');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 个字典数据吗？`,
      onOk: async () => {
        try {
          await deleteDictData(selectedRowKeys as number[]);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          loadData();
        } catch (error) {
          console.error('批量删除失败', error);
        }
      },
    });
  };

  // 返回
  const handleBack = () => {
    navigate('/system/dict/type');
  };

  // 表格列定义
  const columns = [
    {
      title: '字典编码',
      dataIndex: 'dictCode',
      width: 100,
    },
    {
      title: '字典标签',
      dataIndex: 'dictLabel',
      width: 150,
    },
    {
      title: '字典键值',
      dataIndex: 'dictValue',
      width: 150,
    },
    {
      title: '字典排序',
      dataIndex: 'dictSort',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'success' : 'error'}>{status === 1 ? '正常' : '禁用'}</Tag>
      ),
    },
    {
      title: '是否默认',
      dataIndex: 'isDefault',
      width: 100,
      render: (isDefault: number) => (
        <Tag color={isDefault === 1 ? 'blue' : 'default'}>{isDefault === 1 ? '是' : '否'}</Tag>
      ),
    },
    {
      title: '样式属性',
      dataIndex: 'cssClass',
      width: 120,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right' as const,
      render: (_: any, record: DictData) => (
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
            title="确定删除该字典数据吗？"
            onConfirm={() => handleDelete(record.dictCode!)}
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
      {/* 页面头部 */}
      <div style={{ marginBottom: 16, padding: '16px 0', display: 'flex', alignItems: 'center', gap: '12px' }}>
        <Button icon={<ArrowLeftOutlined />} onClick={handleBack} />
        <h2 style={{ margin: 0 }}>{`字典数据管理 (${dictType})`}</h2>
      </div>

      {/* 搜索栏 */}
      <Form form={searchForm} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="dictLabel" label="字典标签">
          <Input placeholder="请输入字典标签" allowClear />
        </Form.Item>
        <Form.Item name="status" label="状态">
          <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
            <Select.Option value={1}>正常</Select.Option>
            <Select.Option value={0}>禁用</Select.Option>
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
          新增字典数据
        </Button>
        <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
          批量删除
        </Button>
        <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>
          返回
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="dictCode"
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
        scroll={{ x: 1300 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('dictCode') ? '编辑字典数据' : '新增字典数据'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item name="dictCode" hidden>
            <Input />
          </Form.Item>
          <Form.Item label="字典类型">
            <Input value={dictType} disabled />
          </Form.Item>
          <Form.Item
            name="dictLabel"
            label="字典标签"
            rules={[{ required: true, message: '请输入字典标签' }]}
          >
            <Input placeholder="请输入字典标签" />
          </Form.Item>
          <Form.Item
            name="dictValue"
            label="字典键值"
            rules={[{ required: true, message: '请输入字典键值' }]}
          >
            <Input placeholder="请输入字典键值" />
          </Form.Item>
          <Form.Item
            name="dictSort"
            label="字典排序"
            rules={[{ required: true, message: '请输入字典排序' }]}
            initialValue={0}
          >
            <InputNumber min={0} placeholder="请输入字典排序" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="cssClass" label="样式属性">
            <Input placeholder="请输入CSS类名（如：primary, success, danger）" />
          </Form.Item>
          <Form.Item name="listClass" label="表格回显样式">
            <Select placeholder="请选择表格回显样式" allowClear>
              <Select.Option value="default">默认</Select.Option>
              <Select.Option value="primary">主要</Select.Option>
              <Select.Option value="success">成功</Select.Option>
              <Select.Option value="info">信息</Select.Option>
              <Select.Option value="warning">警告</Select.Option>
              <Select.Option value="danger">危险</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="isDefault" label="是否默认" initialValue={0}>
            <Select>
              <Select.Option value={1}>是</Select.Option>
              <Select.Option value={0}>否</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
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

export default DictDataManage;
