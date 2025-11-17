import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Form,
  Input,
  Modal,
  message,
  Tabs,
  Descriptions,
} from 'antd';
import {
  SearchOutlined,
  ReloadOutlined,
  DownloadOutlined,
  EyeOutlined,
  CodeOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  listDbTable,
  getDbTableColumns,
  previewTable,
  downloadCode,
  type GenTable,
  type GenTableColumn,
} from '@/api/tool/gen';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

const CodeGen: React.FC = () => {
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<GenTable[]>([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewData, setPreviewData] = useState<Record<string, string>>({});
  const [columnVisible, setColumnVisible] = useState(false);
  const [columns, setColumns] = useState<GenTableColumn[]>([]);

  // 查询列表
  const fetchList = async () => {
    setLoading(true);
    try {
      const values = searchForm.getFieldsValue();
      const res = await listDbTable(values);
      setDataSource(res.data.rows || res.data);
    } catch (error) {
      message.error('查询失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
  }, []);

  // 预览代码
  const handlePreview = async (record: GenTable) => {
    try {
      const res = await previewTable(record.tableName);
      setPreviewData(res.data);
      setPreviewVisible(true);
    } catch (error) {
      message.error('预览失败');
    }
  };

  // 下载代码
  const handleDownload = async (record: GenTable) => {
    try {
      const res = await downloadCode(record.tableName);
      const url = window.URL.createObjectURL(new Blob([res as any]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${record.tableName}.zip`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      message.success('下载成功');
    } catch (error) {
      message.error('下载失败');
    }
  };

  // 查看表结构
  const handleViewColumn = async (record: GenTable) => {
    try {
      const res = await getDbTableColumns(record.tableName);
      setColumns(res.data);
      setColumnVisible(true);
    } catch (error) {
      message.error('查询失败');
    }
  };

  // 搜索
  const handleSearch = () => {
    fetchList();
  };

  // 重置
  const handleReset = () => {
    searchForm.resetFields();
    fetchList();
  };

  const tableColumns: ColumnsType<GenTable> = [
    {
      title: '表名称',
      dataIndex: 'tableName',
      width: 200,
    },
    {
      title: '表描述',
      dataIndex: 'tableComment',
      width: 200,
    },
    {
      title: '实体类名',
      dataIndex: 'className',
      width: 150,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 300,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<CodeOutlined />}
            onClick={() => handleViewColumn(record)}
          >
            表结构
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handlePreview(record)}
          >
            预览
          </Button>
          <Button
            type="link"
            size="small"
            icon={<DownloadOutlined />}
            onClick={() => handleDownload(record)}
          >
            生成代码
          </Button>
        </Space>
      ),
    },
  ];

  const columnColumns: ColumnsType<GenTableColumn> = [
    {
      title: '列名称',
      dataIndex: 'columnName',
      width: 150,
    },
    {
      title: '列描述',
      dataIndex: 'columnComment',
      width: 150,
    },
    {
      title: '列类型',
      dataIndex: 'columnType',
      width: 120,
    },
    {
      title: 'Java类型',
      dataIndex: 'javaType',
      width: 120,
    },
    {
      title: 'Java属性',
      dataIndex: 'javaField',
      width: 120,
    },
    {
      title: '主键',
      dataIndex: 'isPk',
      width: 80,
      render: (isPk) => (isPk === '1' ? '是' : '否'),
    },
    {
      title: '自增',
      dataIndex: 'isIncrement',
      width: 80,
      render: (isIncrement) => (isIncrement === '1' ? '是' : '否'),
    },
    {
      title: '必填',
      dataIndex: 'isRequired',
      width: 80,
      render: (isRequired) => (isRequired === '1' ? '是' : '否'),
    },
  ];

  return (
    <div>
      <Card>
        <Form form={searchForm} layout="inline">
          <Form.Item name="tableName" label="表名称">
            <Input placeholder="请输入表名称" allowClear />
          </Form.Item>
          <Form.Item name="tableComment" label="表描述">
            <Input placeholder="请输入表描述" allowClear />
          </Form.Item>
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
        </Form>
      </Card>

      <Card style={{ marginTop: 16 }}>
        <Table
          rowKey="tableName"
          columns={tableColumns}
          dataSource={dataSource}
          loading={loading}
          scroll={{ x: 1000 }}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>

      {/* 代码预览弹窗 */}
      <Modal
        title="代码预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width="80%"
        style={{ top: 20 }}
      >
        <Tabs
          items={Object.keys(previewData).map((key) => ({
            key,
            label: key,
            children: (
              <SyntaxHighlighter
                language="java"
                style={vscDarkPlus}
                showLineNumbers
                customStyle={{ maxHeight: '60vh', overflow: 'auto' }}
              >
                {previewData[key]}
              </SyntaxHighlighter>
            ),
          }))}
        />
      </Modal>

      {/* 表结构弹窗 */}
      <Modal
        title="表结构"
        open={columnVisible}
        onCancel={() => setColumnVisible(false)}
        footer={null}
        width={1000}
      >
        <Table
          rowKey="columnName"
          columns={columnColumns}
          dataSource={columns}
          pagination={false}
          scroll={{ y: 400 }}
        />
      </Modal>
    </div>
  );
};

export default CodeGen;
