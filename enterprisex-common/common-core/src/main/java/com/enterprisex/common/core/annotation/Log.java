package com.enterprisex.common.core.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author EnterpriseX
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 业务类型（0其它 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8清空）
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;
}
