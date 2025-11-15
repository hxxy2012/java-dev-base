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
  TreeSelect,
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
  getDeptTree,
  addDept,
  updateDept,
  deleteDept,
  Dept,
  DeptQuery,
} from '@/api/system/dept';

const DeptManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Dept[]>([]);
  const [deptTreeData, setDeptTreeData] = useState<any[]>([]);
  const [queryParams, setQueryParams] = useState<DeptQuery>({});
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载部门列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getDeptTree(queryParams);
      if (res.code === 200) {
        setDataSource(res.data || []);
        // 构建TreeSelect数据
        buildTreeSelectData(res.data || []);
      }
    } catch (error) {
      console.error('加载部门列表失败', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [queryParams]);

  // 构建TreeSelect数据
  const buildTreeSelectData = (depts: Dept[]) => {
    const treeData = [
      {
        title: '主类目',
        value: 0,
        children: convertToTreeSelectData(depts),
      },
    ];
    setDeptTreeData(treeData);
  };

  // 递归转换为TreeSelect需要的格式
  const convertToTreeSelectData = (depts: Dept[]): any[] => {
    return depts.map((dept) => ({
      title: dept.deptName,
      value: dept.deptId,
      children: dept.children ? convertToTreeSelectData(dept.children) : undefined,
    }));
  };

  // 搜索
  const handleSearch = (values: any) => {
    setQueryParams({ ...values });
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    setQueryParams({});
  };

  // 新增/编辑提交
  const handleSubmit = async (values: Dept) => {
    try {
      // 如果父部门为0，设置为0
      if (values.parentId === 0) {
        values.parentId = 0;
      }

      if (values.deptId) {
        await updateDept(values);
        message.success('修改成功');
      } else {
        await addDept(values);
        message.success('新增成功');
      }
      setVisible(false);
      loadData();
    } catch (error) {
      console.error('操作失败', error);
    }
  };

  // 新增
  const handleAdd = (parentId?: number) => {
    form.resetFields();
    if (parentId !== undefined) {
      form.setFieldsValue({ parentId });
    }
    setVisible(true);
  };

  // 编辑
  const handleEdit = (record: Dept) => {
    form.setFieldsValue({
      ...record,
      parentId: record.parentId || 0,
    });
    setVisible(true);
  };

  // 删除
  const handleDelete = async (deptId: number) => {
    try {
      await deleteDept(deptId);
      message.success('删除成功');
      loadData();
    } catch (error) {
      console.error('删除失败', error);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '部门名称',
      dataIndex: 'deptName',
      width: 200,
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      width: 80,
    },
    {
      title: '负责人',
      dataIndex: 'leader',
      width: 120,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      width: 150,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 200,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: number) => (
        <Tag color={status === 1 ? 'success' : 'error'}>{status === 1 ? '正常' : '禁用'}</Tag>
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
      width: 280,
      fixed: 'right' as const,
      render: (_: any, record: Dept) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record.deptId)}
          >
            新增
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
            title="确定删除该部门吗？"
            onConfirm={() => handleDelete(record.deptId!)}
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
        <Form.Item name="deptName" label="部门名称">
          <Input placeholder="请输入部门名称" allowClear />
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
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>
          新增部门
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="deptId"
        pagination={false}
        scroll={{ x: 1300 }}
        expandable={{
          defaultExpandAllRows: true,
        }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('deptId') ? '编辑部门' : '新增部门'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={700}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 5 }} wrapperCol={{ span: 17 }}>
          <Form.Item name="deptId" hidden>
            <Input />
          </Form.Item>
          <Form.Item name="parentId" label="上级部门" initialValue={0}>
            <TreeSelect
              treeData={deptTreeData}
              placeholder="请选择上级部门"
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item
            name="deptName"
            label="部门名称"
            rules={[{ required: true, message: '请输入部门名称' }]}
          >
            <Input placeholder="请输入部门名称" />
          </Form.Item>
          <Form.Item
            name="orderNum"
            label="显示排序"
            rules={[{ required: true, message: '请输入显示排序' }]}
            initialValue={0}
          >
            <InputNumber min={0} placeholder="请输入显示排序" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="leader" label="负责人">
            <Input placeholder="请输入负责人" />
          </Form.Item>
          <Form.Item name="phone" label="联系电话">
            <Input placeholder="请输入联系电话" />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ type: 'email', message: '请输入正确的邮箱地址' }]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="status" label="部门状态" initialValue={1}>
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

export default DeptManage;
