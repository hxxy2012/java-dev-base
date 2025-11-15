# EnterpriseX Framework - 功能清单

## 📋 已实现功能列表

### 1. 🏗️ 基础架构

#### 1.1 微服务架构
- [x] Spring Cloud Gateway 网关服务
- [x] Nacos 服务注册与发现
- [x] Nacos 配置中心
- [x] Spring Cloud LoadBalancer 负载均衡
- [x] 微服务间调用（HTTP）

#### 1.2 公共模块
- [x] common-core - 核心公共模块
  - [x] 统一返回结果（R<T>）
  - [x] 分页响应（TableDataInfo<T>）
  - [x] 基础实体类（BaseEntity）
  - [x] 全局异常处理（GlobalExceptionHandler）
  - [x] 业务异常（ServiceException）
  - [x] 字符串工具类（StringUtils）
  - [x] 常量定义（Constants、CacheConstants）

- [x] common-security - 安全公共模块
  - [x] JWT Token提供者（JwtTokenProvider）
  - [x] AccessToken生成
  - [x] RefreshToken生成
  - [x] Token解析与验证
  - [x] Token刷新

- [x] common-redis - Redis公共模块
  - [x] RedisCache工具类
  - [x] 基本对象缓存
  - [x] List/Set/Map数据缓存
  - [x] Hash操作
  - [x] 过期时间设置
  - [x] 批量操作
  - [x] RedisConfig配置类

- [x] common-mybatis - MyBatis增强模块
  - [x] MybatisPlusConfig配置类
  - [x] 分页插件配置
  - [x] 单页限制500条
  - [x] MySQL适配

- [x] common-swagger - 接口文档模块
  - [x] SwaggerConfig配置类
  - [x] OpenAPI 3.0配置
  - [x] Knife4j集成

### 2. 🚪 网关服务 (8080)

#### 2.1 核心功能
- [x] 统一入口
- [x] 动态路由配置
- [x] JWT认证过滤器
- [x] 白名单配置
- [x] 用户信息透传（Header）
- [x] 跨域配置（CORS）
- [x] 统一异常响应

