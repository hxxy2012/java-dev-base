-- ========================================
-- EnterpriseX Framework - 初始化数据脚本
-- 版本: 1.0.0
-- 描述: 插入默认的管理员、角色、菜单、部门等数据
-- ========================================

USE `enterprisex`;

-- ========================================
-- 1. 插入默认部门
-- ========================================
INSERT INTO `sys_dept` VALUES
(1, 0, '0', 'EnterpriseX集团', 0, 'Admin', '15888888888', 'admin@enterprisex.com', 1, 0, 'admin', NOW(), NULL, NOW()),
(2, 1, '0,1', '技术部', 1, 'Tech Leader', '15888888889', 'tech@enterprisex.com', 1, 0, 'admin', NOW(), NULL, NOW()),
(3, 1, '0,1', '市场部', 2, 'Market Leader', '15888888890', 'market@enterprisex.com', 1, 0, 'admin', NOW(), NULL, NOW()),
(4, 1, '0,1', '财务部', 3, 'Finance Leader', '15888888891', 'finance@enterprisex.com', 1, 0, 'admin', NOW(), NULL, NOW());

-- ========================================
-- 2. 插入默认岗位
-- ========================================
INSERT INTO `sys_post` VALUES
(1, 'ceo', 'CEO', 1, 1, 'admin', NOW(), NULL, NOW(), 'CEO'),
(2, 'cto', 'CTO', 2, 1, 'admin', NOW(), NULL, NOW(), 'CTO'),
(3, 'developer', '开发工程师', 3, 1, 'admin', NOW(), NULL, NOW(), '软件开发工程师'),
(4, 'tester', '测试工程师', 4, 1, 'admin', NOW(), NULL, NOW(), '软件测试工程师'),
(5, 'pm', '项目经理', 5, 1, 'admin', NOW(), NULL, NOW(), '项目经理');

-- ========================================
-- 3. 插入默认角色
-- ========================================
INSERT INTO `sys_role` VALUES
(1, '超级管理员', 'admin', 1, 1, 1, 0, 'admin', NOW(), NULL, NOW(), '超级管理员，拥有所有权限'),
(2, '普通角色', 'common', 2, 5, 1, 0, 'admin', NOW(), NULL, NOW(), '普通角色，仅拥有基础权限');

-- ========================================
-- 4. 插入默认用户（密码：admin123）
-- ========================================
-- 注意：这里使用BCrypt加密后的密码，密码是：admin123
INSERT INTO `sys_user` VALUES
(1, 1, 'admin', '系统管理员', 'admin@enterprisex.com', '15888888888', 1, NULL,
'$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sLlnFk.QHiq',
1, 0, '127.0.0.1', NOW(), 'admin', NOW(), NULL, NOW(), '超级管理员');

-- ========================================
-- 5. 用户角色关联
-- ========================================
INSERT INTO `sys_user_role` VALUES (1, 1);

-- ========================================
-- 6. 插入默认菜单
-- ========================================
INSERT INTO `sys_menu` VALUES
-- 一级菜单
(1, 0, '系统管理', 'M', '/system', NULL, NULL, 'SettingOutlined', 1, 1, 1, 'admin', NOW(), NULL, NOW(), '系统管理目录'),
(2, 0, '系统监控', 'M', '/monitor', NULL, NULL, 'MonitorOutlined', 2, 1, 1, 'admin', NOW(), NULL, NOW(), '系统监控目录'),
(3, 0, '系统工具', 'M', '/tool', NULL, NULL, 'ToolOutlined', 3, 1, 1, 'admin', NOW(), NULL, NOW(), '系统工具目录'),

-- 系统管理子菜单
(100, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'UserOutlined', 1, 1, 1, 'admin', NOW(), NULL, NOW(), '用户管理菜单'),
(101, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'TeamOutlined', 2, 1, 1, 'admin', NOW(), NULL, NOW(), '角色管理菜单'),
(102, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'MenuOutlined', 3, 1, 1, 'admin', NOW(), NULL, NOW(), '菜单管理菜单'),
(103, 1, '部门管理', 'C', '/system/dept', 'system/dept/index', 'system:dept:list', 'ApartmentOutlined', 4, 1, 1, 'admin', NOW(), NULL, NOW(), '部门管理菜单'),
(104, 1, '岗位管理', 'C', '/system/post', 'system/post/index', 'system:post:list', 'SolutionOutlined', 5, 1, 1, 'admin', NOW(), NULL, NOW(), '岗位管理菜单'),
(105, 1, '字典管理', 'C', '/system/dict', 'system/dict/index', 'system:dict:list', 'BookOutlined', 6, 1, 1, 'admin', NOW(), NULL, NOW(), '字典管理菜单'),
(106, 1, '参数设置', 'C', '/system/config', 'system/config/index', 'system:config:list', 'ControlOutlined', 7, 1, 1, 'admin', NOW(), NULL, NOW(), '参数设置菜单'),
(107, 1, '通知公告', 'C', '/system/notice', 'system/notice/index', 'system:notice:list', 'NotificationOutlined', 8, 1, 1, 'admin', NOW(), NULL, NOW(), '通知公告菜单'),
(108, 1, '日志管理', 'M', '/system/log', NULL, NULL, 'FileTextOutlined', 9, 1, 1, 'admin', NOW(), NULL, NOW(), '日志管理菜单'),

