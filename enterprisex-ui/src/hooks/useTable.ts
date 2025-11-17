import { useState } from 'react';
import { PAGE_SIZE } from '@/constants';

export interface TablePaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

export interface UseTableOptions {
  defaultPageSize?: number;
  defaultCurrent?: number;
}

/**
 * 表格管理Hook
 */
export const useTable = (options?: UseTableOptions) => {
  const { defaultPageSize = PAGE_SIZE, defaultCurrent = 1 } = options || {};

  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: defaultCurrent,
    pageSize: defaultPageSize,
    total: 0,
  });

  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  /**
   * 设置总数
   */
  const setTotal = (total: number) => {
    setPagination((prev) => ({ ...prev, total }));
  };

  /**
   * 重置分页
   */
  const resetPagination = () => {
    setPagination({
      current: defaultCurrent,
      pageSize: defaultPageSize,
      total: 0,
    });
  };

  /**
   * 页码变化处理
   */
  const handlePageChange = (page: number, pageSize?: number) => {
    setPagination((prev) => ({
      ...prev,
      current: page,
      pageSize: pageSize || prev.pageSize,
    }));
  };

  /**
   * 清空选中项
   */
  const clearSelection = () => {
    setSelectedRowKeys([]);
  };

  /**
   * 行选择配置
   */
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => {
      setSelectedRowKeys(keys);
    },
  };

  return {
    pagination,
    setPagination,
    setTotal,
    resetPagination,
    handlePageChange,
    selectedRowKeys,
    setSelectedRowKeys,
    clearSelection,
    rowSelection,
  };
};
