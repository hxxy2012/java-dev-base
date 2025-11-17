# Dashboard仪表盘使用指南

## 📊 功能概述

Dashboard仪表盘是EnterpriseX Framework的核心监控页面，提供系统运行状态的实时统计和快速访问入口。

## ✨ 主要功能

### 1. 实时统计卡片

Dashboard提供8个实时统计卡片，展示系统核心数据：

#### 第一行统计
- **总用户数**: 显示系统中所有未删除的用户总数
- **角色数量**: 显示系统中所有角色的数量
- **部门数量**: 显示系统组织架构中的部门总数
- **今日登录**: 显示今天（00:00:00至当前时间）的登录次数

#### 第二行统计
- **在线用户**: 显示当前在线的用户数量（需要Redis Session支持）
- **今日操作**: 显示今天的操作日志记录数
- **未读消息**: 显示当前用户的未读消息数量
- **系统健康度**: 以进度条形式展示系统健康状态

### 2. 快速访问链接

提供8个常用功能的快速访问图标：
- 用户管理
- 角色管理
- 菜单管理
- 系统监控
- 操作日志
- 定时任务
- 代码生成
- 系统工具

点击图标可直接跳转到对应的管理页面。

### 3. 用户状态统计

以进度条形式展示用户状态分布：
- **正常用户**: 绿色进度条，显示正常状态的用户数量和占比
- **禁用用户**: 红色进度条，显示禁用状态的用户数量和占比

### 4. 最近登录列表

显示最近5条登录记录，包含以下信息：
- 用户头像（默认头像）
- 用户名
- 登录IP地址
- 登录时间
- 登录状态（成功/失败）

点击"更多"按钮可跳转到完整的登录日志页面。

### 5. 最近操作列表

显示最近5条操作记录，包含以下信息：
- 操作标题
- 操作人
- 操作时间
- 操作状态（成功/失败）

点击"更多"按钮可跳转到完整的操作日志页面。

### 6. 近7天登录趋势

展示最近7天的登录统计数据（暂未在前端显示，数据已准备）：
- 每天的日期（MM/dd格式）
- 每天的登录次数

## 🔧 技术实现

### 后端API

#### 1. 获取Dashboard统计数据
```http
GET /system/dashboard/stats
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userCount": 100,
    "roleCount": 10,
    "deptCount": 20,
    "onlineUserCount": 5,
    "todayLoginCount": 50,
    "todayOperCount": 200,
    "unreadMessageCount": 3,
    "recentLogins": [...],
    "recentOperations": [...],
    "userStatusStats": {
      "正常": 85,
      "禁用": 15
    },
    "weeklyLoginStats": [...]
  }
}
```

#### 2. 获取快速访问链接
```http
GET /system/dashboard/quickLinks
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "name": "用户管理",
      "path": "/system/user",
      "icon": "UserOutlined",
      "color": "#1890ff"
    },
    ...
  ]
}
```

### 前端实现

Dashboard页面位于 `/enterprisex-ui/src/pages/dashboard/index.tsx`

#### 核心技术栈
- React 18 Hooks（useState, useEffect）
- Ant Design 5 组件（Card, Statistic, Progress, List等）
- React Router（useNavigate路由跳转）
- Axios（API请求）

#### 关键代码片段

**数据加载**:
```typescript
const loadStats = async () => {
  try {
    const res = await getDashboardStats();
    if (res.code === 200) {
      setStats(res.data);
    }
  } catch (error) {
    message.error('加载统计数据失败');
  } finally {
    setLoading(false);
  }
};
```

**快速访问**:
```typescript
const navigate = useNavigate();

<Card className={styles.iconCard} onClick={() => navigate(link.path)}>
  <div style={{ color: link.color }}>
    <Icon component={() => getIconComponent(link.icon)}
          style={{ fontSize: '32px' }} />
  </div>
  <div>{link.name}</div>
</Card>
```

## 📝 数据来源

### 数据库表
- **sys_user**: 用户统计、用户状态统计
- **sys_role**: 角色数量统计
- **sys_dept**: 部门数量统计
- **sys_login_log**: 登录统计、最近登录记录、近7天登录趋势
- **sys_oper_log**: 操作统计、最近操作记录
- **sys_message**: 未读消息统计

### 查询优化
- 使用MyBatis Plus的LambdaQueryWrapper构建类型安全的查询
- 使用LIMIT限制查询结果数量，提升性能
- 使用LocalDateTime进行精确的日期时间范围查询
- 按时间倒序排序，确保获取最新数据

## 🚀 性能优化建议

1. **缓存统计数据**:
   - 对于变化不频繁的数据（如用户总数、角色数量），可使用Redis缓存
   - 设置合理的缓存过期时间（如5分钟）

2. **异步加载**:
   - 前端使用异步方式加载数据，避免阻塞页面渲染
   - 显示加载状态，提升用户体验

3. **定时刷新**:
   - 可配置定时刷新机制（如每30秒自动刷新）
   - 提供手动刷新按钮

4. **分页加载**:
   - 对于最近记录列表，后端限制返回数量
   - 如需更多数据，引导用户跳转到详细页面

## 🔐 权限控制

Dashboard页面通常对所有登录用户开放，但可以根据需要进行权限控制：

- 敏感统计数据可根据用户角色显示
- 快速访问链接根据用户权限动态过滤
- 不同角色看到不同的统计维度

## 📱 响应式设计

Dashboard采用响应式布局设计：

- **桌面端**: 使用Card网格布局，充分利用屏幕空间
- **平板端**: 自动调整卡片大小和列数
- **移动端**: 垂直堆叠卡片，优化触摸操作

### 栅格布局配置
```typescript
<Col xs={24} sm={12} md={12} lg={6} xl={6}>
  <Statistic title="总用户数" value={stats.userCount} />
</Col>
```

## 🎨 自定义配置

您可以根据业务需求自定义Dashboard：

### 1. 修改统计卡片
在`DashboardController.java`的`getStats()`方法中添加新的统计项。

### 2. 调整快速访问链接
在`DashboardController.java`的`getQuickLinks()`方法中修改链接配置。

### 3. 定制UI样式
在`/enterprisex-ui/src/pages/dashboard/index.module.css`中修改样式。

### 4. 添加图表
可以集成ECharts或其他图表库，展示更丰富的数据可视化：
- 折线图：展示7天登录趋势
- 饼图：展示用户状态分布
- 柱状图：展示各部门用户分布

## 🐛 已知问题

1. **在线用户数统计**: 目前返回0，需要集成Redis Session统计
2. **用户ID获取**: 当前使用硬编码（userId=1），需要从安全上下文获取当前登录用户ID
3. **实时更新**: 需要实现WebSocket推送机制，实现实时数据更新

## 📚 相关文档

- [FEATURES.md](./FEATURES.md) - 完整功能列表
- [API_EXAMPLES.md](./API_EXAMPLES.md) - API调用示例
- [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) - 开发指南

## 🤝 贡献

欢迎提交Issue和Pull Request来改进Dashboard功能！