-- 用户管理按钮
(1000, 100, '用户查询', 'F', '', NULL, 'system:user:query', NULL, 1, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1001, 100, '用户新增', 'F', '', NULL, 'system:user:add', NULL, 2, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1002, 100, '用户修改', 'F', '', NULL, 'system:user:edit', NULL, 3, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1003, 100, '用户删除', 'F', '', NULL, 'system:user:remove', NULL, 4, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1004, 100, '用户导出', 'F', '', NULL, 'system:user:export', NULL, 5, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1005, 100, '用户导入', 'F', '', NULL, 'system:user:import', NULL, 6, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1006, 100, '重置密码', 'F', '', NULL, 'system:user:resetPwd', NULL, 7, 1, 1, 'admin', NOW(), NULL, NOW(), ''),

-- 角色管理按钮
(1007, 101, '角色查询', 'F', '', NULL, 'system:role:query', NULL, 1, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1008, 101, '角色新增', 'F', '', NULL, 'system:role:add', NULL, 2, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1009, 101, '角色修改', 'F', '', NULL, 'system:role:edit', NULL, 3, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1010, 101, '角色删除', 'F', '', NULL, 'system:role:remove', NULL, 4, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1011, 101, '角色导出', 'F', '', NULL, 'system:role:export', NULL, 5, 1, 1, 'admin', NOW(), NULL, NOW(), ''),

-- 菜单管理按钮
(1012, 102, '菜单查询', 'F', '', NULL, 'system:menu:query', NULL, 1, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1013, 102, '菜单新增', 'F', '', NULL, 'system:menu:add', NULL, 2, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1014, 102, '菜单修改', 'F', '', NULL, 'system:menu:edit', NULL, 3, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1015, 102, '菜单删除', 'F', '', NULL, 'system:menu:remove', NULL, 4, 1, 1, 'admin', NOW(), NULL, NOW(), ''),

-- 部门管理按钮
(1016, 103, '部门查询', 'F', '', NULL, 'system:dept:query', NULL, 1, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1017, 103, '部门新增', 'F', '', NULL, 'system:dept:add', NULL, 2, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1018, 103, '部门修改', 'F', '', NULL, 'system:dept:edit', NULL, 3, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1019, 103, '部门删除', 'F', '', NULL, 'system:dept:remove', NULL, 4, 1, 1, 'admin', NOW(), NULL, NOW(), ''),

-- 岗位管理按钮
(1020, 104, '岗位查询', 'F', '', NULL, 'system:post:query', NULL, 1, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1021, 104, '岗位新增', 'F', '', NULL, 'system:post:add', NULL, 2, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1022, 104, '岗位修改', 'F', '', NULL, 'system:post:edit', NULL, 3, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1023, 104, '岗位删除', 'F', '', NULL, 'system:post:remove', NULL, 4, 1, 1, 'admin', NOW(), NULL, NOW(), ''),
(1024, 104, '岗位导出', 'F', '', NULL, 'system:post:export', NULL, 5, 1, 1, 'admin', NOW(), NULL, NOW(), ''),

-- 日志管理子菜单
(500, 108, '操作日志', 'C', '/system/log/operlog', 'system/log/operlog/index', 'system:operlog:list', 'FormOutlined', 1, 1, 1, 'admin', NOW(), NULL, NOW(), '操作日志菜单'),
(501, 108, '登录日志', 'C', '/system/log/loginlog', 'system/log/loginlog/index', 'system:loginlog:list', 'LoginOutlined', 2, 1, 1, 'admin', NOW(), NULL, NOW(), '登录日志菜单'),

