package com.enterprisex.common.mybatis.interceptor;

import com.enterprisex.common.core.annotation.DataScope;
import com.enterprisex.common.core.domain.model.DataScopeContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 数据权限拦截器
 * 根据用户的数据权限范围，自动拼接SQL条件
 *
 * 使用说明：
 * 1. 在Mapper方法上添加@DataScope注解
 * 2. 指定deptAlias和userAlias（SQL中的表别名）
 * 3. 拦截器会根据用户角色的dataScope自动拼接WHERE条件
 *
 * 示例：
 * @DataScope(deptAlias = "d", userAlias = "u")
 * List<SysUser> selectUserList(SysUser user);
 *
 * 注意：由于SQL解析和重写的复杂性，当前版本仅提供框架代码和用户权限识别。
 * 建议在Service层使用LambdaQueryWrapper手动添加数据权限条件，更灵活可控。
 *
 * @author EnterpriseX
 */
@Slf4j
@Component
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        )
})
public class DataScopeInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        // 获取Mapper方法
        String mapperId = ms.getId();
        String className = mapperId.substring(0, mapperId.lastIndexOf("."));
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);

        try {
            // 检查方法是否有@DataScope注解
            Class<?> mapperClass = Class.forName(className);
            Method[] methods = mapperClass.getMethods();
            DataScope dataScope = null;

            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(DataScope.class)) {
                    dataScope = method.getAnnotation(DataScope.class);
                    break;
                }
            }

            // 如果方法没有@DataScope注解或未启用，直接执行原始查询
            if (dataScope == null || !dataScope.enabled()) {
                return invocation.proceed();
            }

            // 获取用户数据权限上下文
            DataScopeContext context = buildDataScopeContext();

            if (context != null && !context.isAdmin()) {
                log.debug("数据权限拦截 - Mapper: {}, DataScope: {}, UserId: {}, DeptId: {}",
                        mapperId, context.getDataScope(), context.getUserId(), context.getDeptId());

                // 这里可以实现SQL重写逻辑
                // 由于SQL解析和重写较复杂，建议在Service层使用LambdaQueryWrapper实现
                // String sqlCondition = buildSqlCondition(context, dataScope);
                // BoundSql boundSql = ms.getBoundSql(parameter);
                // 修改SQL...（需要使用JSqlParser等工具）
            }

        } catch (Exception e) {
            log.warn("数据权限拦截失败: {}", e.getMessage());
        }

        // 执行原始查询
        return invocation.proceed();
    }

    /**
     * 构建数据权限上下文
     * 从请求Header中获取用户信息
     */
    private DataScopeContext buildDataScopeContext() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            String userIdStr = request.getHeader("X-User-Id");
            String deptIdStr = request.getHeader("X-Dept-Id");
            String dataScopeStr = request.getHeader("X-Data-Scope");

            if (userIdStr == null || userIdStr.isEmpty()) {
                return null;
            }

            DataScopeContext context = new DataScopeContext();
            context.setUserId(Long.parseLong(userIdStr));

            // 用户ID为1视为超级管理员，拥有所有数据权限
            if (context.getUserId() == 1L) {
                context.setAdmin(true);
                context.setDataScope(1); // 全部数据权限
                return context;
            }

            // 设置部门ID
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                context.setDeptId(Long.parseLong(deptIdStr));
            }

            // 设置数据范围（默认为5-仅本人数据）
            if (dataScopeStr != null && !dataScopeStr.isEmpty()) {
                context.setDataScope(Integer.parseInt(dataScopeStr));
            } else {
                context.setDataScope(5); // 默认仅本人数据
            }

            return context;

        } catch (Exception e) {
            log.error("构建数据权限上下文失败", e);
            return null;
        }
    }

    /**
     * 构建SQL条件
     * 根据数据权限范围生成WHERE条件
     *
     * @param context 数据权限上下文
     * @param dataScope @DataScope注解
     * @return SQL条件字符串
     */
    @SuppressWarnings("unused")
    private String buildSqlCondition(DataScopeContext context, DataScope dataScope) {
        Integer scope = context.getDataScope();
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();

        // 添加表别名的点号
        String deptPrefix = deptAlias.isEmpty() ? "" : deptAlias + ".";
        String userPrefix = userAlias.isEmpty() ? "" : userAlias + ".";

        StringBuilder condition = new StringBuilder();

        switch (scope) {
            case 1:
                // 全部数据权限 - 不添加额外条件
                break;

            case 2:
                // 自定义数据权限 - dept_id IN (指定的部门ID列表)
                if (context.getDeptIds() != null && !context.getDeptIds().isEmpty()) {
                    condition.append(" AND ").append(deptPrefix).append("dept_id IN (");
                    condition.append(String.join(",", context.getDeptIds().stream()
                            .map(String::valueOf).toArray(String[]::new)));
                    condition.append(")");
                }
                break;

            case 3:
                // 本部门数据权限 - dept_id = 用户部门ID
                if (context.getDeptId() != null) {
                    condition.append(" AND ").append(deptPrefix)
                            .append("dept_id = ").append(context.getDeptId());
                }
                break;

            case 4:
                // 本部门及以下数据权限 - dept_id IN (本部门及子部门ID列表)
                // 这需要查询部门树，这里简化为只查询本部门
                if (context.getDeptId() != null) {
                    condition.append(" AND ").append(deptPrefix)
                            .append("dept_id = ").append(context.getDeptId());
                }
                break;

            case 5:
                // 仅本人数据权限 - create_by = 用户ID 或 user_id = 用户ID
                if (!userPrefix.isEmpty()) {
                    condition.append(" AND ").append(userPrefix)
                            .append("user_id = ").append(context.getUserId());
                } else {
                    condition.append(" AND create_by = '").append(context.getUserId()).append("'");
                }
                break;

            default:
                // 默认仅本人数据
                condition.append(" AND create_by = '").append(context.getUserId()).append("'");
                break;
        }

        return condition.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以配置一些属性
    }
}
