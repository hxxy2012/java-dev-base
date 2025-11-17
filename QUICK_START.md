# EnterpriseX Framework - 快速开始指南

## 🚀 5分钟快速启动

本指南将帮助您在5分钟内启动EnterpriseX Framework并体验其功能。

## 📋 前置要求

在开始之前，请确保您的开发环境已安装以下工具：

- ✅ **JDK 17+** - Java开发环境
- ✅ **Maven 3.8+** - 项目构建工具
- ✅ **Node.js 16+** - 前端运行环境
- ✅ **Docker & Docker Compose** - 容器运行环境（推荐）
- ✅ **MySQL 8.0+** - 数据库（如不使用Docker）
- ✅ **Redis 7.x** - 缓存数据库（如不使用Docker）

## 🎯 启动步骤

### 方式一：使用Docker Compose（推荐）

这是最简单快速的启动方式，无需手动安装MySQL、Redis、Nacos等服务。

#### 1. 克隆项目
```bash
git clone https://github.com/your-org/java-dev-base.git
cd java-dev-base
```

#### 2. 启动基础服务
```bash
# 使用Docker Compose启动MySQL、Redis、Nacos、MinIO
docker-compose up -d

# 等待服务启动完成（约30秒）
docker-compose ps
```

#### 3. 初始化数据库
```bash
# 进入MySQL容器
docker exec -it enterprisex-mysql mysql -uroot -proot123456

# 执行SQL脚本
mysql> source /docker-entrypoint-initdb.d/enterprisex_schema.sql;
mysql> source /docker-entrypoint-initdb.d/enterprisex_data.sql;
mysql> exit;
```

#### 4. 启动后端服务

**启动网关服务**:
```bash
cd enterprisex-gateway
mvn spring-boot:run
```

**启动认证服务**:
```bash
cd enterprisex-auth
mvn spring-boot:run
```

**启动系统服务**:
```bash
cd enterprisex-modules/enterprisex-system
mvn spring-boot:run
```

#### 5. 启动前端项目
```bash
cd enterprisex-ui
npm install
npm run dev
```

#### 6. 访问系统
打开浏览器访问：http://localhost:3000

**默认登录账号**:
- 用户名: `admin`
- 密码: `admin123`

### 方式二：手动安装服务

如果您不想使用Docker，可以手动安装各项服务。

#### 1. 安装MySQL 8.0
```bash
# 创建数据库
CREATE DATABASE `enterprisex` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本
mysql -uroot -p enterprisex < sql/enterprisex_schema.sql
mysql -uroot -p enterprisex < sql/enterprisex_data.sql
```

#### 2. 安装Redis 7.x
```bash
# 启动Redis
redis-server
```

#### 3. 安装Nacos 2.3.x
```bash
# 下载Nacos
wget https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.tar.gz
tar -xvf nacos-server-2.3.0.tar.gz
cd nacos/bin

# 启动Nacos（单机模式）
sh startup.sh -m standalone
```

#### 4. 修改配置文件
根据您的实际环境，修改各服务的配置文件：

**enterprisex-gateway/src/main/resources/application.yml**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprisex
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
  cloud:
    nacos:
      server-addr: localhost:8848
