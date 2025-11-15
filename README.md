# EnterpriseX Framework - 企业级开发框架

![EnterpriseX](https://img.shields.io/badge/EnterpriseX-v1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)
![React](https://img.shields.io/badge/React-18.2.0-61dafb.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 📖 项目简介

**EnterpriseX Framework** 是一个基于 **Spring Boot 3.2 + Spring Cloud + React 18 + Ant Design 5** 的企业级微服务开发框架，提供完整的 RBAC 权限系统、组织架构管理、微服务网关、分布式配置、服务治理等企业级基础功能。

### 🎯 项目定位

- **开箱即用** - 快速启动项目开发
- **生产就绪** - 直接用于生产环境
- **安全可靠** - 银行级安全设计
- **高性能** - 支持高并发
- **易扩展** - 模块化设计

### ✨ 核心特性

- 🔐 **完整的 RBAC 权限系统** - 用户、角色、菜单、部门、岗位五表设计
- 🌐 **微服务架构** - Spring Cloud Alibaba 全家桶
- 🔒 **JWT 认证** - Spring Security + JWT 无状态认证
- 📊 **数据权限** - 基于部门的数据权限控制（全部/自定义/本部门/本部门及以下/仅本人）
- 🚪 **统一网关** - Spring Cloud Gateway 统一入口、认证、限流
- 📝 **代码生成器** - 快速生成 CRUD 代码
- 📈 **系统监控** - Spring Boot Admin + Prometheus + Grafana
- 🔗 **链路追踪** - Micrometer Tracing + Zipkin
- 📦 **文件服务** - 支持本地存储、MinIO、OSS
- ⏰ **定时任务** - Quartz 分布式定时任务
- 📚 **接口文档** - Knife4j (Swagger3) 自动生成
- 🎨 **前端分离** - React 18 + TypeScript + Ant Design 5

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.1 | 核心框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2022.0.0.0 | 微服务组件 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.x | 缓存数据库 |
| Nacos | 2.3.x | 注册中心/配置中心 |
| Sentinel | - | 流量控制/熔断降级 |
| JWT | 0.12.3 | 令牌认证 |
| Knife4j | 4.4.0 | 接口文档 |
| Hutool | 5.8.25 | 工具类库 |
| Lombok | - | 代码简化 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| React | 18.2.0 | 前端框架 |
| TypeScript | 5.3.3 | 开发语言 |
| Ant Design | 5.12.8 | UI 组件库 |
| Vite | 5.0.10 | 构建工具 |
| React Router | 6.21.1 | 路由管理 |
| Zustand | 4.4.7 | 状态管理 |
| Axios | 1.6.5 | HTTP 客户端 |

## 📁 项目结构

```
enterprisex-framework/
├── enterprisex-common/              # 公共模块
│   ├── common-core/                 # 核心公共模块
│   ├── common-security/             # 安全公共模块
│   ├── common-redis/                # Redis 公共模块
│   ├── common-datasource/           # 数据源公共模块
│   ├── common-log/                  # 日志公共模块
│   ├── common-swagger/              # 接口文档公共模块
│   └── common-mybatis/              # MyBatis 增强公共模块
│
├── enterprisex-gateway/             # 网关服务 (8080)
│
├── enterprisex-auth/                # 认证授权服务 (9200)
│
├── enterprisex-modules/             # 业务模块
│   ├── enterprisex-system/          # 系统服务 (9201)
│   ├── enterprisex-file/            # 文件服务 (9300)
│   ├── enterprisex-job/             # 定时任务服务 (9400)
│   ├── enterprisex-message/         # 消息服务 (9500)
│   ├── enterprisex-monitor/         # 监控服务 (9600)
│   └── enterprisex-generator/       # 代码生成服务 (9700)
│
├── enterprisex-api/                 # API 模块
│   ├── api-system/                  # 系统服务 API
│   ├── api-file/                    # 文件服务 API
│   └── api-message/                 # 消息服务 API
│
├── enterprisex-ui/                  # 前端项目
│   ├── src/
│   │   ├── api/                     # API 接口
│   │   ├── assets/                  # 静态资源
│   │   ├── components/              # 公共组件
│   │   ├── layouts/                 # 布局组件
│   │   ├── pages/                   # 页面
│   │   ├── router/                  # 路由
│   │   ├── store/                   # 状态管理
│   │   ├── types/                   # 类型定义
│   │   └── utils/                   # 工具类
│   ├── package.json
│   └── vite.config.ts
│
├── sql/                             # SQL 脚本
│   ├── enterprisex_schema.sql       # 数据库结构
│   └── enterprisex_data.sql         # 初始化数据
│
├── docker/                          # Docker 配置
├── docker-compose.yml               # Docker Compose 配置
├── pom.xml                          # 父 POM
└── README.md                        # 项目说明
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.8+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Nacos**: 2.3+

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/enterprisex-framework.git
cd enterprisex-framework
```

### 2. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行 SQL 脚本
source sql/enterprisex_schema.sql
source sql/enterprisex_data.sql
```

默认创建数据库 `enterprisex`，包含完整的 RBAC 权限表和初始化数据。

**默认管理员账号**：
- 用户名：`admin`
- 密码：`admin123`

### 3. 启动基础服务（使用 Docker Compose）

```bash
# 启动 MySQL、Redis、Nacos、MinIO
docker-compose up -d mysql redis nacos minio

# 查看服务状态
docker-compose ps

# 访问 Nacos 控制台
# http://localhost:8848/nacos
# 用户名/密码：nacos/nacos
```

### 4. 启动后端服务

```bash
# 编译项目
mvn clean install -DskipTests

# 启动系统服务
cd enterprisex-modules/enterprisex-system
mvn spring-boot:run

# 系统服务启动成功后，访问 API 文档
# http://localhost:9201/doc.html
```

**服务端口说明**：
- 网关服务：`8080`
- 认证服务：`9200`
- 系统服务：`9201`
- 文件服务：`9300`
- 定时任务服务：`9400`
- 消息服务：`9500`
- 监控服务：`9600`
- 代码生成服务：`9700`

### 5. 启动前端项目

```bash
cd enterprisex-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问前端项目
# http://localhost:3000
```

## 📊 数据库设计

### RBAC 权限模型（五表设计）

```sql
sys_user          -- 用户表
sys_role          -- 角色表
sys_menu          -- 菜单权限表
sys_user_role     -- 用户角色关联表
sys_role_menu     -- 角色菜单关联表
```

### 组织架构

```sql
sys_dept          -- 部门表
sys_post          -- 岗位表
sys_user_post     -- 用户岗位关联表
sys_role_dept     -- 角色部门关联表（数据权限）
```

### 系统管理

```sql
sys_dict_type     -- 字典类型表
sys_dict_data     -- 字典数据表
sys_config        -- 参数配置表
sys_notice        -- 通知公告表
```

### 日志管理

```sql
sys_oper_log      -- 操作日志表
sys_login_log     -- 登录日志表
```

### 定时任务

```sql
sys_job           -- 定时任务表
sys_job_log       -- 定时任务日志表
```

### 文件管理

```sql
sys_file          -- 文件信息表
```

## 🔐 权限设计

### 菜单权限

- **目录（M）**：导航菜单
- **菜单（C）**：页面路由
- **按钮（F）**：操作权限

示例：
```
系统管理（M）
  ├── 用户管理（C）
  │   ├── 用户查询（F）system:user:query
  │   ├── 用户新增（F）system:user:add
  │   ├── 用户修改（F）system:user:edit
  │   └── 用户删除（F）system:user:remove
  └── 角色管理（C）
      ├── 角色查询（F）system:role:query
      └── ...
```

### 数据权限

基于部门的数据权限控制：

1. **全部数据权限** - 可查看所有部门数据
2. **自定义数据权限** - 可查看指定部门数据
3. **本部门数据权限** - 只能查看本部门数据
4. **本部门及以下数据权限** - 可查看本部门及子部门数据
5. **仅本人数据权限** - 只能查看自己创建的数据

## 🛠️ 开发指南

### 后端开发规范

1. **代码规范**：遵循阿里巴巴 Java 开发规范
2. **命名规范**：
   - Controller：`SysUserController`
   - Service：`ISysUserService` / `SysUserServiceImpl`
   - Mapper：`SysUserMapper`
   - Entity：`SysUser`
3. **包结构**：
   ```
   com.enterprisex.system
   ├── controller    # 控制层
   ├── service       # 服务层
   ├── mapper        # 数据访问层
   ├── domain        # 实体类
   └── config        # 配置类
   ```

### 前端开发规范

1. **代码规范**：遵循 Airbnb React 规范
2. **命名规范**：
   - 组件：`UserManage.tsx`（大驼峰）
   - 文件夹：`user`（小写）
   - API：`getUserList`（小驼峰）
3. **目录结构**：
   ```
   src/
   ├── api/          # API 接口
   ├── components/   # 公共组件
   ├── pages/        # 页面组件
   ├── utils/        # 工具函数
   └── types/        # 类型定义
   ```

### 新增功能模块

1. **后端**：
   ```bash
   # 使用代码生成器生成基础代码
   # 访问 http://localhost:9700
   ```

2. **前端**：
   ```bash
   # 创建页面组件
   mkdir -p src/pages/system/xxx
   touch src/pages/system/xxx/index.tsx

   # 创建 API 接口
   touch src/api/system/xxx.ts
   ```

## 📦 部署指南

### Docker 部署（推荐）

```bash
# 1. 构建后端镜像
mvn clean package -DskipTests
docker build -t enterprisex-system:1.0.0 -f Dockerfile .

# 2. 构建前端镜像
cd enterprisex-ui
npm run build
docker build -t enterprisex-ui:1.0.0 -f Dockerfile .

# 3. 启动所有服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f
```

### 传统部署

#### 后端部署

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 上传 jar 包到服务器
scp target/enterprisex-system.jar user@server:/app/

# 3. 启动服务
java -jar enterprisex-system.jar \
  --spring.profiles.active=prod \
  --server.port=9201
```

#### 前端部署

```bash
# 1. 构建
npm run build

# 2. 上传 dist 目录到 Nginx
scp -r dist/* user@server:/usr/share/nginx/html/

# 3. 配置 Nginx
# 反向代理后端 API
location /api {
    proxy_pass http://localhost:8080;
}
```

## 🔧 配置说明

### application.yml 核心配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprisex
    username: root
    password: root

  data:
    redis:
      host: localhost
      port: 6379

  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

jwt:
  secret: your-secret-key
  expiration: 7200              # 2小时
  refresh-expiration: 604800    # 7天
```

## 📚 接口文档

启动项目后访问 Knife4j 文档：

```
http://localhost:9201/doc.html
```

## 🤝 参与贡献

我们欢迎所有形式的贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 开源协议

本项目基于 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 💬 联系方式

- **项目主页**：https://github.com/your-repo/enterprisex-framework
- **问题反馈**：https://github.com/your-repo/enterprisex-framework/issues
- **技术交流群**：[加入我们的技术交流群]

## 🙏 鸣谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [React](https://react.dev/)
- [Ant Design](https://ant.design/)
- [MyBatis-Plus](https://baomidou.com/)
- [Hutool](https://hutool.cn/)

---

**⭐ 如果这个项目对你有帮助，请给个 Star！**

Copyright © 2024 EnterpriseX. All rights reserved.
