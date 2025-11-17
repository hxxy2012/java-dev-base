import React, { useState, useEffect } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Table,
  Space,
  Modal,
  message,
  Upload,
  Tag,
  Image,
} from 'antd';
import {
  SearchOutlined,
  ReloadOutlined,
  DeleteOutlined,
  UploadOutlined,
  DownloadOutlined,
  FileOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { listFile, delFile, uploadFile, downloadFile, type SysFile } from '@/api/system/file';
import { formatFileSize } from '@/utils/common';
import { formatDateTime } from '@/utils/date';

const FilePage: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<SysFile[]>([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewFile, setPreviewFile] = useState<SysFile | null>(null);

  // 查询文件列表
  const fetchList = async () => {
    setLoading(true);
    try {
      const values = await form.getFieldsValue();
      const res = await listFile(values);
      setDataSource(res.data || []);
    } catch (error) {
      message.error('查询失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
  }, []);

  // 搜索
  const handleSearch = () => {
    fetchList();
  };

  // 重置
  const handleReset = () => {
    form.resetFields();
    fetchList();
  };

  // 上传文件
  const handleUpload = async (file: File) => {
    try {
      const res = await uploadFile(file);
      message.success('上传成功');
      fetchList();
      return false;
    } catch (error) {
      message.error('上传失败');
      return false;
    }
  };

  // 下载文件
  const handleDownload = async (record: SysFile) => {
    try {
      const res = await downloadFile(record.fileId!);
      const blob = new Blob([res]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = record.originalName;
      a.click();
      window.URL.revokeObjectURL(url);
      message.success('下载成功');
    } catch (error) {
      message.error('下载失败');
    }
  };

  // 预览文件
  const handlePreview = (record: SysFile) => {
    setPreviewFile(record);
    setPreviewVisible(true);
  };

  // 删除
  const handleDelete = (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      content: '是否确认删除选中的文件？',
      onOk: async () => {
        try {
          await delFile(ids);
          message.success('删除成功');
          setSelectedRowKeys([]);
          fetchList();
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    handleDelete(selectedRowKeys as number[]);
  };

  // 判断是否为图片
  const isImage = (fileType: string) => {
    return fileType && fileType.startsWith('image/');
  };

  const columns: ColumnsType<SysFile> = [
    {
      title: '文件ID',
      dataIndex: 'fileId',
      width: 80,
    },
    {
      title: '文件名称',
      dataIndex: 'originalName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '文件大小',
      dataIndex: 'fileSize',
      width: 120,
      render: (size: number) => formatFileSize(size),
    },
    {
      title: '文件类型',
      dataIndex: 'fileType',
      width: 150,
      ellipsis: true,
    },
    {
      title: '存储位置',
      dataIndex: 'storageLocation',
      width: 100,
      render: (location: string) => (
        <Tag color={location === 'minio' ? 'blue' : 'green'}>
          {location === 'minio' ? 'MinIO' : '本地'}
        </Tag>
      ),
    },
    {
      title: '上传者',
      dataIndex: 'uploadBy',
      width: 120,
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
      width: 180,
      render: (time: string) => formatDateTime(time),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          {isImage(record.fileType) && (
            <Button
              type="link"
              size="small"
              icon={<EyeOutlined />}
              onClick={() => handlePreview(record)}
            >
              预览
            </Button>
          )}
          <Button
            type="link"
            size="small"
            icon={<DownloadOutlined />}
            onClick={() => handleDownload(record)}
          >
            下载
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete([record.fileId!])}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys),
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
          <Form.Item name="fileName" label="文件名称">
            <Input placeholder="请输入文件名称" allowClear />
          </Form.Item>
          <Form.Item name="fileType" label="文件类型">
            <Input placeholder="请输入文件类型" allowClear />
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

        <div style={{ marginBottom: 16 }}>
          <Space>
            <Upload beforeUpload={handleUpload} showUploadList={false}>
              <Button type="primary" icon={<UploadOutlined />}>
                上传文件
              </Button>
            </Upload>
            <Button
              danger
              icon={<DeleteOutlined />}
              onClick={handleBatchDelete}
              disabled={selectedRowKeys.length === 0}
            >
              批量删除
            </Button>
          </Space>
        </div>

        <Table
          rowKey="fileId"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowSelection={rowSelection}
          scroll={{ x: 1200 }}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>

      {/* 文件预览弹窗 */}
      <Modal
        title="文件预览"
        open={previewVisible}
        footer={null}
        onCancel={() => setPreviewVisible(false)}
        width={800}
      >
        {previewFile && isImage(previewFile.fileType) && (
          <div style={{ textAlign: 'center' }}>
            <Image
              src={`/api${previewFile.fileUrl}`}
              alt={previewFile.originalName}
              style={{ maxWidth: '100%' }}
            />
            <p style={{ marginTop: 16, color: '#666' }}>
              {previewFile.originalName} ({formatFileSize(previewFile.fileSize)})
            </p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default FilePage;
