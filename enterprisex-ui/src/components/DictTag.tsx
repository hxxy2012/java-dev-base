import React from 'react';
import { Tag, TagProps } from 'antd';

export interface DictTagProps extends Omit<TagProps, 'color'> {
  /**
   * 字典值
   */
  value: number | string;
  /**
   * 字典数据映射
   */
  dict: Record<number | string, string>;
  /**
   * 颜色映射
   */
  colorMap?: Record<number | string, string>;
}

/**
 * 字典标签组件
 * 根据字典值显示对应的标签文本和颜色
 */
const DictTag: React.FC<DictTagProps> = ({ value, dict, colorMap, ...restProps }) => {
  const text = dict[value] || value;
  const color = colorMap?.[value];

  return (
    <Tag color={color} {...restProps}>
      {text}
    </Tag>
  );
};

export default DictTag;