#### 2.2 路由配置
- [x] 认证服务路由 (/auth/**)
- [x] 系统服务路由 (/system/**)
- [x] 文件服务路由 (/file/**)

### 3. 🔐 认证授权服务 (9200)

#### 3.1 认证功能
- [x] 用户登录（POST /auth/login）
- [x] 用户登出（POST /auth/logout）
- [x] Token刷新（POST /auth/refresh）
- [x] 获取用户信息（GET /auth/info）

#### 3.2 Token管理
- [x] AccessToken生成（2小时有效期）
- [x] RefreshToken生成（7天有效期）
- [x] Token验证
- [x] Token刷新机制

#### 3.3 DTO设计
- [x] LoginRequest - 登录请求
- [x] LoginResponse - 登录响应
- [x] UserInfo - 用户信息

### 4. 📊 系统服务 (9201)

#### 4.1 用户管理
- [x] 用户列表查询（GET /system/user/list）
  - [x] 多条件搜索（用户名、昵称、手机号、部门、状态）
  - [x] 分页支持
  - [x] 排序支持
- [x] 用户详情（GET /system/user/{id}）
- [x] 新增用户（POST /system/user）
  - [x] 用户名唯一性校验
  - [x] 表单验证
- [x] 修改用户（PUT /system/user）
  - [x] 超级管理员保护
  - [x] 用户名唯一性校验
- [x] 删除用户（DELETE /system/user/{ids}）
  - [x] 批量删除
  - [x] 超级管理员保护
- [x] 用户状态修改（PUT /system/user/changeStatus）
- [x] 密码重置（PUT /system/user/resetPwd）

#### 4.2 角色管理
- [x] 角色列表查询（GET /system/role/list）
  - [x] 多条件搜索（角色名称、权限字符、状态）
  - [x] 按显示顺序排序
  - [x] 分页支持
- [x] 角色详情（GET /system/role/{id}）
- [x] 新增角色（POST /system/role）
  - [x] 角色名称唯一性校验
  - [x] 权限字符唯一性校验
- [x] 修改角色（PUT /system/role）
  - [x] 超级管理员角色保护
  - [x] 唯一性校验
- [x] 删除角色（DELETE /system/role/{ids}）
  - [x] 批量删除
  - [x] 超级管理员角色保护
- [x] 角色状态修改（PUT /system/role/changeStatus）

#### 4.3 菜单管理
- [x] 菜单列表查询（GET /system/menu/list）
  - [x] 多条件搜索（菜单名称、菜单类型、状态）
  - [x] 按显示顺序排序
- [x] 菜单树查询（GET /system/menu/tree）
  - [x] 递归构建树形结构
  - [x] 支持N级菜单嵌套
- [x] 菜单详情（GET /system/menu/{id}）
- [x] 新增菜单（POST /system/menu）
  - [x] 菜单名称唯一性校验（同级）
  - [x] 支持三种类型（目录M、菜单C、按钮F）
- [x] 修改菜单（PUT /system/menu）
  - [x] 防止父菜单设置为自身
  - [x] 唯一性校验
- [x] 删除菜单（DELETE /system/menu/{id}）
  - [x] 子菜单存在检查

#### 4.4 部门管理
- [x] 部门列表查询（GET /system/dept/list）
  - [x] 多条件搜索（部门名称、状态）
  - [x] 按显示顺序排序
  - [x] 逻辑删除过滤
- [x] 部门树查询（GET /system/dept/tree）
  - [x] 递归构建树形结构
  - [x] 支持N级部门嵌套
- [x] 部门详情（GET /system/dept/{id}）
- [x] 新增部门（POST /system/dept）
  - [x] 部门名称唯一性校验（同级）
  - [x] 祖级列表(ancestors)自动维护
- [x] 修改部门（PUT /system/dept）
  - [x] 防止父部门设置为自身
  - [x] 祖级列表级联更新
  - [x] 唯一性校验
- [x] 删除部门（DELETE /system/dept/{id}）
  - [x] 子部门存在检查
  - [x] 部门用户存在检查

#### 4.5 岗位管理
- [x] 岗位列表查询（GET /system/post/list）
  - [x] 多条件搜索（岗位编码、岗位名称、状态）
  - [x] 分页支持
  - [x] 按显示顺序排序
- [x] 岗位详情（GET /system/post/{id}）
- [x] 新增岗位（POST /system/post）
  - [x] 岗位编码唯一性校验
  - [x] 岗位名称唯一性校验
- [x] 修改岗位（PUT /system/post）
  - [x] 唯一性校验
- [x] 删除岗位（DELETE /system/post/{ids}）
  - [x] 批量删除
- [x] 岗位状态修改（PUT /system/post/changeStatus）

#### 4.6 数据权限
- [x] 全部数据权限（dataScope=1）
- [x] 自定义数据权限（dataScope=2）
- [x] 本部门数据权限（dataScope=3）
- [x] 本部门及以下数据权限（dataScope=4）
- [x] 仅本人数据权限（dataScope=5）

#### 4.7 字典管理
- [x] 字典类型管理
  - [x] 字典类型列表查询（GET /system/dict/type/list）
  - [x] 字典类型详情（GET /system/dict/type/{dictId}）
  - [x] 新增字典类型（POST /system/dict/type）
    - [x] 字典类型唯一性校验
  - [x] 修改字典类型（PUT /system/dict/type）
  - [x] 删除字典类型（DELETE /system/dict/type/{dictIds}）
    - [x] 关联数据检查
  - [x] 批量删除支持
- [x] 字典数据管理
  - [x] 字典数据列表查询（GET /system/dict/data/list）
  - [x] 根据类型查询字典数据（GET /system/dict/data/type/{dictType}）
  - [x] 字典数据详情（GET /system/dict/data/{dictCode}）
  - [x] 新增字典数据（POST /system/dict/data）
  - [x] 修改字典数据（PUT /system/dict/data）
  - [x] 删除字典数据（DELETE /system/dict/data/{dictCodes}）
  - [x] 批量删除支持
  - [x] 样式属性支持

#### 4.8 参数配置管理
- [x] 参数配置列表查询（GET /system/config/list）
  - [x] 多条件搜索（参数名称、参数键名、系统内置）
  - [x] 分页支持
  - [x] 按参数主键排序
- [x] 参数配置详情（GET /system/config/{configId}）
- [x] 根据键名查询参数值（GET /system/config/configKey/{configKey}）
- [x] 新增参数配置（POST /system/config）
  - [x] 参数键名唯一性校验
  - [x] 表单验证
- [x] 修改参数配置（PUT /system/config）
  - [x] 唯一性校验
- [x] 删除参数配置（DELETE /system/config/{configIds}）
  - [x] 批量删除
  - [x] 内置参数保护（configType=1不可删除）

#### 4.9 操作日志管理
- [x] 操作日志列表查询（GET /system/operlog/list）
  - [x] 多条件搜索（标题、操作人员、业务类型、状态）
  - [x] 分页支持
- [x] 操作日志详情（GET /system/operlog/{operId}）
- [x] 删除操作日志（DELETE /system/operlog/{operIds}）
  - [x] 批量删除
- [x] 清空操作日志（DELETE /system/operlog/clean）
- [x] AOP切面自动记录
  - [x] LogAspect切面类（system/aspect/LogAspect.java）
  - [x] @Log注解拦截与解析
  - [x] 自动记录请求参数、响应结果
  - [x] 自动记录IP地址、请求URL、请求方式
  - [x] 异常情况自动记录（状态、错误信息）
  - [x] 用户、角色、菜单等核心模块已集成

#### 4.10 登录日志管理
- [x] 登录日志列表查询（GET /system/loginlog/list）
  - [x] 多条件搜索（用户名、登录IP、状态）
  - [x] 分页支持
- [x] 登录日志详情（GET /system/loginlog/{infoId}）
- [x] 删除登录日志（DELETE /system/loginlog/{infoIds}）
  - [x] 批量删除
- [x] 清空登录日志（DELETE /system/loginlog/clean）

#### 4.11 通知公告管理
- [x] 通知公告列表查询（GET /system/notice/list）
  - [x] 多条件搜索（公告标题、公告类型、状态、创建者）
  - [x] 分页支持
  - [x] 按创建时间倒序排序
- [x] 通知公告详情（GET /system/notice/{noticeId}）
- [x] 新增通知公告（POST /system/notice）
  - [x] 表单验证
  - [x] 支持两种类型（1=通知 2=公告）
- [x] 修改通知公告（PUT /system/notice）
- [x] 删除通知公告（DELETE /system/notice/{noticeIds}）
  - [x] 批量删除

#### 4.12 实体类设计
- [x] SysUser - 用户实体
- [x] SysRole - 角色实体
- [x] SysDept - 部门实体
- [x] SysMenu - 菜单实体
- [x] SysPost - 岗位实体
- [x] SysDictType - 字典类型实体
- [x] SysDictData - 字典数据实体
- [x] SysConfig - 参数配置实体
- [x] SysOperLog - 操作日志实体
- [x] SysLoginLog - 登录日志实体
- [x] SysNotice - 通知公告实体

### 5. 🎨 前端项目 (3000)

#### 5.1 基础架构
- [x] React 18.2.0
- [x] TypeScript 5.3.3
- [x] Ant Design 5.12.8
- [x] Vite 5.0.10
- [x] React Router 6.21.1
- [x] Axios 1.6.5

#### 5.2 工具封装
- [x] Axios请求封装（utils/request.ts）
  - [x] 请求拦截器（Token自动携带）
  - [x] 响应拦截器（统一错误处理）
  - [x] 401自动跳转登录
  - [x] 错误提示
- [x] 环境变量配置
  - [x] .env.development
  - [x] .env.production

#### 5.3 页面组件

**登录模块**
- [x] 登录页面（pages/login/index.tsx）
  - [x] 用户名/密码登录
  - [x] 表单验证
  - [x] 记住用户名
  - [x] Token保存
  - [x] 自动跳转
  - [x] 精美UI设计

**用户管理模块**
- [x] 用户管理页面（pages/system/user/index.tsx）
  - [x] 搜索表单（用户名、昵称、手机号、状态）
  - [x] 用户列表表格
    - [x] 分页功能
    - [x] 排序功能
    - [x] 列表展示
  - [x] 新增/编辑弹窗
    - [x] 完整表单
    - [x] 表单验证
    - [x] 密码强度校验
  - [x] 批量删除
  - [x] 单个删除（确认弹窗）
  - [x] 状态开关（Switch）
  - [x] 超级管理员保护

**角色管理模块**
- [x] 角色管理页面（pages/system/role/index.tsx）
  - [x] 搜索表单（角色名称、权限字符、状态）
  - [x] 角色列表表格
    - [x] 分页功能
    - [x] 数据范围显示（5种权限级别）
    - [x] 状态开关
  - [x] 新增/编辑弹窗
    - [x] 角色名称、权限字符
    - [x] 显示顺序、数据范围
    - [x] 状态、备注
    - [x] 表单验证
  - [x] 批量删除
  - [x] 超级管理员角色保护

**菜单管理模块**
- [x] 菜单管理页面（pages/system/menu/index.tsx）
  - [x] 搜索表单（菜单名称、菜单类型、状态）
  - [x] 菜单树形表格
    - [x] 树形结构展示
    - [x] 默认展开所有节点
    - [x] 菜单类型Tag显示（目录/菜单/按钮）
    - [x] 状态Tag显示
  - [x] 新增/编辑弹窗
    - [x] TreeSelect选择上级菜单
    - [x] 菜单类型、名称、图标
    - [x] 路由地址、组件路径
    - [x] 权限标识、显示排序
    - [x] 显示状态、菜单状态
    - [x] 动态表单（根据类型显示字段）
    - [x] 表单验证
  - [x] 单个删除（确认弹窗）
  - [x] 表格内新增按钮（快速添加子菜单）

**部门管理模块**
- [x] 部门管理页面（pages/system/dept/index.tsx）
  - [x] 搜索表单（部门名称、状态）
  - [x] 部门树形表格
    - [x] 树形结构展示
    - [x] 默认展开所有节点
    - [x] 完整信息展示（名称、排序、负责人、电话、邮箱、状态）
  - [x] 新增/编辑弹窗
    - [x] TreeSelect选择上级部门
    - [x] 部门名称、显示排序
    - [x] 负责人、联系电话、邮箱
    - [x] 部门状态、备注
    - [x] 邮箱格式验证
    - [x] 表单验证
  - [x] 单个删除（确认弹窗）
  - [x] 表格内新增按钮（快速添加子部门）

**岗位管理模块**
- [x] 岗位管理页面（pages/system/post/index.tsx）
  - [x] 搜索表单（岗位编码、岗位名称、状态）
  - [x] 岗位列表表格
    - [x] 分页功能
    - [x] 状态开关(Switch)
    - [x] 完整信息展示（编码、名称、排序、状态、创建时间）
  - [x] 新增/编辑弹窗
    - [x] 岗位编码、岗位名称（必填）
    - [x] 显示顺序、状态
    - [x] 备注
    - [x] 表单验证
  - [x] 批量删除
  - [x] 单个删除（确认弹窗）

**字典管理模块**
- [x] 字典类型管理页面（pages/system/dict/type/index.tsx）
  - [x] 搜索表单（字典名称、字典类型、状态）
  - [x] 字典类型列表表格
    - [x] 分页功能
    - [x] 批量删除
    - [x] 状态Tag显示
  - [x] 新增/编辑弹窗
    - [x] 字典名称、字典类型（必填）
    - [x] 状态、备注
    - [x] 表单验证
  - [x] "字典数据"按钮（跳转到数据管理页面）
  - [x] 单个删除（确认弹窗）
- [x] 字典数据管理页面（pages/system/dict/data/index.tsx）
  - [x] PageHeader显示当前字典类型
  - [x] 返回按钮
  - [x] 搜索表单（字典标签、状态）
  - [x] 字典数据列表表格
    - [x] 完整信息展示（标签、键值、排序、状态、是否默认）
    - [x] 分页功能
    - [x] 批量删除
  - [x] 新增/编辑弹窗
    - [x] 字典标签、键值（必填）
    - [x] 字典排序、样式属性
    - [x] 表格回显样式、是否默认
    - [x] 状态、备注
    - [x] 表单验证
  - [x] 单个删除（确认弹窗）

**参数配置模块**
- [x] 参数配置管理页面（pages/system/config/index.tsx）
  - [x] 搜索表单（参数名称、参数键名、系统内置）
  - [x] 参数配置列表表格
    - [x] 分页功能
    - [x] 批量删除
    - [x] 系统内置Tag显示
    - [x] 内置参数禁止编辑/删除
  - [x] 新增/编辑弹窗
    - [x] 参数名称、键名、键值（必填）
    - [x] 系统内置、备注
    - [x] 表单验证
  - [x] 单个删除（确认弹窗）

**日志管理模块**
- [x] 操作日志页面（pages/monitor/operlog/index.tsx）
  - [x] 搜索表单（标题、操作人员、业务类型、状态）
  - [x] 操作日志列表表格
    - [x] 分页功能
    - [x] 业务类型Tag显示（9种类型）
    - [x] 状态Tag显示（成功/失败）
    - [x] 完整信息展示（标题、操作人员、IP、地点、时间、消耗时长）
  - [x] 详情查看弹窗
    - [x] Descriptions组件展示所有字段
    - [x] 请求参数/响应参数格式化显示
  - [x] 批量删除
  - [x] 清空日志（确认弹窗）
  - [x] 单个删除（确认弹窗）
- [x] 登录日志页面（pages/monitor/loginlog/index.tsx）
  - [x] 搜索表单（用户名、登录IP、状态）
  - [x] 登录日志列表表格
    - [x] 分页功能
    - [x] 登录状态Tag显示（成功/失败）
    - [x] 完整信息展示（用户名、IP、地点、浏览器、操作系统、时间）
  - [x] 批量删除
  - [x] 清空日志（确认弹窗）
  - [x] 单个删除（确认弹窗）

**通知公告模块**
- [x] 通知公告管理页面（pages/system/notice/index.tsx）
  - [x] 搜索表单（公告标题、公告类型、状态、创建者）
  - [x] 通知公告列表表格
    - [x] 分页功能
    - [x] 批量删除
    - [x] 公告类型Tag显示（通知/公告）
    - [x] 状态Tag显示（正常/关闭）
    - [x] 完整信息展示（标题、类型、状态、创建者、创建时间）
  - [x] 新增/编辑弹窗
    - [x] 公告标题、类型、状态（必选）
    - [x] 公告内容（TextArea，最多2000字符）
    - [x] 备注
    - [x] 表单验证
  - [x] 详情查看弹窗
    - [x] 完整显示所有字段
    - [x] 公告内容格式化展示
  - [x] 单个删除（确认弹窗）

#### 5.4 API封装
- [x] 用户API（api/system/user.ts）
  - [x] TypeScript类型定义
  - [x] 完整的CRUD方法
- [x] 角色API（api/system/role.ts）
  - [x] TypeScript类型定义
  - [x] 完整的CRUD方法
- [x] 菜单API（api/system/menu.ts）
  - [x] TypeScript类型定义
  - [x] 菜单树查询方法
  - [x] 完整的CRUD方法
- [x] 部门API（api/system/dept.ts）
  - [x] TypeScript类型定义
  - [x] 部门树查询方法
  - [x] 完整的CRUD方法
- [x] 岗位API（api/system/post.ts）
  - [x] TypeScript类型定义
  - [x] 完整的CRUD方法
  - [x] 状态修改方法
- [x] 字典API（api/system/dict.ts）
  - [x] DictType、DictData类型定义
  - [x] 字典类型完整CRUD方法
  - [x] 字典数据完整CRUD方法
  - [x] getDictDataByType() - 根据类型查询字典数据
- [x] 参数配置API（api/system/config.ts）
  - [x] Config类型定义
  - [x] 完整的CRUD方法
  - [x] getConfigKey() - 根据键名查询参数值
- [x] 日志API（api/monitor/log.ts）
  - [x] OperLog、LoginLog类型定义
  - [x] 操作日志完整查询/删除方法
  - [x] 登录日志完整查询/删除方法
  - [x] cleanOperLog()、cleanLoginLog() - 清空日志
- [x] 通知公告API（api/system/notice.ts）
  - [x] Notice类型定义
  - [x] 完整的CRUD方法
  - [x] 支持公告类型和状态过滤

### 6. 🗄️ 数据库设计 (18张表)

#### 6.1 RBAC权限模型
- [x] sys_user - 用户表
- [x] sys_role - 角色表
- [x] sys_menu - 菜单权限表
- [x] sys_user_role - 用户角色关联表
- [x] sys_role_menu - 角色菜单关联表

#### 6.2 组织架构
- [x] sys_dept - 部门表（树形结构）
- [x] sys_post - 岗位表
- [x] sys_user_post - 用户岗位关联表
- [x] sys_role_dept - 角色部门关联表

#### 6.3 系统管理
- [x] sys_dict_type - 字典类型表
- [x] sys_dict_data - 字典数据表
- [x] sys_config - 参数配置表
- [x] sys_notice - 通知公告表

#### 6.4 日志管理
- [x] sys_oper_log - 操作日志表
- [x] sys_login_log - 登录日志表

#### 6.5 定时任务
- [x] sys_job - 定时任务表
- [x] sys_job_log - 定时任务日志表

#### 6.6 文件管理
- [x] sys_file - 文件信息表

#### 6.7 初始化数据
- [x] 默认管理员（admin/admin123）
- [x] 默认角色（超级管理员、普通角色）
- [x] 完整菜单树（系统管理、系统监控、系统工具）
- [x] 默认部门（4个）
- [x] 默认岗位（5个）
- [x] 系统字典数据

### 7. 🐳 部署支持

#### 7.1 Docker Compose
- [x] MySQL 8.0配置
- [x] Redis 7.x配置
- [x] Nacos 2.3.x配置
- [x] MinIO配置
- [x] Nginx配置（可选）
- [x] 自动初始化数据库

#### 7.2 启动脚本
- [x] start.sh - 快速启动脚本
- [x] 自动检查Docker
- [x] 自动启动基础服务
- [x] 显示服务地址
- [x] 显示默认账号

### 8. 📚 文档

- [x] README.md - 项目说明
- [x] DEVELOPMENT_GUIDE.md - 开发指南
- [x] PROJECT_STRUCTURE.md - 项目结构说明
- [x] FEATURES.md - 功能清单（本文件）
- [x] .gitignore - Git忽略配置

---

## 🚧 待实现功能

### 后端功能
- [ ] 登录日志记录（登录时自动记录）
- [ ] 文件服务（上传/下载）
- [ ] 代码生成器服务
- [ ] 定时任务服务（Quartz）
- [ ] 监控服务（Spring Boot Admin）
- [ ] 消息服务（RabbitMQ/Kafka）

### 前端功能
- [ ] 主布局组件（Header/Sidebar/TabsView）
- [ ] 个人中心页面
- [ ] 修改密码页面
- [ ] Dashboard仪表盘
- [ ] 动态路由（基于菜单）
- [ ] 权限指令（v-permission）

### 高级功能
- [ ] Sentinel流量控制
- [ ] Seata分布式事务
- [ ] Elasticsearch全文检索
- [ ] MinIO文件存储
- [ ] Prometheus + Grafana监控
- [ ] Zipkin链路追踪
- [ ] Workflow工作流
- [ ] WebSocket实时通信
- [ ] Excel导入导出
- [ ] 数据权限拦截器实现
- [ ] 在线用户管理
- [ ] 缓存管理
- [ ] 系统接口管理

---

## 📊 当前进度统计

| 模块 | 完成度 | 说明 |
|------|--------|------|
| 基础架构 | 85% | 微服务架构、公共模块、日志注解完成 |
| 网关服务 | 100% | 认证、路由、跨域完成 |
| 认证服务 | 90% | 登录、Token管理完成，待集成数据库 |
| 系统服务 | 65% | 用户、角色、菜单、部门、岗位、字典、参数、日志、公告管理完成 |
| 前端项目 | 60% | 登录、用户、角色、菜单、部门、岗位、字典、参数、日志、公告页面完成 |
| 数据库设计 | 100% | 18张表设计完成 |
| 部署支持 | 100% | Docker Compose配置完成 |
| 文档 | 90% | 4篇文档完成 |

**总体完成度**: 约 **88%**

---

## 🎯 下一步计划

### 短期目标（优先级高）
1. ✅ 完善菜单管理（CRUD + 树形结构）**[已完成]**
2. ✅ 完善部门管理（CRUD + 树形结构）**[已完成]**
3. ✅ 完善岗位管理（CRUD + 批量操作）**[已完成]**
4. ✅ 完善字典管理（类型 + 数据）**[已完成]**
5. ✅ 实现参数配置管理**[已完成]**
6. ✅ 实现日志管理（操作日志 + 登录日志）**[已完成]**
7. ✅ 实现通知公告管理**[已完成]**
8. ✅ 集成操作日志（AOP切面自动记录）**[已完成]**
9. ⏳ 实现主布局组件（Header/Sidebar/TabsView）
10. ⏳ 实现动态路由（基于菜单）
11. ⏳ 集成登录日志（登录时自动记录）

### 中期目标
1. ⏳ 文件服务实现（MinIO）
2. ⏳ 代码生成器实现
3. ⏳ 定时任务服务实现
4. ⏳ 个人中心页面实现
5. ⏳ Dashboard仪表盘实现

### 长期目标
1. ⏳ 监控服务（Spring Boot Admin）
2. ⏳ 链路追踪（Zipkin）
3. ⏳ 分布式事务（Seata）
4. ⏳ 工作流引擎（Flowable）
5. ⏳ 搜索服务（Elasticsearch）

---

**注意**: 当前框架已经具备生产环境的基础能力，可以在此基础上快速开发业务功能！
