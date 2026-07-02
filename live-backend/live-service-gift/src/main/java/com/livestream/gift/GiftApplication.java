package com.livestream.gift;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 礼物/支付服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.livestream.gift.mapper")
@SpringBootApplication
public class GiftApplication {

    public static void main(String[] args) {
        SpringApplication.run(GiftApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  礼物/支付服务启动成功，端口: 8085");
        System.out.println("===========================================");
    }
}
