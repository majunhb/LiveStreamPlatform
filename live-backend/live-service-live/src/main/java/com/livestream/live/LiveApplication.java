package com.livestream.live;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 直播服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.livestream.live.mapper")
@SpringBootApplication
public class LiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  直播服务启动成功，端口: 8082");
        System.out.println("===========================================");
    }
}
