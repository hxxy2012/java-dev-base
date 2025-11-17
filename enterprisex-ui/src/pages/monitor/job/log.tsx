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
  Row,
  Col,
  Popconfirm,
  Descriptions,
} from 'antd';
import {
  SearchOutlined,
  ReloadOutlined,
  DeleteOutlined,
  ClearOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { listJobLog, delJobLog, cleanJobLog, type JobLog } from '@/api/monitor/job';

const JobLog: React.FC = () => {
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<JobLog[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<JobLog | null>(null);

  // 查询列表
  const fetchList = async () => {
    setLoading(true);
    try {
      const values = searchForm.getFieldsValue();
      const res = await listJobLog({
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

  // 查看详情
  const handleDetail = (record: JobLog) => {
    setCurrentRecord(record);
    setDetailVisible(true);
  };

  // 删除
  const handleDelete = async (id: number) => {
    try {
      await delJobLog(id);
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
      await delJobLog(selectedRowKeys as number[]);
      message.success('删除成功');
      setSelectedRowKeys([]);
      fetchList();
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 清空日志
  const handleClean = async () => {
    try {
      await cleanJobLog();
      message.success('清空成功');
      fetchList();
    } catch (error) {
      message.error('清空失败');
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

  const columns: ColumnsType<JobLog> = [
    {
      title: '日志编号',
      dataIndex: 'jobLogId',
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
      title: '日志信息',
      dataIndex: 'jobMessage',
      width: 200,
      ellipsis: true,
    },
    {
      title: '执行状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === 1 ? 'success' : 'error'}>
          {status === 1 ? '成功' : '失败'}
        </Tag>
      ),
    },
    {
      title: '执行时间',
      dataIndex: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleDetail(record)}
          >
            详情
          </Button>
          <Popconfirm
            title="确定删除该日志吗？"
            onConfirm={() => handleDelete(record.jobLogId)}
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
              <Form.Item name="status" label="执行状态">
                <Select placeholder="请选择状态" allowClear>
                  <Select.Option value={1}>成功</Select.Option>
                  <Select.Option value={0}>失败</Select.Option>
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
          <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
            批量删除
          </Button>
          <Popconfirm
            title="确定清空所有日志吗？"
            onConfirm={handleClean}
            okText="确定"
            cancelText="取消"
          >
            <Button danger icon={<ClearOutlined />}>
              清空
            </Button>
          </Popconfirm>
        </Space>

        <Table
          rowKey="jobLogId"
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
        title="日志详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        {currentRecord && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="日志编号">{currentRecord.jobLogId}</Descriptions.Item>
            <Descriptions.Item label="任务名称">{currentRecord.jobName}</Descriptions.Item>
            <Descriptions.Item label="任务组名">{currentRecord.jobGroup}</Descriptions.Item>
            <Descriptions.Item label="调用目标">{currentRecord.invokeTarget}</Descriptions.Item>
            <Descriptions.Item label="日志信息">{currentRecord.jobMessage}</Descriptions.Item>
            <Descriptions.Item label="执行状态">
              <Tag color={currentRecord.status === 1 ? 'success' : 'error'}>
                {currentRecord.status === 1 ? '成功' : '失败'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="执行时间">{currentRecord.createTime}</Descriptions.Item>
            {currentRecord.exceptionInfo && (
              <Descriptions.Item label="异常信息">
                <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>
                  {currentRecord.exceptionInfo}
                </pre>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default JobLog;
