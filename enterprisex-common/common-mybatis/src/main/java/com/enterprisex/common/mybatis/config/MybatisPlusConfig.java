package com.enterprisex.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 *
 * @author EnterpriseX
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(500L); // 单页分页条数限制
        paginationInnerInterceptor.setOverflow(false); // 溢出总页数后是否进行处理

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}
