package com.enterprisex.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统服务启动类
 *
 * @author EnterpriseX
 */
@SpringBootApplication(scanBasePackages = "com.enterprisex")
@EnableDiscoveryClient
@MapperScan("com.enterprisex.system.mapper")
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("   EnterpriseX 系统服务启动成功！");
        System.out.println("   API文档地址: http://localhost:9201/doc.html");
        System.out.println("========================================");
    }
}
