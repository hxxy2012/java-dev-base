import React, { useState } from 'react';
import { Select, Space } from 'antd';
import type { SelectProps } from 'antd';
import * as Icons from '@ant-design/icons';

const { Option } = Select;

export interface IconSelectProps extends Omit<SelectProps, 'options'> {
  /**
   * 选中的图标
   */
  value?: string;
  /**
   * 值变化回调
   */
  onChange?: (value: string) => void;
}

/**
 * 图标选择组件
 */
const IconSelect: React.FC<IconSelectProps> = ({ value, onChange, ...restProps }) => {
  const [searchText, setSearchText] = useState('');

  // 获取所有图标名称
  const iconNames = Object.keys(Icons).filter(
    (key) => key.endsWith('Outlined') || key.endsWith('Filled') || key.endsWith('TwoTone')
  );

  // 过滤图标
  const filteredIcons = searchText
    ? iconNames.filter((name) => name.toLowerCase().includes(searchText.toLowerCase()))
    : iconNames.slice(0, 100); // 限制显示数量

  // 渲染图标
  const renderIcon = (iconName: string) => {
    const IconComponent = (Icons as any)[iconName];
    if (!IconComponent) return null;
    return <IconComponent style={{ fontSize: '16px' }} />;
  };

  return (
    <Select
      showSearch
      value={value}
      onChange={onChange}
      placeholder="请选择图标"
      filterOption={false}
      onSearch={setSearchText}
      {...restProps}
    >
      {filteredIcons.map((iconName) => (
        <Option key={iconName} value={iconName}>
          <Space>
            {renderIcon(iconName)}
            <span>{iconName}</span>
          </Space>
        </Option>
      ))}
    </Select>
  );
};

export default IconSelect;
