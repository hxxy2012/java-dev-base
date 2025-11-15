package com.enterprisex.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 *
 * @author EnterpriseX
 */
@SpringBootApplication(scanBasePackages = "com.enterprisex")
@EnableDiscoveryClient
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("========================================");
        System.out.println("   EnterpriseX 认证服务启动成功！");
        System.out.println("   认证服务地址: http://localhost:9200");
        System.out.println("========================================");
    }
}
