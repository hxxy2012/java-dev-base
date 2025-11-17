import React from 'react';
import { TreeSelect, TreeSelectProps } from 'antd';

export interface DeptTreeNode {
  deptId: number;
  deptName: string;
  parentId?: number;
  children?: DeptTreeNode[];
}

export interface DeptTreeSelectProps extends Omit<TreeSelectProps, 'treeData'> {
  /**
   * 部门树数据
   */
  deptTree: DeptTreeNode[];
  /**
   * 当前编辑的部门ID（用于过滤掉自己和子部门）
   */
  currentDeptId?: number;
}

/**
 * 部门树选择组件
 */
const DeptTreeSelect: React.FC<DeptTreeSelectProps> = ({
  deptTree,
  currentDeptId,
  ...restProps
}) => {
  /**
   * 过滤掉当前部门及其子部门
   */
  const filterTree = (tree: DeptTreeNode[], excludeId?: number): DeptTreeNode[] => {
    if (!excludeId) return tree;

    return tree
      .filter((node) => node.deptId !== excludeId)
      .map((node) => ({
        ...node,
        children: node.children ? filterTree(node.children, excludeId) : undefined,
      }));
  };

  /**
   * 转换为TreeSelect需要的数据格式
   */
  const convertToTreeData = (tree: DeptTreeNode[]): any[] => {
    return tree.map((node) => ({
      title: node.deptName,
      value: node.deptId,
      key: node.deptId,
      children: node.children ? convertToTreeData(node.children) : undefined,
    }));
  };

  const filteredTree = filterTree(deptTree, currentDeptId);
  const treeData = convertToTreeData(filteredTree);

  return (
    <TreeSelect
      showSearch
      treeDefaultExpandAll
      placeholder="请选择上级部门"
      treeData={[
        {
          title: '顶级部门',
          value: 0,
          key: 0,
        },
        ...treeData,
      ]}
      treeNodeFilterProp="title"
      {...restProps}
    />
  );
};

export default DeptTreeSelect;