-- 系统监控菜单
(200, 2, '在线用户', 'C', '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'UserSwitchOutlined', 1, 1, 1, 'admin', NOW(), NULL, NOW(), '在线用户菜单'),
(201, 2, '定时任务', 'C', '/monitor/job', 'monitor/job/index', 'monitor:job:list', 'ClockCircleOutlined', 2, 1, 1, 'admin', NOW(), NULL, NOW(), '定时任务菜单'),
(202, 2, '服务监控', 'C', '/monitor/server', 'monitor/server/index', 'monitor:server:list', 'CloudServerOutlined', 3, 1, 1, 'admin', NOW(), NULL, NOW(), '服务监控菜单'),

-- 系统工具菜单
(300, 3, '代码生成', 'C', '/tool/gen', 'tool/gen/index', 'tool:gen:list', 'CodeOutlined', 1, 1, 1, 'admin', NOW(), NULL, NOW(), '代码生成菜单'),
(301, 3, '系统接口', 'C', '/tool/swagger', 'tool/swagger/index', 'tool:swagger:list', 'ApiOutlined', 2, 1, 1, 'admin', NOW(), NULL, NOW(), '系统接口菜单');

-- ========================================
-- 7. 角色菜单关联（超级管理员拥有所有权限）
-- ========================================
INSERT INTO `sys_role_menu`
SELECT 1, menu_id FROM sys_menu WHERE status = 1;

-- ========================================
-- 8. 插入默认字典类型
-- ========================================
INSERT INTO `sys_dict_type` VALUES
(1, '用户性别', 'sys_user_sex', 1, 'admin', NOW(), NULL, NOW(), '用户性别列表'),
(2, '菜单状态', 'sys_show_hide', 1, 'admin', NOW(), NULL, NOW(), '菜单状态列表'),
(3, '系统开关', 'sys_normal_disable', 1, 'admin', NOW(), NULL, NOW(), '系统开关列表'),
(4, '任务状态', 'sys_job_status', 1, 'admin', NOW(), NULL, NOW(), '任务状态列表'),
(5, '任务分组', 'sys_job_group', 1, 'admin', NOW(), NULL, NOW(), '任务分组列表'),
(6, '系统是否', 'sys_yes_no', 1, 'admin', NOW(), NULL, NOW(), '系统是否列表'),
(7, '通知类型', 'sys_notice_type', 1, 'admin', NOW(), NULL, NOW(), '通知类型列表'),
(8, '通知状态', 'sys_notice_status', 1, 'admin', NOW(), NULL, NOW(), '通知状态列表'),
(9, '操作类型', 'sys_oper_type', 1, 'admin', NOW(), NULL, NOW(), '操作类型列表'),
(10, '系统状态', 'sys_common_status', 1, 'admin', NOW(), NULL, NOW(), '系统状态列表');

-- ========================================
-- 9. 插入默认字典数据
-- ========================================
INSERT INTO `sys_dict_data` VALUES
-- 用户性别
(1, 1, '男', '1', 'sys_user_sex', '', '', 0, 1, 'admin', NOW(), NULL, NOW(), '性别男'),
(2, 2, '女', '2', 'sys_user_sex', '', '', 0, 1, 'admin', NOW(), NULL, NOW(), '性别女'),
(3, 3, '未知', '0', 'sys_user_sex', '', '', 1, 1, 'admin', NOW(), NULL, NOW(), '性别未知'),

-- 菜单状态
(4, 1, '显示', '1', 'sys_show_hide', '', 'success', 1, 1, 'admin', NOW(), NULL, NOW(), '显示菜单'),
(5, 2, '隐藏', '0', 'sys_show_hide', '', 'error', 0, 1, 'admin', NOW(), NULL, NOW(), '隐藏菜单'),

-- 系统开关
(6, 1, '正常', '1', 'sys_normal_disable', '', 'success', 1, 1, 'admin', NOW(), NULL, NOW(), '正常状态'),
(7, 2, '禁用', '0', 'sys_normal_disable', '', 'error', 0, 1, 'admin', NOW(), NULL, NOW(), '禁用状态'),

-- 系统是否
(8, 1, '是', '1', 'sys_yes_no', '', 'success', 1, 1, 'admin', NOW(), NULL, NOW(), '是'),
(9, 2, '否', '0', 'sys_yes_no', '', 'error', 0, 1, 'admin', NOW(), NULL, NOW(), '否'),

-- 通知类型
(10, 1, '通知', '1', 'sys_notice_type', '', 'warning', 1, 1, 'admin', NOW(), NULL, NOW(), '通知'),
(11, 2, '公告', '2', 'sys_notice_type', '', 'success', 0, 1, 'admin', NOW(), NULL, NOW(), '公告');

-- ========================================
-- 10. 插入默认配置
-- ========================================
INSERT INTO `sys_config` VALUES
(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 0, 'admin', NOW(), NULL, NOW(), '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 1, 'admin', NOW(), NULL, NOW(), '初始化密码 123456'),
(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 0, 'admin', NOW(), NULL, NOW(), '深色主题theme-dark，浅色主题theme-light');

-- ========================================
-- 初始化数据完成
-- 默认登录账号：admin
-- 默认登录密码：admin123
-- ========================================
