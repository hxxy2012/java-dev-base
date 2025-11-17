import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  List,
  Button,
  Space,
  Input,
  Modal,
  message,
  Tag,
  Tooltip,
  Empty
} from 'antd';
import {
  FileTextOutlined,
  ReloadOutlined,
  DownloadOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import {
  getLogFileList,
  getLogContent,
  searchLog,
  downloadLog,
  clearLog,
  deleteLog,
  type LogFileInfo,
  type LogContent,
} from '@/api/tool/logviewer';
import { formatDateTime } from '@/utils/date';

const { Search } = Input;

const LogViewerPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [logFiles, setLogFiles] = useState<LogFileInfo[]>([]);
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [logContent, setLogContent] = useState<LogContent | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');

  // 加载日志文件列表
  const loadLogFiles = async () => {
    setLoading(true);
    try {
      const response = await getLogFileList();
      setLogFiles(response.data || []);
    } catch (error) {
      message.error('加载日志文件列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 加载日志内容
  const loadLogContent = async (fileName: string) => {
    setLoading(true);
    try {
      const response = await getLogContent(fileName, 1000);
      setLogContent(response.data);
      setSelectedFile(fileName);
      setSearchKeyword('');
    } catch (error) {
      message.error('加载日志内容失败');
    } finally {
      setLoading(false);
    }
  };

  // 搜索日志
  const handleSearch = async (keyword: string) => {
    if (!selectedFile) {
      message.warning('请先选择日志文件');
      return;
    }

    if (!keyword.trim()) {
      // 如果搜索关键字为空，重新加载完整日志
      loadLogContent(selectedFile);
      return;
    }

    setLoading(true);
    try {
      const response = await searchLog(selectedFile, keyword, 500);
      setLogContent(response.data);
      setSearchKeyword(keyword);
      message.success(`找到 ${response.data.totalLines} 条匹配记录`);
    } catch (error) {
      message.error('搜索失败');
    } finally {
      setLoading(false);
    }
  };

  // 下载日志
  const handleDownload = (fileName: string) => {
    const url = downloadLog(fileName);
    window.open(url, '_blank');
    message.success('下载开始');
  };

  // 清空日志
  const handleClear = (fileName: string) => {
    Modal.confirm({
      title: '确认清空',
      icon: <ExclamationCircleOutlined />,
      content: `确定要清空日志文件 "${fileName}" 的内容吗？此操作不可恢复！`,
      okText: '确认清空',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await clearLog(fileName);
          message.success('日志文件已清空');
          if (selectedFile === fileName) {
            loadLogContent(fileName);
          }
        } catch (error) {
          message.error('清空失败');
        }
      },
    });
  };

  // 删除日志
  const handleDelete = (fileName: string) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定要删除日志文件 "${fileName}" 吗？此操作不可恢复！`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteLog(fileName);
          message.success('日志文件已删除');
          loadLogFiles();
          if (selectedFile === fileName) {
            setSelectedFile(null);
            setLogContent(null);
          }
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 刷新日志内容
  const handleRefresh = () => {
    if (selectedFile) {
      if (searchKeyword) {
        handleSearch(searchKeyword);
      } else {
        loadLogContent(selectedFile);
      }
    }
  };

  useEffect(() => {
    loadLogFiles();
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <Row gutter={16}>
        {/* 左侧：日志文件列表 */}
        <Col xs={24} md={8}>
          <Card
            title="日志文件列表"
            extra={
              <Button
                icon={<ReloadOutlined />}
                onClick={loadLogFiles}
                loading={loading}
                size="small"
              >
                刷新
              </Button>
            }
          >
            {logFiles.length === 0 ? (
              <Empty description="暂无日志文件" />
            ) : (
              <List
                dataSource={logFiles}
                renderItem={(item) => (
                  <List.Item
                    key={item.fileName}
                    style={{
                      cursor: 'pointer',
                      backgroundColor: selectedFile === item.fileName ? '#e6f7ff' : 'transparent',
                      padding: '12px',
                      borderRadius: 4,
                    }}
                    onClick={() => loadLogContent(item.fileName)}
                  >
                    <List.Item.Meta
                      avatar={<FileTextOutlined style={{ fontSize: 24 }} />}
                      title={
                        <Tooltip title={item.fileName}>
                          <div
                            style={{
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              whiteSpace: 'nowrap',
                            }}
                          >
                            {item.fileName}
                          </div>
                        </Tooltip>
                      }
                      description={
                        <Space direction="vertical" size={0}>
                          <span>大小: {item.fileSizeStr}</span>
                          <span style={{ fontSize: 12 }}>
                            更新: {formatDateTime(item.lastModified)}
                          </span>
                        </Space>
                      }
                    />
                  </List.Item>
                )}
              />
            )}
          </Card>
        </Col>

        {/* 右侧：日志内容查看器 */}
        <Col xs={24} md={16}>
          <Card
            title={
              <Space>
                <span>日志查看器</span>
                {selectedFile && <Tag color="blue">{selectedFile}</Tag>}
              </Space>
            }
            extra={
              selectedFile && (
                <Space>
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={handleRefresh}
                    loading={loading}
                    size="small"
                  >
                    刷新
                  </Button>
                  <Button
                    icon={<DownloadOutlined />}
                    onClick={() => handleDownload(selectedFile)}
                    size="small"
                  >
                    下载
                  </Button>
                  <Button
                    icon={<ClearOutlined />}
                    onClick={() => handleClear(selectedFile)}
                    size="small"
                    danger
                  >
                    清空
                  </Button>
                  <Button
                    icon={<DeleteOutlined />}
                    onClick={() => handleDelete(selectedFile)}
                    size="small"
                    danger
                  >
                    删除
                  </Button>
                </Space>
              )
            }
          >
            {selectedFile && (
              <div style={{ marginBottom: 16 }}>
                <Search
                  placeholder="搜索日志内容（支持关键字搜索）"
                  allowClear
                  enterButton={<SearchOutlined />}
                  size="middle"
                  onSearch={handleSearch}
                  loading={loading}
                />
              </div>
            )}

            {!selectedFile ? (
              <Empty
                description="请从左侧选择一个日志文件"
                style={{ padding: '60px 0' }}
              />
            ) : logContent ? (
              <div>
                <div style={{ marginBottom: 12 }}>
                  <Space>
                    <Tag>总行数: {logContent.totalLines}</Tag>
                    <Tag>文件大小: {logContent.fileSizeStr}</Tag>
                    <Tag>最后更新: {formatDateTime(logContent.lastModified)}</Tag>
                    {searchKeyword && (
                      <Tag color="green">搜索: {searchKeyword}</Tag>
                    )}
                  </Space>
                </div>
                <div
                  style={{
                    backgroundColor: '#1e1e1e',
                    color: '#d4d4d4',
                    padding: 16,
                    borderRadius: 4,
                    fontFamily: 'Consolas, Monaco, "Courier New", monospace',
                    fontSize: 13,
                    lineHeight: 1.6,
                    maxHeight: 'calc(100vh - 350px)',
                    overflowY: 'auto',
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-all',
                  }}
                >
                  {logContent.content || '日志文件为空'}
                </div>
              </div>
            ) : (
              <Empty description="加载中..." />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default LogViewerPage;
