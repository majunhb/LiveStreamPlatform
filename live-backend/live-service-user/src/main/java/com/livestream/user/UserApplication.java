package com.livestream.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.livestream.user.mapper")
@SpringBootApplication(scanBasePackages = "com.livestream")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  用户服务启动成功，端口: 8081");
        System.out.println("===========================================");
    }
}
