package com.enterprisex.common.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 *
 * @author EnterpriseX
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EnterpriseX Framework API文档")
                        .description("企业级微服务开发框架 - 生产就绪的商业级解决方案")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EnterpriseX Team")
                                .email("support@enterprisex.com")
                                .url("https://www.enterprisex.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
