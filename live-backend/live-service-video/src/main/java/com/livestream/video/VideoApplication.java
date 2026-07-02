package com.livestream.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 短视频服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.livestream.video.mapper")
@SpringBootApplication
public class VideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  短视频服务启动成功，端口: 8083");
        System.out.println("===========================================");
    }
}
