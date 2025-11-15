# EnterpriseX Framework - 开发指南

## 快速上手

### 1. 开发环境准备

#### 必需工具
```bash
# 检查 Java 版本（需要 17+）
java -version

# 检查 Maven 版本（需要 3.8+）
mvn -v

# 检查 Node.js 版本（需要 18+）
node -v
npm -v
```

#### 启动基础服务

使用 Docker Compose 快速启动所有基础服务：

```bash
# 启动 MySQL、Redis、Nacos、MinIO
docker-compose up -d

# 等待服务启动完成（约 30 秒）
docker-compose ps

# 查看 Nacos 是否正常
curl http://localhost:8848/nacos/
```

**服务访问地址**：
- Nacos：http://localhost:8848/nacos （nacos/nacos）
- MinIO：http://localhost:9001 （admin/admin123）

### 2. 初始化数据库

```bash
# 方法一：使用 Docker（推荐）
# Docker Compose 会自动执行 sql 目录下的 SQL 脚本

# 方法二：手动执行
mysql -u root -p < sql/enterprisex_schema.sql
mysql -u root -p < sql/enterprisex_data.sql
```

验证数据库：
```bash
mysql -u root -p

mysql> USE enterprisex;
mysql> SHOW TABLES;
mysql> SELECT * FROM sys_user;
```

### 3. 启动后端服务

```bash
# 编译整个项目
mvn clean install -DskipTests

# 启动系统服务
cd enterprisex-modules/enterprisex-system
mvn spring-boot:run

# 或者使用 IDEA 直接运行 SystemApplication.java
```

**验证后端服务**：
```bash
# 访问健康检查端点
curl http://localhost:9201/actuator/health

# 访问 API 文档
open http://localhost:9201/doc.html
```

### 4. 启动前端服务

```bash
cd enterprisex-ui

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev

# 浏览器自动打开 http://localhost:3000
```

**默认登录账号**：
- 用户名：`admin`
- 密码：`admin123`

## 核心功能开发

### 后端开发示例

#### 创建实体类

```java
package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.enterprisex.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_example")
public class SysExample extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer status;

    @TableLogic
    private Integer delFlag;
}
```

#### 创建 Mapper

```java
package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysExample;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysExampleMapper extends BaseMapper<SysExample> {
}
```

#### 创建 Service

```java
package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysExample;

public interface ISysExampleService extends IService<SysExample> {
}
```

```java
package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.system.domain.SysExample;
import com.enterprisex.system.mapper.SysExampleMapper;
import com.enterprisex.system.service.ISysExampleService;
import org.springframework.stereotype.Service;

@Service
public class SysExampleServiceImpl
    extends ServiceImpl<SysExampleMapper, SysExample>
    implements ISysExampleService {
}
```

#### 创建 Controller

```java
package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysExample;
import com.enterprisex.system.service.ISysExampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "示例管理")
@RestController
@RequestMapping("/system/example")
public class SysExampleController {

    @Autowired
    private ISysExampleService exampleService;

    @Operation(summary = "查询列表")
    @GetMapping("/list")
    public TableDataInfo<SysExample> list(SysExample example) {
        List<SysExample> list = exampleService.list();
        return TableDataInfo.ok(list, list.size());
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public R<SysExample> getInfo(@PathVariable Long id) {
        return R.ok(exampleService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public R<Void> add(@RequestBody SysExample example) {
        return R.toAjax(exampleService.save(example));
    }

    @Operation(summary = "修改")
    @PutMapping
    public R<Void> edit(@RequestBody SysExample example) {
        return R.toAjax(exampleService.updateById(example));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return R.toAjax(exampleService.removeBatchByIds(Arrays.asList(ids)));
    }
}
```

### 前端开发示例

#### 创建 API 接口

```typescript
// src/api/system/example.ts
import { get, post, put, del } from '@/utils/request';

export interface Example {
  id?: number;
  name: string;
  status: number;
}

// 查询列表
export const getExampleList = (params: any) => {
  return get('/system/example/list', params);
};

// 获取详情
export const getExample = (id: number) => {
  return get(`/system/example/${id}`);
};

// 新增
export const addExample = (data: Example) => {
  return post('/system/example', data);
};

// 修改
export const updateExample = (data: Example) => {
  return put('/system/example', data);
};

// 删除
export const deleteExample = (ids: number[]) => {
  return del(`/system/example/${ids.join(',')}`);
};
```

#### 创建页面组件

```typescript
// src/pages/system/example/index.tsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getExampleList, addExample, updateExample, deleteExample, Example } from '@/api/system/example';

const ExampleManage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Example[]>([]);
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();

  // 加载数据
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getExampleList({});
      setDataSource(res.data.rows);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // 新增/编辑
  const handleSubmit = async (values: Example) => {
    try {
      if (values.id) {
        await updateExample(values);
        message.success('修改成功');
      } else {
        await addExample(values);
        message.success('新增成功');
      }
      setVisible(false);
      loadData();
    } catch (error) {
      console.error(error);
    }
  };

  // 删除
  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除？',
      onOk: async () => {
        await deleteExample([id]);
        message.success('删除成功');
        loadData();
      },
    });
  };

  const columns = [
    { title: 'ID', dataIndex: 'id' },
    { title: '名称', dataIndex: 'name' },
    { title: '状态', dataIndex: 'status' },
    {
      title: '操作',
      render: (_: any, record: Example) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => {
            form.setFieldsValue(record);
            setVisible(true);
          }}>
            编辑
          </Button>
          <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record.id!)}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => {
          form.resetFields();
          setVisible(true);
        }}>
          新增
        </Button>
      </Space>

      <Table
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="id"
      />

      <Modal
        title={form.getFieldValue('id') ? '编辑' : '新增'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} onFinish={handleSubmit}>
          <Form.Item name="id" hidden>
            <Input />
          </Form.Item>
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]}>
            <Input type="number" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ExampleManage;
```

## 常见问题

### 1. 端口冲突

如果端口被占用，修改 `application.yml`：

```yaml
server:
  port: 9202  # 修改为其他端口
```

### 2. 数据库连接失败

检查 MySQL 是否启动：
```bash
docker-compose ps mysql
```

检查数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprisex?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
```

### 3. Redis 连接失败

检查 Redis 是否启动：
```bash
docker-compose ps redis
```

### 4. Nacos 连接失败

检查 Nacos 是否启动：
```bash
curl http://localhost:8848/nacos/
```

### 5. 前端跨域问题

Vite 已配置代理，如果仍有问题，检查 `vite.config.ts`：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:9201',
      changeOrigin: true,
    },
  },
}
```

## 代码提交规范

使用 Conventional Commits 规范：

```bash
# 新功能
git commit -m "feat: 添加用户管理模块"

# 修复 bug
git commit -m "fix: 修复登录验证码不显示问题"

# 文档更新
git commit -m "docs: 更新 README"

# 代码重构
git commit -m "refactor: 重构权限验证逻辑"

# 性能优化
git commit -m "perf: 优化用户列表查询性能"

# 测试
git commit -m "test: 添加用户服务单元测试"
```

## 下一步

- 阅读 [API 文档](http://localhost:9201/doc.html)
- 查看 [在线演示](https://demo.enterprisex.com)
- 加入 [技术交流群](#)

---

Happy Coding! 🚀
