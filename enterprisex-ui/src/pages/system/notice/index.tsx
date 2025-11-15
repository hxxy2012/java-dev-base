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
  EyeOutlined,
} from '@ant-design/icons';
import {
  getNoticeList,
  addNotice,
  updateNotice,
  deleteNotice,
  Notice,
  NoticeQuery,
} from '@/api/system/notice';

const NoticeManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Notice[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<NoticeQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [visible, setVisible] = useState(false);
  const [viewVisible, setViewVisible] = useState(false);
  const [currentNotice, setCurrentNotice] = useState<Notice | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载通知公告列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getNoticeList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载通知公告列表失败', error);
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
  const handleSubmit = async (values: Notice) => {
    try {
      if (values.noticeId) {
        await updateNotice(values);
        message.success('修改成功');
      } else {
        await addNotice(values);
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
    form.setFieldsValue({ noticeType: 1, status: 0 });
    setVisible(true);
  };

  // 编辑
  const handleEdit = (record: Notice) => {
    form.setFieldsValue(record);
    setVisible(true);
  };

  // 查看详情
  const handleView = (record: Notice) => {
    setCurrentNotice(record);
    setViewVisible(true);
  };

  // 删除
  const handleDelete = async (noticeId: number) => {
    try {
      await deleteNotice([noticeId]);
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
      message.warning('请选择要删除的通知公告');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 条通知公告吗？`,
      onOk: async () => {
        try {
          await deleteNotice(selectedRowKeys as number[]);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          loadData();
        } catch (error) {
          console.error('批量删除失败', error);
        }
      },
    });
  };

  // 公告类型映射
  const noticeTypeMap: { [key: number]: { text: string; color: string } } = {
    1: { text: '通知', color: 'blue' },
    2: { text: '公告', color: 'green' },
  };

  // 表格列定义
  const columns = [
    {
      title: '公告ID',
      dataIndex: 'noticeId',
      width: 80,
    },
    {
      title: '公告标题',
      dataIndex: 'noticeTitle',
      width: 250,
      ellipsis: true,
    },
    {
      title: '公告类型',
      dataIndex: 'noticeType',
      width: 100,
      render: (noticeType: number) => (
        <Tag color={noticeTypeMap[noticeType]?.color}>{noticeTypeMap[noticeType]?.text}</Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 0 ? 'success' : 'error'}>{status === 0 ? '正常' : '关闭'}</Tag>
      ),
    },
    {
      title: '创建者',
      dataIndex: 'createBy',
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
      width: 280,
      fixed: 'right' as const,
      render: (_: any, record: Notice) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            查看
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除该通知公告吗？"
            onConfirm={() => handleDelete(record.noticeId!)}
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
        <Form.Item name="noticeTitle" label="公告标题">
          <Input placeholder="请输入公告标题" allowClear />
        </Form.Item>
        <Form.Item name="noticeType" label="公告类型">
          <Select placeholder="请选择公告类型" allowClear style={{ width: 120 }}>
            <Select.Option value={1}>通知</Select.Option>
            <Select.Option value={2}>公告</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="status" label="状态">
          <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
            <Select.Option value={0}>正常</Select.Option>
            <Select.Option value={1}>关闭</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="createBy" label="创建者">
          <Input placeholder="请输入创建者" allowClear />
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
          新增公告
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
        rowKey="noticeId"
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
        title={form.getFieldValue('noticeId') ? '编辑通知公告' : '新增通知公告'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={800}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 4 }} wrapperCol={{ span: 18 }}>
          <Form.Item name="noticeId" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            name="noticeTitle"
            label="公告标题"
            rules={[{ required: true, message: '请输入公告标题' }]}
          >
            <Input placeholder="请输入公告标题" maxLength={50} />
          </Form.Item>
          <Form.Item name="noticeType" label="公告类型" initialValue={1}>
            <Select>
              <Select.Option value={1}>通知</Select.Option>
              <Select.Option value={2}>公告</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={0}>
            <Select>
              <Select.Option value={0}>正常</Select.Option>
              <Select.Option value={1}>关闭</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="noticeContent" label="公告内容">
            <Input.TextArea
              rows={8}
              placeholder="请输入公告内容"
              maxLength={2000}
              showCount
            />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情弹窗 */}
      <Modal
        title="通知公告详情"
        open={viewVisible}
        onCancel={() => setViewVisible(false)}
        footer={[
          <Button key="close" onClick={() => setViewVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {currentNotice && (
          <div>
            <div style={{ marginBottom: 16 }}>
              <strong>公告标题：</strong>
              {currentNotice.noticeTitle}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>公告类型：</strong>
              <Tag color={noticeTypeMap[currentNotice.noticeType || 1]?.color}>
                {noticeTypeMap[currentNotice.noticeType || 1]?.text}
              </Tag>
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>状态：</strong>
              <Tag color={currentNotice.status === 0 ? 'success' : 'error'}>
                {currentNotice.status === 0 ? '正常' : '关闭'}
              </Tag>
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>创建者：</strong>
              {currentNotice.createBy}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>创建时间：</strong>
              {currentNotice.createTime}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>公告内容：</strong>
              <div
                style={{
                  marginTop: 8,
                  padding: 12,
                  background: '#f5f5f5',
                  borderRadius: 4,
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                }}
              >
                {currentNotice.noticeContent || '暂无内容'}
              </div>
            </div>
            {currentNotice.remark && (
              <div>
                <strong>备注：</strong>
                {currentNotice.remark}
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default NoticeManage;
