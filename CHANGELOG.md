# EnterpriseX Framework - 更新日志

## [Unreleased] - 2024

### Added - 新增功能

#### 在线用户统计
- ✅ **用户登录时存储在线信息到Redis**：记录userId、username、IP、userAgent、loginTime
- ✅ **用户登出时删除在线信息**：保持数据一致性
- ✅ **Dashboard实时展示在线用户数**：从Redis动态统计在线用户数量
- ✅ **在线用户信息自动过期**：TTL与Token过期时间一致，自动清理
- ✅ **新增AuthService.removeOnlineUser()**：删除在线用户信息
- ✅ **新增IDashboardService.getOnlineUserCount()**：获取在线用户数量

#### 数据权限拦截器框架
- ✅ **@DataScope注解**：用于标记需要数据权限控制的Mapper方法
- ✅ **DataScopeContext类**：数据权限上下文，存储用户权限信息
- ✅ **DataScopeInterceptor完善**：实现MyBatis拦截器框架
  - 从请求Header获取用户ID、部门ID、数据范围等信息
  - 识别@DataScope注解的Mapper方法
  - 超级管理员（userId=1）自动拥有全部数据权限
  - 提供5种数据权限范围的SQL条件构建方法
  - 支持自定义表别名（deptAlias、userAlias）
- ✅ **数据权限范围支持**：
  - 1-全部数据权限：不添加额外条件
  - 2-自定义数据权限：dept_id IN (指定部门列表)
  - 3-本部门数据权限：dept_id = 用户部门ID
  - 4-本部门及以下：dept_id IN (本部门及子部门)
  - 5-仅本人数据：create_by = 用户ID

#### 认证授权增强
- ✅ **Token黑名单机制**：用户登出时将token加入黑名单（存储在Redis中）
- ✅ **用户路由菜单查询**：根据用户角色权限动态返回路由菜单树
- ✅ **路由菜单树构建**：递归构建多级菜单结构，支持元信息（icon、title、hidden）
- ✅ **管理员权限特殊处理**：管理员（userId=1）返回所有菜单
- ✅ 新增RouterVo类：用于前端路由菜单数据传输

#### IP访问控制CIDR支持
- ✅ **新增IpUtils工具类**：提供IP地址处理和CIDR匹配功能
- ✅ **CIDR格式支持**：IP规则支持CIDR格式（如192.168.1.0/24）
- ✅ **CIDR验证**：验证CIDR格式的合法性
- ✅ **IP范围匹配**：精确判断IP是否在指定的CIDR网络范围内
- ✅ **IP地址转换**：IP字符串与long型整数互相转换

#### Dashboard性能优化
- ✅ 实现服务层抽象（IDashboardService接口）
- ✅ 添加Redis缓存支持（基础统计、用户状态统计）
- ✅ 缓存策略：稳定数据5分钟TTL，实时数据不缓存
- ✅ 重构Controller层，分离业务逻辑到Service层
- ✅ 优化代码结构和可维护性

#### SecurityUtils工具类
- 新增`SecurityUtils`工具类，统一管理用户上下文信息
- 支持从请求头获取当前登录用户ID和用户名
- 提供`isAuthenticated()`方法检查登录状态
- 提供`isCurrentUser(userId)`方法验证用户身份

#### Dashboard仪表盘增强
- ✅ 实现真实数据统计（替换模拟数据）
- ✅ 基础统计：用户、角色、部门、今日登录、今日操作数量
- ✅ 最近登录记录：显示最近5条登录日志
- ✅ 最近操作记录：显示最近5条操作日志
- ✅ 用户状态统计：正常/禁用用户数量和占比
- ✅ 近7天登录趋势：每日登录次数统计
- ✅ 未读消息统计：集成消息服务
- ✅ 快速访问链接：8个常用功能快速入口
- ✅ 响应式布局设计

#### 密码重置功能
- ✅ 实现用户密码重置功能（管理员操作）
- ✅ 使用BCrypt加密算法
- ✅ 超级管理员密码保护（不允许重置）
- ✅ 支持自定义密码或使用默认密码（123456）
- ✅ 添加操作日志记录

#### 消息通知系统
- ✅ sys_message表：19张数据库表设计完成
- ✅ 消息类型：系统消息、通知消息、待办消息
- ✅ 消息级别：普通、重要、紧急
- ✅ 已读/未读状态管理
- ✅ 消息发送：指定用户发送、广播消息
- ✅ 3条示例消息数据

#### 文档完善
- ✅ **DASHBOARD_GUIDE.md**：Dashboard使用指南（详细功能说明、技术实现、性能优化建议）
- ✅ **QUICK_START.md**：快速开始指南（5分钟快速启动、常见问题解决）
- ✅ **CHANGELOG.md**：更新日志（本文件）
- ✅ 更新FEATURES.md：新增Dashboard模块说明、更新数据库表数量

### Changed - 功能改进

#### DashboardController优化
- 移除所有模拟数据，使用真实数据库查询
- 使用LocalDateTime进行精确的日期时间统计
- 使用MyBatis Plus的LambdaQueryWrapper确保类型安全
- 添加业务类型名称映射（新增/修改/删除等）
- 优化查询性能（LIMIT限制结果数量）
- 使用Stream API优化数据转换

#### SysMessageController完善
- 移除所有硬编码的用户ID（userId=1L）
- 使用SecurityUtils获取当前登录用户信息
- 所有方法添加登录状态检查
- 消息发送使用真实发送人信息
- 改进错误处理和提示信息

