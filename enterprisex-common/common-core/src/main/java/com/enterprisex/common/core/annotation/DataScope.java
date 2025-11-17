package com.enterprisex.common.core.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author EnterpriseX
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    String deptAlias() default "";

    /**
     * 用户表的别名
     */
    String userAlias() default "";

    /**
     * 是否启用数据权限过滤
     */
    boolean enabled() default true;
}
