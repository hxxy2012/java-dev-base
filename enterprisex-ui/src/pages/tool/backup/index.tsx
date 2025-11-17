import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Table,
  Space,
  message,
  Modal,
  Tag,
  Tooltip,
  Upload,
  Alert
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  CloudDownloadOutlined,
  CloudUploadOutlined,
  DeleteOutlined,
  ReloadOutlined,
  ExclamationCircleOutlined,
  UploadOutlined,
  SyncOutlined
} from '@ant-design/icons';
import type { UploadFile } from 'antd/es/upload/interface';
import {
  createBackup,
  getBackupList,
  downloadBackup,
  restoreBackup,
  deleteBackup,
  uploadBackup,
  type BackupInfo
} from '@/api/tool/backup';
import { formatDateTime } from '@/utils/date';

const BackupPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [backupList, setBackupList] = useState<BackupInfo[]>([]);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [fileList, setFileList] = useState<UploadFile[]>([]);

  // 加载备份列表
  const loadBackupList = async () => {
    setLoading(true);
    try {
      const response = await getBackupList();
      setBackupList(response.data || []);
    } catch (error) {
      message.error('加载备份列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBackupList();
  }, []);

  // 创建备份
  const handleCreateBackup = async () => {
    setLoading(true);
    try {
      await createBackup();
      message.success('备份创建成功');
      loadBackupList();
    } catch (error) {
      message.error('创建备份失败');
    } finally {
      setLoading(false);
    }
  };

  // 下载备份
  const handleDownload = (fileName: string) => {
    const url = downloadBackup(fileName);
    window.open(url, '_blank');
  };

  // 恢复备份
  const handleRestore = (fileName: string) => {
    Modal.confirm({
      title: '确认恢复',
      icon: <ExclamationCircleOutlined />,
      content: (
        <div>
          <p>确定要恢复此备份吗？</p>
          <p style={{ color: 'red' }}>
            <strong>警告：</strong>恢复操作将覆盖当前数据库的所有数据，且不可逆！
          </p>
          <p>备份文件：{fileName}</p>
        </div>
      ),
      okText: '确认恢复',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await restoreBackup(fileName);
          message.success('数据库恢复成功');
        } catch (error) {
          message.error('恢复失败');
        }
      },
    });
  };

  // 删除备份
  const handleDelete = (fileName: string) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定要删除备份文件 "${fileName}" 吗？`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteBackup(fileName);
          message.success('删除成功');
          loadBackupList();
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 上传备份
  const handleUpload = async () => {
    if (fileList.length === 0) {
      message.warning('请选择要上传的备份文件');
      return;
    }

    const file = fileList[0].originFileObj as File;
    setLoading(true);
    try {
      await uploadBackup(file);
      message.success('上传成功');
      setUploadModalVisible(false);
      setFileList([]);
      loadBackupList();
    } catch (error) {
      message.error('上传失败');
    } finally {
      setLoading(false);
    }
  };

  const columns: ColumnsType<BackupInfo> = [
    {
      title: '备份文件名',
      dataIndex: 'fileName',
      key: 'fileName',
      width: 300,
      render: (text: string) => (
        <Tooltip title={text}>
          <span style={{ fontFamily: 'monospace' }}>{text}</span>
        </Tooltip>
      ),
    },
    {
      title: '文件大小',
      dataIndex: 'fileSizeStr',
      key: 'fileSizeStr',
      width: 120,
      align: 'right',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (text: string) => formatDateTime(text),
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<CloudDownloadOutlined />}
            onClick={() => handleDownload(record.fileName)}
          >
            下载
          </Button>
          <Button
            type="link"
            size="small"
            icon={<SyncOutlined />}
            onClick={() => handleRestore(record.fileName)}
          >
            恢复
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.fileName)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card
        title="数据库备份管理"
        extra={
          <Space>
            <Button
              type="primary"
              icon={<CloudUploadOutlined />}
              onClick={handleCreateBackup}
              loading={loading}
            >
              创建备份
            </Button>
            <Button
              icon={<UploadOutlined />}
              onClick={() => setUploadModalVisible(true)}
            >
              上传备份
            </Button>
            <Button
              icon={<ReloadOutlined />}
              onClick={loadBackupList}
              loading={loading}
            >
              刷新
            </Button>
          </Space>
        }
      >
        <Alert
          message="数据库备份说明"
          description={
            <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
              <li>备份功能需要服务器安装 <code>mysqldump</code> 和 <code>mysql</code> 命令行工具</li>
              <li>创建备份将导出整个数据库到 SQL 文件</li>
              <li>恢复备份将覆盖当前数据库的所有数据，操作不可逆，请谨慎操作</li>
              <li>建议定期创建备份，并下载到本地保存</li>
            </ul>
          }
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
        />

        <Table
          columns={columns}
          dataSource={backupList}
          rowKey="fileName"
          loading={loading}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>

      {/* 上传备份弹窗 */}
      <Modal
        title="上传备份文件"
        open={uploadModalVisible}
        onOk={handleUpload}
        onCancel={() => {
          setUploadModalVisible(false);
          setFileList([]);
        }}
        confirmLoading={loading}
      >
        <Alert
          message="只支持上传 .sql 格式的数据库备份文件"
          type="warning"
          showIcon
          style={{ marginBottom: 16 }}
        />
        <Upload
          fileList={fileList}
          beforeUpload={(file) => {
            if (!file.name.endsWith('.sql')) {
              message.error('只支持上传 .sql 格式的文件');
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
          <Button icon={<UploadOutlined />}>选择文件</Button>
        </Upload>
      </Modal>
    </div>
  );
};

export default BackupPage;