#### 代码质量提升
- 消除7个TODO标记
- 添加详细的JavaDoc注释
- 统一错误处理和返回格式
- 提升代码可读性和可维护性

### Security - 安全改进

- ✅ **Token黑名单机制**：防止已登出的token继续访问系统
- ✅ **Token过期时间管理**：黑名单token自动过期，避免Redis内存浪费
- ✅ **权限路由控制**：根据用户角色返回对应的菜单权限
- ✅ **IP访问控制增强**：支持CIDR格式，更灵活的网络访问控制
- ✅ 统一的用户身份验证机制（SecurityUtils）
- ✅ 密码BCrypt加密存储
- ✅ 超级管理员操作保护
- ✅ 登录状态验证
- ✅ 防止SQL注入（使用LambdaQueryWrapper）

### Performance - 性能优化

- ✅ Dashboard查询优化（LIMIT限制、索引使用）
- ✅ 日期范围查询优化（使用LocalDateTime精确查询）
- ✅ 减少不必要的数据库查询
- ✅ Stream API提升数据处理效率

### Fixed - Bug修复

- ✅ **修复logout功能缺失**：实现完整的登出逻辑（Token黑名单+日志记录）
- ✅ **修复getRouters返回空数组**：实现根据用户权限查询菜单的完整逻辑
- ✅ **修复IP访问控制不支持CIDR**：新增CIDR格式支持，完善IP匹配逻辑
- ✅ **完善数据权限拦截器**：实现用户权限识别和SQL条件构建框架
- ✅ **消除所有TODO标记**：完成AuthController、IpAccessController和DataScopeInterceptor中的待实现功能
- 修复Dashboard统计数据不准确的问题
- 修复未登录用户访问消息接口的安全隐患
- 修复密码重置功能缺失的问题
- 修复用户信息获取不统一的问题

## [1.0.0] - 初始版本

### 核心功能
- ✅ 完整的RBAC权限系统（用户、角色、菜单、部门、岗位）
- ✅ 微服务架构（网关、认证、系统服务）
- ✅ JWT无状态认证
- ✅ 数据权限控制（5种数据范围）
- ✅ 操作日志记录（AOP自动记录）
- ✅ 登录日志追踪
- ✅ 在线用户管理
- ✅ 定时任务管理（Quartz）
- ✅ 服务器性能监控
- ✅ 系统健康检查
- ✅ 代码生成器
- ✅ MinIO文件存储
- ✅ WebSocket实时通信
- ✅ Sentinel流量控制
- ✅ 数据库管理工具
- ✅ 数据备份与恢复
- ✅ Excel导入导出
- ✅ 系统日志查看器
- ✅ IP访问控制
- ✅ 系统消息通知

### 技术栈
- **后端**: Spring Boot 3.2 + Spring Cloud + MyBatis Plus + MySQL 8.0 + Redis 7.x
- **前端**: React 18 + TypeScript + Ant Design 5 + Vite
- **中间件**: Nacos + Sentinel + MinIO + Quartz
- **工具**: Docker Compose + Knife4j + EasyExcel

### 数据库
- 19张数据库表完整设计
- 初始化数据脚本
- 默认管理员账号（admin/admin123）

### 文档
- README.md - 项目说明
- FEATURES.md - 功能清单
- DEVELOPMENT_GUIDE.md - 开发指南
- PROJECT_STRUCTURE.md - 项目结构
- API_EXAMPLES.md - API示例
- CONFIGURATION_GUIDE.md - 配置指南
- DASHBOARD_GUIDE.md - Dashboard指南
- QUICK_START.md - 快速开始
- CHANGELOG.md - 更新日志

## 下一步计划

### 高优先级
- [x] 实现在线用户统计（从Redis Session获取）
- [x] 添加Dashboard数据缓存（提升性能）
- [ ] 实现实时数据推送（WebSocket）
- [ ] 添加数据导出功能（PDF、Excel）

### 中优先级
- [x] IP访问控制支持CIDR格式（如192.168.1.0/24）
- [ ] 添加系统参数热更新功能
- [ ] 实现用户在线状态展示
- [ ] 添加操作日志详细查询和分析

### 低优先级
- [ ] 集成Spring Boot Admin监控
- [ ] 添加Zipkin链路追踪
- [ ] 实现Seata分布式事务
- [ ] 集成Elasticsearch全文检索
- [ ] 添加Flowable工作流引擎

## 技术债务

- [ ] 优化慢查询（添加数据库索引）
- [ ] 添加单元测试和集成测试
- [ ] 完善异常处理机制
- [ ] 添加接口限流配置
- [ ] 实现配置文件加密

## 已知问题

1. **在线用户数统计**：目前返回0，需要从Redis Session获取真实在线用户数
2. **Dashboard刷新**：需要实现自动刷新或WebSocket推送机制
3. **文件上传大小**：需要配置Nginx和Spring Boot的文件上传大小限制

## 贡献指南

欢迎提交Issue和Pull Request！

- Issue模板：Bug Report、Feature Request
- PR规范：功能分支、详细说明、单元测试
- 代码规范：遵循阿里巴巴Java开发规范

## 许可证

MIT License - 详见LICENSE文件

---

**更新时间**: 2024年
**维护者**: EnterpriseX Team
**联系方式**: enterprise-x@example.com
