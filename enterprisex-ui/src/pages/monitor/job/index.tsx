import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Form,
  Input,
  Select,
  Modal,
  message,
  Tag,
  Switch,
  Row,
  Col,
  Popconfirm,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  PlayCircleOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  listJob,
  getJob,
  addJob,
  updateJob,
  delJob,
  changeJobStatus,
  runJob,
  type Job,
} from '@/api/monitor/job';

const JobManage: React.FC = () => {
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Job[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  // 查询列表
  const fetchList = async () => {
    setLoading(true);
    try {
      const values = searchForm.getFieldsValue();
      const res = await listJob({
        ...values,
        pageNum,
        pageSize,
      });
      setDataSource(res.data.rows);
      setTotal(res.data.total);
    } catch (error) {
      message.error('查询失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
  }, [pageNum, pageSize]);

  // 新增
  const handleAdd = () => {
    setModalTitle('新增定时任务');
    setModalVisible(true);
    form.resetFields();
  };

  // 编辑
  const handleEdit = async (record: Job) => {
    setModalTitle('编辑定时任务');
    setModalVisible(true);
    const res = await getJob(record.jobId!);
    form.setFieldsValue(res.data);
  };

  // 删除
  const handleDelete = async (id: number) => {
    try {
      await delJob(id);
      message.success('删除成功');
      fetchList();
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await delJob(selectedRowKeys as number[]);
      message.success('删除成功');
      setSelectedRowKeys([]);
      fetchList();
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 状态变更
  const handleStatusChange = async (checked: boolean, record: Job) => {
    try {
      await changeJobStatus(record.jobId!, checked ? 1 : 0, record.jobGroup);
      message.success('状态修改成功');
      fetchList();
    } catch (error) {
      message.error('状态修改失败');
    }
  };

  // 立即执行
  const handleRun = async (record: Job) => {
    try {
      await runJob(record.jobId!, record.jobGroup);
      message.success('执行成功');
    } catch (error) {
      message.error('执行失败');
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (modalTitle === '新增定时任务') {
        await addJob(values);
        message.success('新增成功');
      } else {
        await updateJob(values);
        message.success('修改成功');
      }
      setModalVisible(false);
      fetchList();
    } catch (error) {
      message.error('操作失败');
    }
  };

  // 搜索
  const handleSearch = () => {
    setPageNum(1);
    fetchList();
  };

  // 重置
  const handleReset = () => {
    searchForm.resetFields();
    setPageNum(1);
    fetchList();
  };

  const columns: ColumnsType<Job> = [
    {
      title: '任务编号',
      dataIndex: 'jobId',
      width: 100,
    },
    {
      title: '任务名称',
      dataIndex: 'jobName',
      width: 150,
    },
    {
      title: '任务组名',
      dataIndex: 'jobGroup',
      width: 120,
    },
    {
      title: '调用目标字符串',
      dataIndex: 'invokeTarget',
      width: 200,
      ellipsis: true,
    },
    {
      title: 'cron表达式',
      dataIndex: 'cronExpression',
      width: 150,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status, record) => (
        <Switch
          checked={status === 1}
          onChange={(checked) => handleStatusChange(checked, record)}
          checkedChildren="正常"
          unCheckedChildren="暂停"
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            icon={<PlayCircleOutlined />}
            onClick={() => handleRun(record)}
          >
            执行
          </Button>
          <Popconfirm
            title="确定删除该任务吗？"
            onConfirm={() => handleDelete(record.jobId!)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card>
        <Form form={searchForm} layout="inline">
          <Row gutter={16} style={{ width: '100%' }}>
            <Col span={6}>
              <Form.Item name="jobName" label="任务名称">
                <Input placeholder="请输入任务名称" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="jobGroup" label="任务组名">
                <Input placeholder="请输入任务组名" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="status" label="任务状态">
                <Select placeholder="请选择状态" allowClear>
                  <Select.Option value={1}>正常</Select.Option>
                  <Select.Option value={0}>暂停</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item>
                <Space>
                  <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                    搜索
                  </Button>
                  <Button icon={<ReloadOutlined />} onClick={handleReset}>
                    重置
                  </Button>
                </Space>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      <Card style={{ marginTop: 16 }}>
        <Space style={{ marginBottom: 16 }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增
          </Button>
          <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
            批量删除
          </Button>
        </Space>

        <Table
          rowKey="jobId"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          scroll={{ x: 1200 }}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          pagination={{
            current: pageNum,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, size) => {
              setPageNum(page);
              setPageSize(size);
            },
          }}
        />
      </Card>

      <Modal
        title={modalTitle}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
        destroyOnClose
      >
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item name="jobId" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            name="jobName"
            label="任务名称"
            rules={[{ required: true, message: '请输入任务名称' }]}
          >
            <Input placeholder="请输入任务名称" />
          </Form.Item>
          <Form.Item
            name="jobGroup"
            label="任务组名"
            rules={[{ required: true, message: '请输入任务组名' }]}
            initialValue="DEFAULT"
          >
            <Input placeholder="请输入任务组名" />
          </Form.Item>
          <Form.Item
            name="invokeTarget"
            label="调用方法"
            rules={[{ required: true, message: '请输入调用目标字符串' }]}
            tooltip="Bean调用示例：ryTask.ryParams('ry')"
          >
            <Input placeholder="请输入调用目标字符串" />
          </Form.Item>
          <Form.Item
            name="cronExpression"
            label="cron表达式"
            rules={[{ required: true, message: '请输入cron执行表达式' }]}
            tooltip="如：0 0/10 * * * ?"
          >
            <Input placeholder="请输入cron执行表达式" />
          </Form.Item>
          <Form.Item
            name="misfirePolicy"
            label="执行策略"
            initialValue="3"
            rules={[{ required: true }]}
          >
            <Select>
              <Select.Option value="1">立即执行</Select.Option>
              <Select.Option value="2">执行一次</Select.Option>
              <Select.Option value="3">放弃执行</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="concurrent" label="是否并发" initialValue={1}>
            <Select>
              <Select.Option value={1}>允许</Select.Option>
              <Select.Option value={0}>禁止</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={0}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>暂停</Select.Option>
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

export default JobManage;
