import React, { useState, useEffect } from 'react';
import {
  Card,
  List,
  Tag,
  Space,
  Button,
  Badge,
  Modal,
  Tabs,
  Empty,
  message as antdMessage,
  Popconfirm,
} from 'antd';
import {
  BellOutlined,
  DeleteOutlined,
  CheckOutlined,
  ExclamationCircleOutlined,
  NotificationOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import {
  getMessageList,
  markAsRead,
  markAllAsRead,
  deleteMessage,
  type Message,
} from '@/api/system/message';
import { formatDateTime } from '@/utils/date';

const { TabPane } = Tabs;

const MessagePage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [allMessages, setAllMessages] = useState<Message[]>([]);
  const [unreadMessages, setUnreadMessages] = useState<Message[]>([]);
  const [readMessages, setReadMessages] = useState<Message[]>([]);
  const [selectedTab, setSelectedTab] = useState('all');
  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedMessage, setSelectedMessage] = useState<Message | null>(null);

  // 加载消息列表
  const loadMessages = async () => {
    setLoading(true);
    try {
      const response = await getMessageList();
      const messages = response.rows || [];
      setAllMessages(messages);
      setUnreadMessages(messages.filter((m: Message) => m.isRead === 0));
      setReadMessages(messages.filter((m: Message) => m.isRead === 1));
    } catch (error) {
      antdMessage.error('加载消息失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMessages();
  }, []);

  // 查看消息详情
  const handleViewDetail = async (msg: Message) => {
    setSelectedMessage(msg);
    setDetailVisible(true);

    // 如果是未读消息，标记为已读
    if (msg.isRead === 0 && msg.messageId) {
      try {
        await markAsRead(msg.messageId);
        loadMessages(); // 重新加载消息列表
      } catch (error) {
        console.error('标记已读失败', error);
      }
    }
  };

  // 全部标记为已读
  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      antdMessage.success('已全部标记为已读');
      loadMessages();
    } catch (error) {
      antdMessage.error('操作失败');
    }
  };

  // 删除消息
  const handleDelete = async (messageId: number) => {
    try {
      await deleteMessage([messageId]);
      antdMessage.success('删除成功');
      loadMessages();
    } catch (error) {
      antdMessage.error('删除失败');
    }
  };

  // 获取消息类型标签
  const getMessageTypeTag = (type?: number) => {
    const typeMap: Record<number, { text: string; color: string; icon: React.ReactNode }> = {
      1: { text: '系统消息', color: 'blue', icon: <BellOutlined /> },
      2: { text: '通知消息', color: 'green', icon: <NotificationOutlined /> },
      3: { text: '待办消息', color: 'orange', icon: <FileTextOutlined /> },
    };
    const config = typeMap[type || 1];
    return (
      <Tag color={config.color} icon={config.icon}>
        {config.text}
      </Tag>
    );
  };

  // 获取消息级别标签
  const getLevelTag = (level?: number) => {
    const levelMap: Record<number, { text: string; color: string }> = {
      1: { text: '普通', color: 'default' },
      2: { text: '重要', color: 'warning' },
      3: { text: '紧急', color: 'error' },
    };
    const config = levelMap[level || 1];
    return <Badge status={config.color as any} text={config.text} />;
  };

  // 渲染消息列表
  const renderMessageList = (messages: Message[]) => {
    if (messages.length === 0) {
      return <Empty description="暂无消息" />;
    }

    return (
      <List
        dataSource={messages}
        renderItem={(item) => (
          <List.Item
            key={item.messageId}
            style={{
              padding: '16px',
              backgroundColor: item.isRead === 0 ? '#f0f9ff' : 'transparent',
              borderLeft: item.isRead === 0 ? '3px solid #1890ff' : 'none',
              cursor: 'pointer',
            }}
            onClick={() => handleViewDetail(item)}
            actions={[
              <Popconfirm
                title="确定删除此消息吗？"
                onConfirm={(e) => {
                  e?.stopPropagation();
                  handleDelete(item.messageId!);
                }}
                onCancel={(e) => e?.stopPropagation()}
              >
                <Button
                  type="link"
                  danger
                  size="small"
                  icon={<DeleteOutlined />}
                  onClick={(e) => e.stopPropagation()}
                >
                  删除
                </Button>
              </Popconfirm>,
            ]}
          >
            <List.Item.Meta
              avatar={
                item.isRead === 0 ? (
                  <Badge dot>
                    <BellOutlined style={{ fontSize: 24, color: '#1890ff' }} />
                  </Badge>
                ) : (
                  <BellOutlined style={{ fontSize: 24, color: '#d9d9d9' }} />
                )
              }
              title={
                <Space>
                  <span style={{ fontWeight: item.isRead === 0 ? 'bold' : 'normal' }}>
                    {item.title}
                  </span>
                  {getMessageTypeTag(item.messageType)}
                  {getLevelTag(item.level)}
                </Space>
              }
              description={
                <Space direction="vertical" size={4} style={{ width: '100%' }}>
                  <div
                    style={{
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                      maxWidth: '600px',
                    }}
                  >
                    {item.content}
                  </div>
                  <Space size={16}>
                    <span style={{ fontSize: 12, color: '#999' }}>
                      发送人: {item.senderName || '系统'}
                    </span>
                    <span style={{ fontSize: 12, color: '#999' }}>
                      {formatDateTime(item.createTime)}
                    </span>
                  </Space>
                </Space>
              }
            />
          </List.Item>
        )}
      />
    );
  };

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={
          <Space>
            <BellOutlined />
            <span>消息中心</span>
            {unreadMessages.length > 0 && (
              <Badge count={unreadMessages.length} style={{ marginLeft: 8 }} />
            )}
          </Space>
        }
        extra={
          unreadMessages.length > 0 && (
            <Button
              type="primary"
              icon={<CheckOutlined />}
              onClick={handleMarkAllAsRead}
            >
              全部已读
            </Button>
          )
        }
      >
        <Tabs activeKey={selectedTab} onChange={setSelectedTab}>
          <TabPane
            tab={
              <span>
                全部消息
                <Badge count={allMessages.length} style={{ marginLeft: 8 }} showZero />
              </span>
            }
            key="all"
          >
            {renderMessageList(allMessages)}
          </TabPane>
          <TabPane
            tab={
              <span>
                未读消息
                <Badge count={unreadMessages.length} style={{ marginLeft: 8 }} />
              </span>
            }
            key="unread"
          >
            {renderMessageList(unreadMessages)}
          </TabPane>
          <TabPane
            tab={
              <span>
                已读消息
                <Badge count={readMessages.length} style={{ marginLeft: 8 }} showZero />
              </span>
            }
            key="read"
          >
            {renderMessageList(readMessages)}
          </TabPane>
        </Tabs>
      </Card>

      {/* 消息详情弹窗 */}
      <Modal
        title={
          <Space>
            {selectedMessage && getMessageTypeTag(selectedMessage.messageType)}
            <span>{selectedMessage?.title}</span>
          </Space>
        }
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
        ]}
        width={700}
      >
        {selectedMessage && (
          <div>
            <div style={{ marginBottom: 16 }}>
              <Space>
                {getLevelTag(selectedMessage.level)}
                <span style={{ color: '#999' }}>
                  发送人: {selectedMessage.senderName || '系统'}
                </span>
                <span style={{ color: '#999' }}>
                  {formatDateTime(selectedMessage.createTime)}
                </span>
              </Space>
            </div>
            <div
              style={{
                padding: 16,
                backgroundColor: '#f5f5f5',
                borderRadius: 4,
                whiteSpace: 'pre-wrap',
                lineHeight: 1.8,
              }}
            >
              {selectedMessage.content}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MessagePage;
