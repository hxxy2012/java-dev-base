import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  message,
  Tabs,
  Descriptions,
  Modal,
  Input,
  Tag,
  Statistic,
  Row,
  Col,
} from 'antd';
import {
  DatabaseOutlined,
  ReloadOutlined,
  SearchOutlined,
  ThunderboltOutlined,
  BarChartOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  getTables,
  getTableInfo,
  executeQuery,
  getStatistics,
  optimizeTable,
  analyzeTable,
} from '@/api/tool/database';
import { formatFileSize } from '@/utils/common';

const { TabPane } = Tabs;
const { TextArea } = Input;

const DatabasePage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [tables, setTables] = useState<any[]>([]);
  const [statistics, setStatistics] = useState<any>(null);
  const [tableInfoVisible, setTableInfoVisible] = useState(false);
  const [currentTable, setCurrentTable] = useState<any>(null);
  const [queryResult, setQueryResult] = useState<any>(null);
  const [sqlText, setSqlText] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      await Promise.all([fetchTables(), fetchStatistics()]);
    } finally {
      setLoading(false);
    }
  };

  const fetchTables = async () => {
    try {
      const res = await getTables();
      setTables(res.data || []);
    } catch (error) {
      message.error('获取表列表失败');
    }
  };

  const fetchStatistics = async () => {
    try {
      const res = await getStatistics();
      setStatistics(res.data);
    } catch (error) {
      message.error('获取统计信息失败');
    }
  };

  const handleViewTable = async (record: any) => {
    try {
      const res = await getTableInfo(record.tableName);
      setCurrentTable(res.data);
      setTableInfoVisible(true);
    } catch (error) {
      message.error('获取表信息失败');
    }
  };

  const handleOptimize = async (tableName: string) => {
    try {
      await optimizeTable(tableName);
      message.success('表优化成功');
      fetchTables();
    } catch (error) {
      message.error('表优化失败');
    }
  };

  const handleAnalyze = async (tableName: string) => {
    try {
      await analyzeTable(tableName);
      message.success('表分析成功');
    } catch (error) {
      message.error('表分析失败');
    }
  };

  const handleExecuteQuery = async () => {
    if (!sqlText.trim()) {
      message.warning('请输入SQL语句');
      return;
    }

    try {
      setLoading(true);
      const res = await executeQuery(sqlText);
      setQueryResult(res.data);
      message.success('查询成功');
    } catch (error: any) {
      message.error(error.msg || '查询失败');
    } finally {
      setLoading(false);
    }
  };

  const tableColumns: ColumnsType<any> = [
    {
      title: '表名',
      dataIndex: 'tableName',
      key: 'tableName',
      width: 200,
    },
    {
      title: '表注释',
      dataIndex: 'tableComment',
      key: 'tableComment',
      ellipsis: true,
    },
    {
      title: '行数',
      dataIndex: 'tableRows',
      key: 'tableRows',
      width: 100,
      render: (val: number) => val?.toLocaleString(),
    },
    {
      title: '数据大小',
      dataIndex: 'dataLength',
      key: 'dataLength',
      width: 120,
      render: (val: number) => formatFileSize(val || 0),
    },
    {
      title: '索引大小',
      dataIndex: 'indexLength',
      key: 'indexLength',
      width: 120,
      render: (val: number) => formatFileSize(val || 0),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => handleViewTable(record)}>
            查看结构
          </Button>
          <Button type="link" size="small" onClick={() => handleOptimize(record.tableName)}>
            优化
          </Button>
          <Button type="link" size="small" onClick={() => handleAnalyze(record.tableName)}>
            分析
          </Button>
        </Space>
      ),
    },
  ];

  const columnColumns: ColumnsType<any> = [
    {
      title: '列名',
      dataIndex: 'columnName',
      key: 'columnName',
    },
    {
      title: '类型',
      dataIndex: 'columnType',
      key: 'columnType',
    },
    {
      title: '允许NULL',
      dataIndex: 'isNullable',
      key: 'isNullable',
      render: (val: string) => (
        <Tag color={val === 'YES' ? 'orange' : 'green'}>{val}</Tag>
      ),
    },
    {
      title: '键',
      dataIndex: 'columnKey',
      key: 'columnKey',
      render: (val: string) => (
        val ? <Tag color="blue">{val}</Tag> : '-'
      ),
    },
    {
      title: '默认值',
      dataIndex: 'columnDefault',
      key: 'columnDefault',
      render: (val: any) => val || '-',
    },
    {
      title: '额外',
      dataIndex: 'extra',
      key: 'extra',
      render: (val: string) => val || '-',
    },
    {
      title: '注释',
      dataIndex: 'columnComment',
      key: 'columnComment',
      ellipsis: true,
    },
  ];

  const queryColumns = queryResult?.data?.[0]
    ? Object.keys(queryResult.data[0]).map((key) => ({
        title: key,
        dataIndex: key,
        key,
        ellipsis: true,
      }))
    : [];

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <DatabaseOutlined />
            <span>数据库管理</span>
          </Space>
        }
        extra={
          <Button icon={<ReloadOutlined />} onClick={fetchData} loading={loading}>
            刷新
          </Button>
        }
      >
        <Tabs defaultActiveKey="1">
          <TabPane
            tab={
              <span>
                <BarChartOutlined />
                统计信息
              </span>
            }
            key="1"
          >
            {statistics && (
              <>
                <Row gutter={16} style={{ marginBottom: 24 }}>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="数据库"
                        value={statistics.database}
                        valueStyle={{ color: '#3f8600' }}
                      />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="表数量"
                        value={statistics.tableCount}
                        suffix="个"
                      />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="总行数"
                        value={statistics.totalRows}
                        suffix="行"
                      />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="数据库版本"
                        value={statistics.version}
                        valueStyle={{ fontSize: 16 }}
                      />
                    </Card>
                  </Col>
                </Row>

                <Row gutter={16}>
                  <Col span={8}>
                    <Card>
                      <Statistic
                        title="总大小"
                        value={formatFileSize(statistics.size?.totalSize || 0)}
                        valueStyle={{ color: '#1890ff' }}
                      />
                    </Card>
                  </Col>
                  <Col span={8}>
                    <Card>
                      <Statistic
                        title="数据大小"
                        value={formatFileSize(statistics.size?.dataSize || 0)}
                      />
                    </Card>
                  </Col>
                  <Col span={8}>
                    <Card>
                      <Statistic
                        title="索引大小"
                        value={formatFileSize(statistics.size?.indexSize || 0)}
                      />
                    </Card>
                  </Col>
                </Row>
              </>
            )}
          </TabPane>

          <TabPane
            tab={
              <span>
                <DatabaseOutlined />
                表列表
              </span>
            }
            key="2"
          >
            <Table
              columns={tableColumns}
              dataSource={tables}
              rowKey="tableName"
              loading={loading}
              scroll={{ x: 1200 }}
              pagination={{
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条`,
              }}
            />
          </TabPane>

          <TabPane
            tab={
              <span>
                <SearchOutlined />
                SQL查询
              </span>
            }
            key="3"
          >
            <Space direction="vertical" style={{ width: '100%' }} size="large">
              <div>
                <TextArea
                  rows={6}
                  placeholder="请输入SQL查询语句（仅支持SELECT语句）"
                  value={sqlText}
                  onChange={(e) => setSqlText(e.target.value)}
                />
                <Button
                  type="primary"
                  icon={<ThunderboltOutlined />}
                  onClick={handleExecuteQuery}
                  loading={loading}
                  style={{ marginTop: 16 }}
                >
                  执行查询
                </Button>
              </div>

              {queryResult && (
                <Card
                  title={`查询结果 (${queryResult.rowCount} 行, 耗时: ${queryResult.executionTime})`}
                  size="small"
                >
                  <Table
                    columns={queryColumns}
                    dataSource={queryResult.data}
                    rowKey={(_, index) => String(index)}
                    scroll={{ x: true }}
                    pagination={{
                      pageSize: 20,
                      showTotal: (total) => `共 ${total} 行`,
                    }}
                  />
                </Card>
              )}
            </Space>
          </TabPane>
        </Tabs>
      </Card>

      <Modal
        title={`表结构 - ${currentTable?.tableInfo?.tableName}`}
        open={tableInfoVisible}
        onCancel={() => setTableInfoVisible(false)}
        footer={null}
        width={1000}
      >
        {currentTable && (
          <>
            <Descriptions bordered column={2} style={{ marginBottom: 16 }}>
              <Descriptions.Item label="表名">
                {currentTable.tableInfo?.tableName}
              </Descriptions.Item>
              <Descriptions.Item label="表注释">
                {currentTable.tableInfo?.tableComment}
              </Descriptions.Item>
              <Descriptions.Item label="引擎">
                {currentTable.tableInfo?.engine}
              </Descriptions.Item>
              <Descriptions.Item label="字符集">
                {currentTable.tableInfo?.collation}
              </Descriptions.Item>
              <Descriptions.Item label="行数">
                {currentTable.tableInfo?.tableRows?.toLocaleString()}
              </Descriptions.Item>
              <Descriptions.Item label="数据大小">
                {formatFileSize(currentTable.tableInfo?.dataLength || 0)}
              </Descriptions.Item>
            </Descriptions>

            <Table
              columns={columnColumns}
              dataSource={currentTable.columns}
              rowKey="columnName"
              pagination={false}
              size="small"
            />
          </>
        )}
      </Modal>
    </div>
  );
};

export default DatabasePage;
