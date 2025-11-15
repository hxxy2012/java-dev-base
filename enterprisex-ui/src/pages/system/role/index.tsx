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
  Switch,
  message,
  Popconfirm,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import {
  getRoleList,
  addRole,
  updateRole,
  deleteRole,
  changeRoleStatus,
  Role,
  RoleQuery,
} from '@/api/system/role';

const RoleManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Role[]>([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState<RoleQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载角色列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getRoleList(queryParams);
      if (res.code === 200) {
        setDataSource(res.rows || []);
        setTotal(res.total || 0);
      }
    } catch (error) {
      console.error('加载角色列表失败', error);
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
  const handleSubmit = async (values: Role) => {
    try {
      if (values.roleId) {
        await updateRole(values);
        message.success('修改成功');
      } else {
        await addRole(values);
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
  const handleEdit = (record: Role) => {
    form.setFieldsValue(record);
    setVisible(true);
  };

  // 删除
  const handleDelete = async (roleId: number) => {
    try {
      await deleteRole([roleId]);
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
      message.warning('请选择要删除的角色');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.length} 个角色吗？`,
      onOk: async () => {
        try {
          await deleteRole(selectedRowKeys as number[]);
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
  const handleStatusChange = async (roleId: number, status: number) => {
    try {
      await changeRoleStatus(roleId, status);
      message.success('状态修改成功');
      loadData();
    } catch (error) {
      console.error('状态修改失败', error);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '角色ID',
      dataIndex: 'roleId',
      width: 80,
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      width: 150,
    },
    {
      title: '角色权限字符串',
      dataIndex: 'roleKey',
      width: 150,
    },
    {
      title: '显示顺序',
      dataIndex: 'roleSort',
      width: 100,
    },
    {
      title: '数据范围',
      dataIndex: 'dataScope',
      width: 150,
      render: (dataScope: number) => {
        const scopeMap: { [key: number]: string } = {
          1: '全部数据权限',
          2: '自定义数据权限',
          3: '本部门数据权限',
          4: '本部门及以下数据权限',
          5: '仅本人数据权限',
        };
        return scopeMap[dataScope] || '-';
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number, record: Role) => (
        <Switch
          checked={status === 1}
          checkedChildren="正常"
          unCheckedChildren="禁用"
          onChange={(checked) => handleStatusChange(record.roleId!, checked ? 1 : 0)}
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
      render: (_: any, record: Role) => (
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
            title="确定删除该角色吗？"
            onConfirm={() => handleDelete(record.roleId!)}
            disabled={record.roleId === 1}
          >
            <Button
              type="link"
              danger
              size="small"
              icon={<DeleteOutlined />}
              disabled={record.roleId === 1}
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
        <Form.Item name="roleName" label="角色名称">
          <Input placeholder="请输入角色名称" allowClear />
        </Form.Item>
        <Form.Item name="roleKey" label="权限字符">
          <Input placeholder="请输入权限字符" allowClear />
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
          新增角色
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
        rowKey="roleId"
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
          getCheckboxProps: (record) => ({
            disabled: record.roleId === 1,
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
        scroll={{ x: 1300 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('roleId') ? '编辑角色' : '新增角色'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item name="roleId" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            name="roleName"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称' }]}
          >
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item
            name="roleKey"
            label="权限字符"
            rules={[{ required: true, message: '请输入权限字符' }]}
          >
            <Input placeholder="请输入权限字符" />
          </Form.Item>
          <Form.Item
            name="roleSort"
            label="显示顺序"
            rules={[{ required: true, message: '请输入显示顺序' }]}
          >
            <InputNumber min={0} placeholder="请输入显示顺序" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="dataScope" label="数据范围" initialValue={1}>
            <Select>
              <Select.Option value={1}>全部数据权限</Select.Option>
              <Select.Option value={2}>自定义数据权限</Select.Option>
              <Select.Option value={3}>本部门数据权限</Select.Option>
              <Select.Option value={4}>本部门及以下数据权限</Select.Option>
              <Select.Option value={5}>仅本人数据权限</Select.Option>
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

export default RoleManage;
