import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  Select,
  Switch,
  message,
  Popconfirm,
  Upload,
} from 'antd';
import type { UploadFile } from 'antd/es/upload/interface';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  UploadOutlined,
  DownloadOutlined,
  FileExcelOutlined,
} from '@ant-design/icons';
import {
  getUserList,
  addUser,
  updateUser,
  deleteUser,
  changeUserStatus,
  exportUser,
  downloadTemplate,
  importUser,
  User,
  UserQuery,
} from '@/api/system/user';

const UserManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<User[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<UserQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [visible, setVisible] = useState(false);
  const [importVisible, setImportVisible] = useState(false);
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载用户列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getUserList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载用户列表失败', error);
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
  const handleSubmit = async (values: User) => {
    try {
      if (values.userId) {
        await updateUser(values);
        message.success('修改成功');
      } else {
        await addUser(values);
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
  const handleEdit = (record: User) => {
    form.setFieldsValue(record);
    setVisible(true);
  };

  // 删除
  const handleDelete = async (userId: number) => {
    try {
      await deleteUser([userId]);
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
      message.warning('请选择要删除的用户');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 个用户吗？`,
      onOk: async () => {
        try {
          await deleteUser(selectedRowKeys as number[]);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          loadData();
        } catch (error) {
          console.error('批量删除失败', error);
        }
      },
    });
  };

  // 修改状态
  const handleStatusChange = async (userId: number, status: number) => {
    try {
      await changeUserStatus(userId, status);
      message.success('状态修改成功');
      loadData();
    } catch (error) {
      console.error('状态修改失败', error);
    }
  };

  // 导出用户
  const handleExport = () => {
    const url = exportUser(queryParams);
    window.open(url, '_blank');
    message.success('导出成功');
  };

  // 下载模板
  const handleDownloadTemplate = () => {
    window.open(downloadTemplate(), '_blank');
    message.success('模板下载开始');
  };

  // 导入用户
  const handleImport = async () => {
    if (fileList.length === 0) {
      message.warning('请选择要导入的文件');
      return;
    }

    const file = fileList[0].originFileObj as File;
    setLoading(true);
    try {
      const response = await importUser(file);
      if (response.code === 200) {
        message.success(response.msg || '导入成功');
      } else {
        // 显示详细的失败信息（包含HTML换行）
        Modal.warning({
          title: '导入结果',
          content: <div dangerouslySetInnerHTML={{ __html: response.msg }} />,
          width: 600,
        });
      }
      setImportVisible(false);
      setFileList([]);
      loadData();
    } catch (error) {
      message.error('导入失败');
    } finally {
      setLoading(false);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '用户ID',
      dataIndex: 'userId',
      width: 80,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 120,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      width: 120,
    },
    {
      title: '部门',
      dataIndex: 'deptId',
      width: 150,
      render: (deptId: number) => deptId || '-',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 180,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number, record: User) => (
        <Switch
          checked={status === 1}
          checkedChildren="正常"
          unCheckedChildren="禁用"
          onChange={(checked) => handleStatusChange(record.userId!, checked ? 1 : 0)}
        />
      ),
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
      render: (_: any, record: User) => (
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
            title="确定删除该用户吗？"
            onConfirm={() => handleDelete(record.userId!)}
            disabled={record.userId === 1}
          >
            <Button
              type="link"
              danger
              size="small"
              icon={<DeleteOutlined />}
              disabled={record.userId === 1}
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
        <Form.Item name="username" label="用户名">
          <Input placeholder="请输入用户名" allowClear />
        </Form.Item>
        <Form.Item name="nickname" label="昵称">
          <Input placeholder="请输入昵称" allowClear />
        </Form.Item>
        <Form.Item name="phone" label="手机号">
          <Input placeholder="请输入手机号" allowClear />
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
          新增用户
        </Button>
        <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete}>
          批量删除
        </Button>
        <Button icon={<UploadOutlined />} onClick={() => setImportVisible(true)}>
          导入
        </Button>
        <Button icon={<DownloadOutlined />} onClick={handleExport}>
          导出
        </Button>
        <Button icon={<FileExcelOutlined />} onClick={handleDownloadTemplate}>
          下载模板
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="userId"
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
          getCheckboxProps: (record) => ({
            disabled: record.userId === 1, // 超级管理员不能被选中
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
        scroll={{ x: 1500 }}
      />

      {/* 导入弹窗 */}
      <Modal
        title="导入用户"
        open={importVisible}
        onOk={handleImport}
        onCancel={() => {
          setImportVisible(false);
          setFileList([]);
        }}
        confirmLoading={loading}
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <div>
            <p>请先下载模板，按照模板格式填写数据，然后上传Excel文件进行导入。</p>
            <p style={{ color: '#ff4d4f' }}>注意：用户名不能重复，密码为空时默认为 123456</p>
          </div>
          <Upload
            fileList={fileList}
            beforeUpload={(file) => {
              const isExcel =
                file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                file.type === 'application/vnd.ms-excel' ||
                file.name.endsWith('.xlsx') ||
                file.name.endsWith('.xls');
              if (!isExcel) {
                message.error('只能上传Excel文件！');
                return false;
              }
              setFileList([file as UploadFile]);
              return false;
            }}
            onRemove={() => {
              setFileList([]);
            }}
            maxCount={1}
          >
            <Button icon={<UploadOutlined />}>选择Excel文件</Button>
          </Upload>
        </Space>
      </Modal>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('userId') ? '编辑用户' : '新增用户'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item name="userId" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item name="nickname" label="昵称">
            <Input placeholder="请输入昵称" />
          </Form.Item>
          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: !form.getFieldValue('userId'), message: '请输入密码' },
              { min: 6, message: '密码至少6位' },
            ]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ type: 'email', message: '请输入正确的邮箱格式' }]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="gender" label="性别">
            <Select placeholder="请选择性别">
              <Select.Option value={0}>未知</Select.Option>
              <Select.Option value={1}>男</Select.Option>
              <Select.Option value={2}>女</Select.Option>
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

export default UserManage;
