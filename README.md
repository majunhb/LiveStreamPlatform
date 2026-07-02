# 直播平台 - LiveStream Platform

<div align="center">

![LiveStream Platform](https://img.shields.io/badge/Platform-LiveStream-blue)
![Java](https://img.shields.io/badge/Java-17+-green)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023-green)
![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

**高性能直播平台 | 微服务架构 | 分布式设计**

</div>

---

## 📖 项目简介

LiveStream Platform 是一款面向未来的高性能直播平台，采用微服务架构设计，支持直播、短视频、弹幕互动、礼物打赏等核心功能。平台基于 Spring Cloud Alibaba 构建，整合了 Nacos、Sentinel、Kafka 等中间件，提供稳定可靠的服务端能力。

### 核心特性

- 🖥️ **实时直播** - 支持 RTMP 推流、HTTP-FLV / HLS / WebRTC 多协议拉流
- 🎬 **短视频** - 短视频上传、编辑、推荐
- 💬 **实时弹幕** - 基于 WebSocket 的低延迟弹幕系统
- 🎁 **礼物系统** - 丰富的礼物特效与打赏机制
- 💰 **虚拟货币** - 完整的钱包、充值、提现体系
- 📊 **数据统计** - 实时在线人数、观看趋势分析
- 🔐 **权限管理** - 基于 RBAC 的后台管理系统

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              客户端层                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  Web 用户端  │  │ 移动 App    │  │ Admin Web   │  │ 第三方推流工具      │ │
│  │  (Vue.js)   │  │  (Flutter)  │  │  (Vue.js)   │  │  (OBS/XiaoMi)       │ │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘ │
└─────────┼────────────────┼────────────────┼───────────────────┼─────────────┘
          │                │                │                   │
          ▼                ▼                ▼                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              网关层 (API Gateway)                            │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                         Spring Cloud Gateway                            │ │
│  │   • 路由转发    • 负载均衡    • 限流熔断    • 统一认证    • 日志记录   │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
          │                │                │                   │
          ▼                ▼                ▼                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              服务层 (Business Services)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐│
│  │ 用户服务  │ │ 直播服务  │ │ 视频服务  │ │ 消息服务  │ │ 支付服务  │ │Admin服务││
│  │ live-user│ │ live-live│ │live-video│ │live-msg  │ │live-gift │ │live-admin││
│  │  :8081   │ │  :8082   │ │  :8083   │ │  :8084   │ │  :8085   │ │ :8086  ││
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └───┬────┘│
└───────┼────────────┼────────────┼────────────┼────────────┼───────────┼─────┘
        │            │            │            │            │           │
        ▼            ▼            ▼            ▼            ▼           ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据层 (Data Layer)                            │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌───────┐ │
│  │ MySQL  │ │ Redis  │ │MongoDB │ │ Kafka  │ │ MinIO  │ │  SRS   │ │ Nacos │ │
│  │  3306  │ │  6379  │ │ 27017  │ │  9092  │ │9000/9001│ │1935等  │ │ 8848  │ │
│  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ └───────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 主力开发语言 |
| Spring Boot | 3.2.x | 基础框架 |
| Spring Cloud Alibaba | 2023.x | 微服务解决方案 |
| Nacos | 2.3.x | 配置中心 & 服务发现 |
| Sentinel | 1.8.x | 流量控制 & 熔断降级 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x | 缓存 & 分布式锁 |
| MongoDB | 7.x | 文档数据库（弹幕） |
| Kafka | 3.6 | 消息队列 |
| MinIO | Latest | 对象存储 |
| SRS | 5.x | 流媒体服务器 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.x | 用户端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.x | UI 组件库 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.x | HTTP 客户端 |

### 基础设施

| 技术 | 说明 |
|------|------|
| Docker & Docker Compose | 容器化部署 |
| Nginx | 反向代理 & 静态资源 |
| GitLab CI/CD | 持续集成（可选） |

---

## 📁 项目结构

```
LiveStreamPlatform/
├── backend/                          # 后端微服务
│   ├── live-gateway/                 # API 网关服务
│   ├── live-common/                  # 公共模块（实体、工具类）
│   ├── live-user/                    # 用户服务
│   ├── live-live/                    # 直播服务
│   ├── live-video/                   # 短视频服务
│   ├── live-message/                 # 消息服务（弹幕）
│   ├── live-gift/                    # 礼物/支付服务
│   └── live-admin/                   # 运营后台服务
│
├── admin-web/                        # 运营管理后台（Vue.js）
│   ├── src/
│   │   ├── api/                      # API 接口定义
│   │   ├── views/                    # 页面视图
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # 状态管理
│   │   └── utils/                    # 工具函数
│   └── package.json
│
├── user-web/                         # 用户端 Web（Vue.js）
│
├── infra/                            # 基础设施配置
│   ├── docker/                       # Docker 配置
│   │   ├── mysql/init.sql           # 数据库初始化脚本
│   │   └── srs/srs.conf             # SRS 流媒体配置
│   └── docker-compose.yml            # 容器编排文件
│
├── docs/                             # 项目文档
│   ├── api/                         # API 接口文档
│   └── manual/                      # 操作手册
│
├── README.md                         # 项目说明文档
├── .gitignore                        # Git 忽略配置
└── LICENSE                           # 开源许可证
```

---

## 🚀 快速开始

### 环境要求

- Docker & Docker Compose
- JDK 17+
- Node.js 18+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/your-org/livestream-platform.git
cd livestream-platform
```

### 2. 启动基础设施

```bash
# 启动所有中间件服务（MySQL、Redis、MongoDB、Nacos、Kafka、MinIO、SRS）
docker-compose up -d

# 查看服务启动状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [service_name]
```

### 3. 初始化数据库

数据库初始化脚本会在 MySQL 容器首次启动时自动执行。

手动执行（如需重新初始化）：

```bash
docker exec -i livestream-mysql mysql -uroot -proot123456 < infra/docker/mysql/init.sql
```

### 4. 配置 Nacos

1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 默认账号密码：`nacos / nacos`
3. 导入配置文件到 `backend/live-*/src/main/resources/nacos/`

### 5. 启动后端服务

```bash
cd backend

# 编译所有服务
mvn clean package -DskipTests

# 启动各个服务（按依赖顺序）
java -jar live-gateway/target/live-gateway.jar &
java -jar live-common/target/live-common.jar &
java -jar live-user/target/live-user.jar &
# ... 其他服务
```

### 6. 启动前端

```bash
# 运营后台
cd admin-web
npm install
npm run dev

# 用户端
cd user-web
npm install
npm run dev
```

---

## 🔌 服务端口汇总

| 服务 | 端口 | 协议 | 说明 |
|------|------|------|------|
| MySQL | 3306 | TCP | MySQL 数据库 |
| Redis | 6379 | TCP | Redis 缓存 |
| MongoDB | 27017 | TCP | MongoDB 数据库 |
| Nacos | 8848 | HTTP | Nacos 控制台 |
| Nacos gRPC | 9848/9849 | gRPC | Nacos 内部通信 |
| ZooKeeper | 2181 | TCP | Kafka 协调服务 |
| Kafka | 9092 | TCP | Kafka 消息队列 |
| MinIO API | 9000 | HTTP | MinIO 对象存储 |
| MinIO Console | 9001 | HTTP | MinIO 管理界面 |
| SRS RTMP | 1935 | RTMP | 直播推流 |
| SRS HTTP-FLV | 8080 | HTTP | FLV 拉流 |
| SRS API | 1985 | HTTP | SRS 管理接口 |
| SRS WebRTC | 8000/500 | UDP | WebRTC 通信 |

---

## 🌐 API 网关路由

| 路由前缀 | 目标服务 | 说明 |
|---------|---------|------|
| `/api/user/**` | live-user:8081 | 用户相关接口 |
| `/api/live/**` | live-live:8082 | 直播相关接口 |
| `/api/video/**` | live-video:8083 | 短视频相关接口 |
| `/api/message/**` | live-message:8084 | 消息/弹幕接口 |
| `/api/gift/**` | live-gift:8085 | 礼物/支付接口 |
| `/api/admin/**` | live-admin:8086 | 运营后台接口 |
| `/ws/**` | live-message:8084 | WebSocket 端点 |

---

## 📝 开发规范

### Git 提交规范

```
<type>(<scope>): <subject>

# 类型说明
feat:     新功能
fix:      Bug 修复
docs:     文档更新
style:    代码格式调整
refactor: 重构
test:     测试相关
chore:    构建/工具变动
```

示例：
```
feat(user): 添加用户注册短信验证码功能
fix(live): 修复直播间在线人数统计不准确问题
```

### 代码规范

- 命名遵循 Java 命名规范
- Controller 层只做参数校验和响应封装
- Service 层处理业务逻辑
- Mapper 层处理数据库交互
- 异常统一使用 GlobalExceptionHandler 处理

### API 设计规范

- RESTful 风格 URL
- 请求方法：GET/POST/PUT/DELETE
- 统一响应格式：
```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

---

## 📚 相关文档

- [SRS 流媒体服务器文档](https://ossrs.net/lts/zh-cn/)
- [Nacos 配置中心文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zhcn/)
- [MinIO 对象存储文档](https://min.io/docs/minio/linux/index.html)

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 License

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给我们一个 Star！**

Made with ❤️ by LiveStream Team

</div>
