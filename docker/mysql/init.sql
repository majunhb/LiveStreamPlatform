-- ============================================================
-- 直播平台数据库初始化脚本
-- 创建时间: 2024
-- 描述: 初始化所有业务数据库和表结构
-- ============================================================

-- 设置时区和字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. live_user 库 - 用户服务
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `live_user`;

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. live_live 库 - 直播服务
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_live` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `live_live`;

-- 直播间表
DROP TABLE IF EXISTS `t_live_room`;
CREATE TABLE `t_live_room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '直播间ID',
    `user_id` BIGINT NOT NULL COMMENT '主播用户ID',
    `title` VARCHAR(255) NOT NULL COMMENT '直播间标题',
    `cover` VARCHAR(512) DEFAULT NULL COMMENT '直播间封面图',
    `category` VARCHAR(64) DEFAULT NULL COMMENT '直播分类',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未开播, 1-直播中, 2-已封禁',
    `online_count` INT DEFAULT 0 COMMENT '在线人数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='直播间表';

-- ============================================================
-- 3. live_video 库 - 短视频服务
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_video` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `live_video`;

-- 短视频表
DROP TABLE IF EXISTS `t_short_video`;
CREATE TABLE `t_short_video` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视频ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(255) NOT NULL COMMENT '视频标题',
    `description` TEXT COMMENT '视频描述',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面URL',
    `video_url` VARCHAR(512) NOT NULL COMMENT '视频URL',
    `duration` INT DEFAULT 0 COMMENT '视频时长(秒)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-待审核, 1-已发布, 2-已下架',
    `view_count` BIGINT DEFAULT 0 COMMENT '观看次数',
    `like_count` BIGINT DEFAULT 0 COMMENT '点赞次数',
    `comment_count` BIGINT DEFAULT 0 COMMENT '评论次数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短视频表';

-- ============================================================
-- 4. live_message 库 - 消息服务（弹幕）
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_message` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `live_message`;

/*
-- MongoDB Collection: live_danmaku (弹幕表)
-- 连接方式: mongodb://mongo:27017/live_message
-- 使用 MongoDB Compass 或 mongosh 执行以下命令创建 Collection:

use live_message;
db.createCollection("t_danmaku", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["room_id", "user_id", "content", "created_time"],
         properties: {
            room_id: { bsonType: "long" },
            user_id: { bsonType: "long" },
            content: { bsonType: "string" },
            type: { bsonType: "int", minimum: 0, maximum: 10 },
            color: { bsonType: "string" },
            created_time: { bsonType: "date" }
         }
      }
   }
});

-- 创建索引
db.t_danmaku.createIndex({ room_id: 1, created_time: -1 });
db.t_danmaku.createIndex({ user_id: 1 });
*/

-- 为保持 SQL 完整性，创建一个注释表
DROP TABLE IF EXISTS `t_danmaku_note`;
CREATE TABLE `t_danmaku_note` (
    `id` BIGINT NOT NULL COMMENT '占位表，用于说明弹幕数据存储在 MongoDB',
    `note` VARCHAR(500) DEFAULT '弹幕数据存储在 MongoDB live_message.t_danmaku Collection'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹幕表说明（实际存储在MongoDB）';

-- ============================================================
-- 5. live_gift 库 - 礼物与钱包服务
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_gift` DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE `live_gift`;

-- 礼物表
DROP TABLE IF EXISTS `t_gift`;
CREATE TABLE `t_gift` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '礼物ID',
    `name` VARCHAR(64) NOT NULL COMMENT '礼物名称',
    `price` INT NOT NULL DEFAULT 0 COMMENT '价格(钻石)',
    `icon` VARCHAR(512) DEFAULT NULL COMMENT '礼物图标URL',
    `animation` VARCHAR(512) DEFAULT NULL COMMENT '动画效果配置',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='礼物表';

-- 用户钱包表
DROP TABLE IF EXISTS `t_wallet`;
CREATE TABLE `t_wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `balance` DECIMAL(12,2) DEFAULT 0.00 COMMENT '余额(元)',
    `diamond` BIGINT DEFAULT 0 COMMENT '钻石数量',
    `total_recharge` DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计充值金额',
    `total_withdraw` DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计提现金额',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';

-- 礼物记录表
DROP TABLE IF EXISTS `t_gift_record`;
CREATE TABLE `t_gift_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `room_id` BIGINT NOT NULL COMMENT '直播间ID',
    `from_user_id` BIGINT NOT NULL COMMENT '送礼用户ID',
    `to_user_id` BIGINT NOT NULL COMMENT '收礼用户ID(主播)',
    `gift_id` BIGINT NOT NULL COMMENT '礼物ID',
    `count` INT NOT NULL DEFAULT 1 COMMENT '礼物数量',
    `price` INT NOT NULL COMMENT '单次总价(钻石)',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '赠送时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_from_user_id` (`from_user_id`),
    KEY `idx_to_user_id` (`to_user_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='礼物赠送记录表';

-- 提现记录表
DROP TABLE IF EXISTS `t_withdraw`;
CREATE TABLE `t_withdraw` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提现ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注/拒绝原因',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- ============================================================
-- 6. live_admin 库 - 运营管理系统
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE `live_admin`;

-- 角色表
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `permissions` TEXT COMMENT '权限JSON',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 管理员表
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 初始化超级管理员角色
INSERT INTO `t_role` (`id`, `name`, `permissions`, `status`, `created_time`) VALUES
(1, '超级管理员', '{"all": true}', 1, NOW()),
(2, '运营管理员', '{"user:view": true, "user:edit": true, "room:view": true, "room:manage": true, "video:audit": true}', 1, NOW());

-- 初始化管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `t_admin` (`username`, `password`, `real_name`, `role_id`, `status`, `created_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1, NOW());

-- ============================================================
-- 7. live_nacos 库 - Nacos 配置中心
-- ============================================================
CREATE DATABASE IF NOT EXISTS `live_nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 初始化默认礼物数据
-- ============================================================
USE `live_gift`;
INSERT INTO `t_gift` (`name`, `price`, `icon`, `animation`, `status`, `sort_order`) VALUES
('小花', 1, '/icons/gift_flower.png', 'flower_anim.json', 1, 1),
('掌声', 5, '/icons/gift_clap.png', 'clap_anim.json', 1, 2),
('火箭', 100, '/icons/gift_rocket.png', 'rocket_anim.json', 1, 3),
('城堡', 500, '/icons/gift_castle.png', 'castle_anim.json', 1, 4),
('飞船', 1000, '/icons/gift_spaceship.png', 'spaceship_anim.json', 1, 5);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 初始化完成
-- ============================================================
