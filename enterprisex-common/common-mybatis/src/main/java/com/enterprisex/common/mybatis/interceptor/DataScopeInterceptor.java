package com.enterprisex.common.mybatis.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据权限拦截器
 * 根据用户的数据权限范围，自动拼接SQL条件
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

        // TODO: 从请求Header或ThreadLocal获取当前用户信息
        // TODO: 根据用户的数据权限（dataScope）动态拼接SQL WHERE条件
        // 1 全部数据权限 - 不添加额外条件
        // 2 自定义数据权限 - dept_id IN (指定的部门ID列表)
        // 3 本部门数据权限 - dept_id = 用户部门ID
        // 4 本部门及以下数据权限 - dept_id IN (本部门及子部门ID列表)
        // 5 仅本人数据权限 - create_by = 用户ID

        log.debug("数据权限拦截器: {}", ms.getId());

        // 执行原始查询
        return invocation.proceed();
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
