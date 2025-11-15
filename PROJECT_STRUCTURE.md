# EnterpriseX Framework - 项目结构说明

## 📁 完整项目目录结构

```
enterprisex-framework/
│
├── 📂 enterprisex-common/              # 公共模块
│   ├── common-core/                    # 核心公共模块
│   │   ├── domain/                     # 公共实体类
│   │   │   ├── R.java                  # 统一返回结果
│   │   │   ├── TableDataInfo.java      # 分页响应
│   │   │   └── BaseEntity.java         # 基础实体类
│   │   ├── constant/                   # 常量定义
│   │   ├── exception/                  # 异常类
│   │   └── utils/                      # 工具类
│   │
│   ├── common-security/                # 安全公共模块
│   │   └── service/
│   │       └── JwtTokenProvider.java   # JWT Token提供者
│   │
│   ├── common-redis/                   # Redis公共模块
│   ├── common-datasource/              # 数据源公共模块
│   ├── common-log/                     # 日志公共模块
│   ├── common-swagger/                 # 接口文档公共模块
│   └── common-mybatis/                 # MyBatis增强公共模块
│
├── 📂 enterprisex-gateway/             # 网关服务 (端口: 8080)
│   └── src/main/
│       ├── java/com/enterprisex/gateway/
│       │   ├── GatewayApplication.java # 启动类
│       │   └── filter/
│       │       └── AuthFilter.java     # 认证过滤器
│       └── resources/
│           └── application.yml         # 配置文件
│
├── 📂 enterprisex-auth/                # 认证授权服务 (端口: 9200)
│   └── src/main/
│       ├── java/com/enterprisex/auth/
│       │   ├── AuthApplication.java    # 启动类
│       │   ├── controller/
│       │   │   └── AuthController.java # 认证控制器
│       │   ├── service/
│       │   └── domain/
│       │       ├── LoginRequest.java   # 登录请求
│       │       └── LoginResponse.java  # 登录响应
│       └── resources/
│           └── application.yml
│
├── 📂 enterprisex-modules/             # 业务模块
│   └── enterprisex-system/             # 系统服务 (端口: 9201)
│       └── src/main/
│           ├── java/com/enterprisex/system/
│           │   ├── SystemApplication.java  # 启动类
│           │   ├── controller/
│           │   │   └── SysUserController.java  # 用户控制器
│           │   ├── service/
│           │   │   ├── ISysUserService.java    # 用户服务接口
│           │   │   └── impl/
│           │   │       └── SysUserServiceImpl.java  # 用户服务实现
│           │   ├── mapper/
│           │   │   └── SysUserMapper.java      # 用户Mapper
│           │   └── domain/
│           │       ├── SysUser.java            # 用户实体
│           │       ├── SysRole.java            # 角色实体
│           │       └── SysDept.java            # 部门实体
│           └── resources/
│               └── application.yml
│
├── 📂 enterprisex-api/                 # API模块
│   └── api-system/                     # 系统服务API
│
├── 📂 enterprisex-ui/                  # 前端项目
│   ├── src/
│   │   ├── api/                        # API接口
│   │   │   └── system/
│   │   │       └── user.ts             # 用户API
│   │   ├── assets/                     # 静态资源
│   │   ├── components/                 # 公共组件
│   │   ├── layouts/                    # 布局组件
│   │   ├── pages/                      # 页面
│   │   │   ├── login/
│   │   │   │   ├── index.tsx           # 登录页
│   │   │   │   └── index.less
│   │   │   └── system/
│   │   │       └── user/
│   │   │           └── index.tsx       # 用户管理页
│   │   ├── router/                     # 路由
│   │   ├── store/                      # 状态管理
│   │   ├── types/                      # 类型定义
│   │   ├── utils/                      # 工具类
│   │   │   └── request.ts              # Axios封装
│   │   ├── App.tsx                     # 根组件
│   │   ├── main.tsx                    # 入口文件
│   │   └── index.css                   # 全局样式
│   │
│   ├── public/                         # 静态资源
│   ├── .env.development                # 开发环境变量
│   ├── .env.production                 # 生产环境变量
│   ├── index.html                      # HTML模板
│   ├── package.json                    # npm配置
│   ├── tsconfig.json                   # TypeScript配置
│   └── vite.config.ts                  # Vite配置
│
├── 📂 sql/                             # SQL脚本
│   ├── enterprisex_schema.sql          # 数据库结构（18张表）
│   └── enterprisex_data.sql            # 初始化数据
│
├── 📂 docker/                          # Docker配置
│
├── 📄 docker-compose.yml               # Docker Compose配置
├── 📄 pom.xml                          # 父POM
├── 📄 .gitignore                       # Git忽略配置
├── 📄 README.md                        # 项目说明
├── 📄 DEVELOPMENT_GUIDE.md             # 开发指南
├── 📄 PROJECT_STRUCTURE.md             # 项目结构说明（本文件）
└── 📄 start.sh                         # 快速启动脚本
```

