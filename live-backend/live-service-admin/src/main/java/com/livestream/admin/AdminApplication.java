package com.livestream.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 管理后台服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.livestream.admin.mapper")
@SpringBootApplication(scanBasePackages = "com.livestream")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  管理后台服务启动成功，端口: 8086");
        System.out.println("===========================================");
    }
}
