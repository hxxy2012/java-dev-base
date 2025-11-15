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
  getMenuTree,
  addMenu,
  updateMenu,
  deleteMenu,
  Menu,
  MenuQuery,
} from '@/api/system/menu';

const MenuManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Menu[]>([]);
  const [menuTreeData, setMenuTreeData] = useState<any[]>([]);
  const [queryParams, setQueryParams] = useState<MenuQuery>({});
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  // 加载菜单列表
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getMenuTree(queryParams);
      if (res.code === 200) {
        setDataSource(res.data || []);
        // 构建TreeSelect数据
        buildTreeSelectData(res.data || []);
      }
    } catch (error) {
      console.error('加载菜单列表失败', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [queryParams]);

  // 构建TreeSelect数据
  const buildTreeSelectData = (menus: Menu[]) => {
    const treeData = [
      {
        title: '主类目',
        value: 0,
        children: convertToTreeSelectData(menus),
      },
    ];
    setMenuTreeData(treeData);
  };

  // 递归转换为TreeSelect需要的格式
  const convertToTreeSelectData = (menus: Menu[]): any[] => {
    return menus.map((menu) => ({
      title: menu.menuName,
      value: menu.menuId,
      children: menu.children ? convertToTreeSelectData(menu.children) : undefined,
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
  const handleSubmit = async (values: Menu) => {
    try {
      // 如果父菜单为0，设置为null
      if (values.parentId === 0) {
        values.parentId = 0;
      }

      if (values.menuId) {
        await updateMenu(values);
        message.success('修改成功');
      } else {
        await addMenu(values);
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
  const handleEdit = (record: Menu) => {
    form.setFieldsValue({
      ...record,
      parentId: record.parentId || 0,
    });
    setVisible(true);
  };

  // 删除
  const handleDelete = async (menuId: number) => {
    try {
      await deleteMenu(menuId);
      message.success('删除成功');
      loadData();
    } catch (error) {
      console.error('删除失败', error);
    }
  };

  // 菜单类型映射
  const menuTypeMap: { [key: string]: { text: string; color: string } } = {
    M: { text: '目录', color: 'blue' },
    C: { text: '菜单', color: 'green' },
    F: { text: '按钮', color: 'orange' },
  };

  // 表格列定义
  const columns = [
    {
      title: '菜单名称',
      dataIndex: 'menuName',
      width: 200,
    },
    {
      title: '菜单类型',
      dataIndex: 'menuType',
      width: 100,
      render: (menuType: string) => {
        const typeInfo = menuTypeMap[menuType] || { text: '未知', color: 'default' };
        return <Tag color={typeInfo.color}>{typeInfo.text}</Tag>;
      },
    },
    {
      title: '图标',
      dataIndex: 'icon',
      width: 80,
      render: (icon: string) => (icon ? <i className={icon} /> : '-'),
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      width: 80,
    },
    {
      title: '路由地址',
      dataIndex: 'path',
      width: 180,
    },
    {
      title: '组件路径',
      dataIndex: 'component',
      width: 200,
    },
    {
      title: '权限标识',
      dataIndex: 'perms',
      width: 150,
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
      title: '操作',
      key: 'action',
      width: 280,
      fixed: 'right' as const,
      render: (_: any, record: Menu) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record.menuId)}
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
            title="确定删除该菜单吗？"
            onConfirm={() => handleDelete(record.menuId!)}
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
        <Form.Item name="menuName" label="菜单名称">
          <Input placeholder="请输入菜单名称" allowClear />
        </Form.Item>
        <Form.Item name="menuType" label="菜单类型">
          <Select placeholder="请选择菜单类型" allowClear style={{ width: 120 }}>
            <Select.Option value="M">目录</Select.Option>
            <Select.Option value="C">菜单</Select.Option>
            <Select.Option value="F">按钮</Select.Option>
          </Select>
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
          新增菜单
        </Button>
      </Space>

      {/* 表格 */}
      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="menuId"
        pagination={false}
        scroll={{ x: 1500 }}
        expandable={{
          defaultExpandAllRows: true,
        }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={form.getFieldValue('menuId') ? '编辑菜单' : '新增菜单'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        width={700}
      >
        <Form form={form} onFinish={handleSubmit} labelCol={{ span: 5 }} wrapperCol={{ span: 17 }}>
          <Form.Item name="menuId" hidden>
            <Input />
          </Form.Item>
          <Form.Item name="parentId" label="上级菜单" initialValue={0}>
            <TreeSelect
              treeData={menuTreeData}
              placeholder="请选择上级菜单"
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item
            name="menuType"
            label="菜单类型"
            rules={[{ required: true, message: '请选择菜单类型' }]}
            initialValue="M"
          >
            <Select>
              <Select.Option value="M">目录</Select.Option>
              <Select.Option value="C">菜单</Select.Option>
              <Select.Option value="F">按钮</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="menuName"
            label="菜单名称"
            rules={[{ required: true, message: '请输入菜单名称' }]}
          >
            <Input placeholder="请输入菜单名称" />
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) =>
              prevValues.menuType !== currentValues.menuType
            }
          >
            {({ getFieldValue }) =>
              getFieldValue('menuType') !== 'F' && (
                <>
                  <Form.Item name="icon" label="菜单图标">
                    <Input placeholder="请输入图标类名" />
                  </Form.Item>
                  <Form.Item name="path" label="路由地址">
                    <Input placeholder="请输入路由地址" />
                  </Form.Item>
                </>
              )
            }
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) =>
              prevValues.menuType !== currentValues.menuType
            }
          >
            {({ getFieldValue }) =>
              getFieldValue('menuType') === 'C' && (
                <Form.Item name="component" label="组件路径">
                  <Input placeholder="请输入组件路径" />
                </Form.Item>
              )
            }
          </Form.Item>
          <Form.Item name="perms" label="权限标识">
            <Input placeholder="请输入权限标识，如：system:user:list" />
          </Form.Item>
          <Form.Item
            name="orderNum"
            label="显示排序"
            rules={[{ required: true, message: '请输入显示排序' }]}
            initialValue={0}
          >
            <InputNumber min={0} placeholder="请输入显示排序" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="visible" label="显示状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>显示</Select.Option>
              <Select.Option value={0}>隐藏</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="菜单状态" initialValue={1}>
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

export default MenuManage;
