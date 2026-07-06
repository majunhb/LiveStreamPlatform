# 安全修复变更记录 (P0 + P1)

**修复日期**: 2026-07-05  
**分支**: fix/security-p0-p1  
**修复范围**: 10个P0致命漏洞 + 6个P1严重漏洞

---

## P0 致命问题修复 (10项)

### C-01: 密码明文存储
- **文件**: UserServiceImpl.java, live-service-user/pom.xml
- **修复**: 引入 spring-security-crypto 依赖，注册时使用 BCryptPasswordEncoder.encode() 加密密码，登录时使用 matches() 比对

### C-02: JWT密钥硬编码
- **文件**: JwtUtil.java, common.yml
- **修复**: 改为从环境变量 JWT_SECRET 读取密钥，配置文件中使用 ${JWT_SECRET:} 引用，未配置时生成随机密钥并告警

### C-03: CORS全放开
- **文件**: CorsConfig.java, gateway/application.yml
- **修复**: 限定特定来源域名（通过 CORS_ALLOWED_ORIGINS 环境变量配置），移除 gateway 全局 CORS 配置

### C-04: URL参数Token传递
- **文件**: AuthFilter.java
- **修复**: 移除从查询参数 ?token=xxx 获取 Token 的逻辑，仅支持 Authorization 请求头

### C-05: 密码硬编码
- **文件**: 所有 application.yml, docker-compose.yml, .env.example
- **修复**: 所有数据库/中间件密码改为 ${ENV_VAR} 环境变量引用，新建 .env.example 模板

### C-06: Nacos认证关闭
- **文件**: docker-compose.yml
- **修复**: 设置 NACOS_AUTH_ENABLE=true，添加 NACOS_AUTH_IDENTITY_KEY/VALUE

### C-07: 弹幕XSS
- **文件**: DanmakuServiceImpl.java, live-service-message/pom.xml
- **修复**: 引入 jsoup 1.17.2 依赖，sendDanmaku() 中使用 Jsoup.clean(content, Safelist.none()) 过滤HTML标签，限制200字符

### C-08: 管理后台无鉴权
- **文件**: AuthFilter.java, AdminAuthInterceptor.java(新增), WebMvcConfig.java(新增)
- **修复**: 网关 AuthFilter 传递 X-User-Role 头并校验管理路径角色，Admin 服务新增拦截器校验管理员身份

### C-09: MongoDB正则注入
- **文件**: FeedServiceImpl.java
- **修复**: searchVideos() 中使用 Pattern.quote() 转义用户输入的正则特殊字符

### C-10: 异常信息泄露
- **文件**: GlobalExceptionHandler.java
- **修复**: 未知异常统一返回 "系统繁忙，请稍后重试"，不暴露原始异常信息

---

## P1 严重问题修复 (6项)

### S-01: 钱包TOCTOU竞态条件
- **文件**: GiftServiceImpl.java, WalletMapper.java
- **修复**: 移除 checkBalance + deduct 两步操作，改为直接调用原子SQL deductBalance（WHERE coin_balance >= amount），返回0即余额不足

### S-02: Redis锁不安全
- **文件**: WalletServiceImpl.java
- **修复**: 使用 UUID 作为锁值，Lua 脚本比对后释放（CAS模式），防止误删其他线程持有的锁

### S-05: 连接池未配置
- **文件**: user/gift/live/video/admin 的 application.yml
- **修复**: 所有 MySQL 服务补充 HikariCP 参数（max 20, min 5, idle-timeout 300s, max-lifetime 1200s）

### S-06: SQL日志生产开启
- **文件**: live-service-user/application.yml
- **修复**: log-impl 改为 ${MYBATIS_LOG_IMPL:NoLoggingImpl}，开发环境可通过环境变量开启

### S-07: Actuator过度暴露
- **文件**: live-gateway/application.yml
- **修复**: 仅暴露 health 端点，show-details 改为 when-authorized

### S-10: 敏感数据泄露
- **文件**: User.java, UserVO.java(新增), UserController.java
- **修复**: User 实体敏感字段（password/phone/email/lastLoginIp）加 @JsonIgnore，password 加 @TableField(select=false)，UserController 返回 UserVO

---

## 基础设施补充

- 所有 *Application.java 补充 scanBasePackages="com.livestream"
- 修复空的 UserApplication.java 和 GatewayApplication.java
- .env.example 环境变量模板已创建

---

## 变更文件清单

### 修改文件
1. live-backend/live-common/src/main/java/com/livestream/common/exception/GlobalExceptionHandler.java
2. live-backend/live-common/src/main/java/com/livestream/common/util/JwtUtil.java (已含外部化)
3. live-backend/live-gateway/src/main/java/com/livestream/gateway/filter/AuthFilter.java
4. live-backend/live-gateway/src/main/java/com/livestream/config/CorsConfig.java (已含限定)
5. live-backend/live-gateway/src/main/resources/application.yml
6. live-backend/live-service-user/src/main/java/com/livestream/user/entity/User.java
7. live-backend/live-service-user/src/main/java/com/livestream/user/controller/UserController.java
8. live-backend/live-service-user/src/main/java/com/livestream/user/service/impl/UserServiceImpl.java
9. live-backend/live-service-user/src/main/resources/application.yml
10. live-backend/live-service-gift/src/main/java/com/livestream/gift/service/impl/GiftServiceImpl.java
11. live-backend/live-service-gift/src/main/java/com/livestream/gift/service/impl/WalletServiceImpl.java
12. live-backend/live-service-gift/src/main/resources/application.yml
13. live-backend/live-service-video/src/main/java/com/livestream/video/service/impl/FeedServiceImpl.java
14. live-backend/live-service-video/src/main/resources/application.yml
15. live-backend/live-service-live/src/main/resources/application.yml
16. live-backend/live-service-admin/src/main/resources/application.yml
17. live-backend/live-service-message/src/main/java/com/livestream/message/service/impl/DanmakuServiceImpl.java
18. live-backend/live-service-message/pom.xml
19. docker-compose.yml
20. .env.example

### 新增文件
1. live-backend/live-service-user/src/main/java/com/livestream/user/vo/UserVO.java
2. live-backend/live-service-admin/src/main/java/com/livestream/admin/interceptor/AdminAuthInterceptor.java
3. live-backend/live-service-admin/src/main/java/com/livestream/admin/config/WebMvcConfig.java
4. CHANGELOG-FIX.md

### 修复文件
1. live-backend/live-service-user/src/main/java/com/livestream/user/UserApplication.java (原为空)
2. live-backend/live-gateway/src/main/java/com/livestream/gateway/GatewayApplication.java (原为空)
3. 所有 *Application.java 补充 scanBasePackages
