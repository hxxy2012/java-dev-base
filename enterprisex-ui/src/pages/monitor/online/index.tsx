import React, { useEffect, useState } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Table,
  Space,
  message,
  Modal,
  Tag,
} from 'antd';
import { SearchOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getOnlineList, forceLogout, batchForceLogout, UserOnline } from '@/api/monitor/online';
import { formatDateTime } from '@/utils/date';

const OnlineUser: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<UserOnline[]>([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  // 查询在线用户列表
  const fetchData = async () => {
    setLoading(true);
    try {
      const values = form.getFieldsValue();
      const response = await getOnlineList(values);
      if (response.code === 200) {
        setDataSource(response.rows || []);
      }
    } catch (error) {
      console.error('查询失败：', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // 搜索
  const handleSearch = () => {
    fetchData();
  };

  // 重置
  const handleReset = () => {
    form.resetFields();
    fetchData();
  };

  // 强退用户
  const handleForceLogout = (record: UserOnline) => {
    Modal.confirm({
      title: '确认强退',
      content: `确定要强退用户"${record.username}"吗？`,
      onOk: async () => {
        try {
          const response = await forceLogout(record.tokenId);
          if (response.code === 200) {
            message.success('强退成功');
            fetchData();
          }
        } catch (error) {
          console.error('强退失败：', error);
        }
      },
    });
  };

  // 批量强退
  const handleBatchForceLogout = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要强退的用户');
      return;
    }

    Modal.confirm({
      title: '确认批量强退',
      content: `确定要强退选中的 ${selectedRowKeys.length} 个用户吗？`,
      onOk: async () => {
        try {
          const response = await batchForceLogout(selectedRowKeys as string[]);
          if (response.code === 200) {
            message.success('批量强退成功');
            setSelectedRowKeys([]);
            fetchData();
          }
        } catch (error) {
          console.error('批量强退失败：', error);
        }
      },
    });
  };

  const columns: ColumnsType<UserOnline> = [
    {
      title: '会话编号',
      dataIndex: 'tokenId',
      key: 'tokenId',
      width: 180,
      ellipsis: true,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '部门名称',
      dataIndex: 'deptName',
      key: 'deptName',
      width: 150,
    },
    {
      title: 'IP地址',
      dataIndex: 'ipaddr',
      key: 'ipaddr',
      width: 140,
    },
    {
      title: '登录地点',
      dataIndex: 'loginLocation',
      key: 'loginLocation',
      width: 150,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      key: 'browser',
      width: 120,
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      key: 'os',
      width: 120,
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
      width: 180,
      render: (time: number) => formatDateTime(new Date(time)),
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 100,
      render: (_, record) => (
        <Button
          type="link"
          danger
          size="small"
          icon={<DeleteOutlined />}
          onClick={() => handleForceLogout(record)}
        >
          强退
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Card>
        <Form form={form} layout="inline">
          <Form.Item name="username" label="用户名">
            <Input placeholder="请输入用户名" allowClear />
          </Form.Item>
          <Form.Item name="ipaddr" label="IP地址">
            <Input placeholder="请输入IP地址" allowClear />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button
                type="primary"
                icon={<SearchOutlined />}
                onClick={handleSearch}
              >
                搜索
              </Button>
              <Button icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>

      <Card style={{ marginTop: 16 }}>
        <Space style={{ marginBottom: 16 }}>
          <Button
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={handleBatchForceLogout}
            disabled={selectedRowKeys.length === 0}
          >
            批量强退
          </Button>
          <Button icon={<ReloadOutlined />} onClick={fetchData}>
            刷新
          </Button>
        </Space>

        <Table
          rowKey="tokenId"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          scroll={{ x: 1400 }}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          pagination={{
            total: dataSource.length,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>
    </div>
  );
};

export default OnlineUser;
