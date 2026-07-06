package com.livestream.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关启动类
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.livestream")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  API网关启动成功，端口: 8080");
        System.out.println("===========================================");
    }
}