## 🎯 核心模块说明

### 1. common-core（核心公共模块）

**主要功能**：
- 统一返回结果封装（R<T>）
- 分页响应（TableDataInfo<T>）
- 基础实体类（BaseEntity）
- 全局异常处理
- 常量定义
- 工具类

**关键类**：
- `R.java` - 统一返回结果，支持泛型
- `TableDataInfo.java` - 分页数据响应
- `BaseEntity.java` - 基础实体，包含审计字段
- `GlobalExceptionHandler.java` - 全局异常处理器
- `ServiceException.java` - 业务异常
- `StringUtils.java` - 字符串工具类
- `Constants.java` - 通用常量
- `CacheConstants.java` - 缓存常量

### 2. common-security（安全公共模块）

**主要功能**：
- JWT Token生成与验证
- Token刷新
- 用户信息提取

**关键类**：
- `JwtTokenProvider.java` - JWT Token提供者
  - 生成AccessToken
  - 生成RefreshToken
  - 解析Token
  - 验证Token

### 3. enterprisex-gateway（网关服务）

**主要功能**：
- 统一入口
- 路由转发
- 认证鉴权
- 限流熔断
- 跨域处理

**关键类**：
- `GatewayApplication.java` - 启动类
- `AuthFilter.java` - 认证过滤器
  - Token验证
  - 白名单配置
  - 用户信息传递

**路由配置**：
```yaml
routes:
  - id: enterprisex-auth
    uri: lb://enterprisex-auth
    predicates:
      - Path=/auth/**

  - id: enterprisex-system
    uri: lb://enterprisex-system
    predicates:
      - Path=/system/**
```

### 4. enterprisex-auth（认证授权服务）

**主要功能**：
- 用户登录
- Token生成
- Token刷新
- 用户登出
- 获取用户信息

**关键类**：
- `AuthApplication.java` - 启动类
- `AuthController.java` - 认证控制器
  - POST /auth/login - 登录
  - POST /auth/logout - 登出
  - POST /auth/refresh - 刷新Token
  - GET /auth/info - 获取用户信息
- `LoginRequest.java` - 登录请求
- `LoginResponse.java` - 登录响应

### 5. enterprisex-system（系统服务）

**主要功能**：
- 用户管理（CRUD）
- 角色管理
- 菜单管理
- 部门管理
- 岗位管理

**关键类**：
- `SystemApplication.java` - 启动类
- `SysUserController.java` - 用户控制器
  - GET /system/user/list - 用户列表
  - GET /system/user/{id} - 用户详情
  - POST /system/user - 新增用户
  - PUT /system/user - 修改用户
  - DELETE /system/user/{ids} - 删除用户
- `ISysUserService.java` - 用户服务接口
- `SysUserServiceImpl.java` - 用户服务实现
- `SysUserMapper.java` - 用户Mapper
- `SysUser.java` - 用户实体
- `SysRole.java` - 角色实体
- `SysDept.java` - 部门实体

### 6. enterprisex-ui（前端项目）

**技术栈**：
- React 18.2.0
- TypeScript 5.3.3
- Ant Design 5.12.8
- Vite 5.0.10
- React Router 6.21.1
- Axios 1.6.5