```

其他服务类似修改。

#### 5. 启动服务
按照"方式一"中的步骤4-6启动后端和前端服务。

## 🎨 系统功能导览

登录成功后，您将看到以下主要功能模块：

### 1. Dashboard仪表盘 📊
- 查看系统实时统计数据
- 快速访问常用功能
- 查看最近登录和操作记录
- 用户状态分布统计

👉 [详细使用指南](./DASHBOARD_GUIDE.md)

### 2. 系统管理 🔧
- **用户管理**: 用户的增删改查、状态控制、密码重置
- **角色管理**: 角色权限配置、数据权限设置
- **菜单管理**: 树形菜单管理、权限标识配置
- **部门管理**: 组织架构树形管理
- **岗位管理**: 岗位信息维护
- **字典管理**: 字典类型和字典数据管理
- **参数设置**: 系统参数配置
- **通知公告**: 系统公告发布
- **消息中心**: 系统消息通知（NEW）
- **IP访问控制**: IP黑白名单管理（NEW）

### 3. 系统监控 📈
- **在线用户**: 查看当前在线用户、强制退出
- **定时任务**: Quartz定时任务管理
- **服务监控**: 服务器性能监控（CPU、内存、磁盘）
- **健康检查**: 数据库、Redis、磁盘、内存健康检查（NEW）
- **操作日志**: 系统操作审计日志
- **登录日志**: 用户登录记录追踪

### 4. 系统工具 🛠️
- **代码生成**: 快速生成CRUD代码
- **系统接口**: Knife4j接口文档
- **系统信息**: 查看系统运行信息
- **数据库管理**: 数据库表管理、SQL查询（NEW）
- **数据备份**: 数据库备份与恢复（NEW）
- **日志查看**: 系统日志文件查看（NEW）

## 📊 功能验证清单

启动成功后，您可以验证以下功能：

- [ ] 登录系统并进入Dashboard
- [ ] 查看用户管理列表
- [ ] 新增一个测试用户
- [ ] 修改用户信息
- [ ] 查看操作日志，确认操作记录已生成
- [ ] 访问系统接口文档（http://localhost:8080/doc.html）
- [ ] 查看服务器监控信息
- [ ] 执行数据库查询
- [ ] 查看系统健康检查状态

## 🔍 常见问题

### 1. 服务启动失败
**问题**: 服务启动时报错连接数据库失败
**解决**:
- 检查MySQL服务是否正常运行
- 确认配置文件中的数据库连接信息正确
- 检查防火墙是否阻止了数据库端口

### 2. 前端页面无法访问
**问题**: 访问http://localhost:3000显示"无法访问此网站"
**解决**:
- 确认前端服务已成功启动
- 检查终端输出是否有错误信息
- 尝试执行`npm install`重新安装依赖

### 3. 登录后页面空白
**问题**: 登录成功但页面显示空白
**解决**:
- 打开浏览器开发者工具查看控制台错误
- 检查后端服务是否都已正常启动
- 确认网关服务（8080端口）是否正常运行

### 4. Nacos注册失败
**问题**: 服务启动日志显示Nacos注册失败
**解决**:
- 确认Nacos服务已启动（访问http://localhost:8848/nacos）
- 检查配置文件中的Nacos地址是否正确
- 检查网络连接是否正常

### 5. 数据库初始化失败
**问题**: 执行SQL脚本时报错
**解决**:
- 确认数据库字符集为utf8mb4
- 检查MySQL版本是否为8.0+
- 尝试手动创建数据库后再执行脚本

## 📞 获取帮助

如果遇到问题，可以通过以下方式获取帮助：

1. **查看文档**:
   - [功能清单](./FEATURES.md)
   - [开发指南](./DEVELOPMENT_GUIDE.md)
   - [项目结构](./PROJECT_STRUCTURE.md)
   - [API示例](./API_EXAMPLES.md)
   - [配置指南](./CONFIGURATION_GUIDE.md)

2. **提交Issue**:
   - GitHub Issues: https://github.com/your-org/java-dev-base/issues

3. **查看日志**:
   - 后端日志: `logs/`目录下
   - 前端控制台: 浏览器开发者工具

## 🎓 下一步学习

成功启动系统后，建议您：

1. **熟悉基础功能**: 浏览各个模块，了解系统提供的功能
2. **阅读开发文档**: 查看[开发指南](./DEVELOPMENT_GUIDE.md)学习如何开发新功能
3. **体验代码生成**: 使用代码生成器快速创建CRUD模块
4. **自定义配置**: 根据业务需求修改系统参数和菜单
5. **API调用**: 查看[API示例](./API_EXAMPLES.md)学习如何调用后端接口

## 🚀 生产部署

如需部署到生产环境，请参考：

1. **构建项目**:
```bash
# 后端打包
mvn clean package -DskipTests

# 前端打包
cd enterprisex-ui
npm run build
```

2. **部署建议**:
- 使用Nginx作为前端静态资源服务器和反向代理
- 使用Docker容器化部署各个微服务
- 配置Redis持久化和主从复制
- 使用MySQL主从复制保证数据安全
- 配置Nacos集群模式提高可用性
- 使用Sentinel进行流量控制和熔断降级

3. **监控运维**:
- 配置Prometheus采集服务指标
- 使用Grafana展示监控数据
- 配置Zipkin进行链路追踪
- 设置告警规则，及时发现问题

## 📝 许可证

本项目采用MIT许可证 - 详见[LICENSE](./LICENSE)文件

## 🤝 贡献

欢迎贡献代码、提出建议或报告问题！

---

**祝您使用愉快！** 🎉

如有任何问题，请随时联系我们。