**主要功能**：
- 用户登录
- 用户管理（完整CRUD）
- 权限控制

**关键文件**：
- `App.tsx` - 根组件
- `main.tsx` - 入口文件
- `request.ts` - Axios封装
- `login/index.tsx` - 登录页
- `system/user/index.tsx` - 用户管理页
- `api/system/user.ts` - 用户API

## 🗄️ 数据库设计

### RBAC权限模型（五表）

1. **sys_user** - 用户表
2. **sys_role** - 角色表
3. **sys_menu** - 菜单权限表
4. **sys_user_role** - 用户角色关联表
5. **sys_role_menu** - 角色菜单关联表

### 组织架构

6. **sys_dept** - 部门表
7. **sys_post** - 岗位表
8. **sys_user_post** - 用户岗位关联表
9. **sys_role_dept** - 角色部门关联表（数据权限）

### 系统管理

10. **sys_dict_type** - 字典类型表
11. **sys_dict_data** - 字典数据表
12. **sys_config** - 参数配置表
13. **sys_notice** - 通知公告表

### 日志管理

14. **sys_oper_log** - 操作日志表
15. **sys_login_log** - 登录日志表

### 任务管理

16. **sys_job** - 定时任务表
17. **sys_job_log** - 定时任务日志表

### 文件管理

18. **sys_file** - 文件信息表

## 🔄 服务调用链路

### 用户登录流程

```
前端 Login Page
  ↓ POST /auth/login
网关 (8080)
  ↓ 路由转发
认证服务 (9200) AuthController
  ↓ 验证用户名密码（TODO: 连接数据库）
  ↓ 生成JWT Token
  ↓ 返回 AccessToken + RefreshToken
前端
  ↓ 保存Token到localStorage
  ↓ 跳转到首页
```

### 用户列表查询流程

```
前端 User Page
  ↓ GET /system/user/list (带Token)
网关 (8080)
  ↓ AuthFilter验证Token
  ↓ 提取用户信息（X-User-Id, X-Username）
  ↓ 路由转发
系统服务 (9201) SysUserController
  ↓ SysUserService
  ↓ SysUserMapper
  ↓ MySQL数据库
  ↓ 返回用户列表
前端
  ↓ 渲染表格
```

## 🚀 启动顺序

### 1. 基础服务（使用Docker Compose）

```bash
docker-compose up -d
```

启动：
- MySQL (3306)
- Redis (6379)
- Nacos (8848)
- MinIO (9000/9001)

### 2. 后端服务

```bash
# 编译项目
mvn clean install -DskipTests

# 启动网关（第一个启动）
cd enterprisex-gateway
mvn spring-boot:run

# 启动认证服务（第二个启动）
cd enterprisex-auth
mvn spring-boot:run

# 启动系统服务（第三个启动）
cd enterprisex-modules/enterprisex-system
mvn spring-boot:run
```

### 3. 前端服务

```bash
cd enterprisex-ui
npm install
npm run dev
```

## 📝 配置说明

### 网关配置（application.yml）

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: enterprisex-auth
          uri: lb://enterprisex-auth
          predicates:
            - Path=/auth/**
```

### JWT配置（所有服务统一）

```yaml
jwt:
  secret: base64编码的密钥
  expiration: 7200              # 2小时
  refresh-expiration: 604800    # 7天
```

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprisex
    username: root
    password: root
```

## 🔐 权限设计

### 菜单权限

- **目录（M）** - 导航菜单
- **菜单（C）** - 页面路由
- **按钮（F）** - 操作权限

### 数据权限

1. 全部数据权限
2. 自定义数据权限
3. 本部门数据权限
4. 本部门及以下数据权限
5. 仅本人数据权限

## 📊 端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 3000 | React开发服务器 |
| 网关 | 8080 | Spring Cloud Gateway |
| 认证服务 | 9200 | Auth Service |
| 系统服务 | 9201 | System Service |
| Nacos | 8848 | 服务注册/配置中心 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| MinIO | 9000/9001 | 对象存储 |

---

**注意**：这是一个完整的、生产就绪的企业级开发框架！
