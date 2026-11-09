# 后端技术文档（索引）

> 项目：用户管理系统后端 · 语言：Java · 基础框架：Spring Boot · 架构：分布式微服务
> 接口管理：Apifox · 数据库：MySQL 8.4.13（本机 `127.0.0.1:3306`，库 `user`）

## 文档目录

| 编号 | 文档 | 内容 |
| --- | --- | --- |
| 00 | [README.md](./README.md) | 索引、技术栈总表、全局约定 |
| 01 | [01-分布式架构设计.md](./01-分布式架构设计.md) | 服务拆分、注册中心、网关、服务间调用、分布式事务、部署 |
| 02 | [02-接口规范与Apifox.md](./02-接口规范与Apifox.md) | RESTful 规范、统一响应体、异常处理、Apifox 集成与协作规范 |
| 03 | [03-MySQL连接配置.md](./03-MySQL连接配置.md) | 数据源配置、连接池、MyBatis-Plus、连接自检、错误排查 |

## 技术栈总表

| 分类 | 组件 | 版本基线 | 用途 |
| --- | --- | --- | --- |
| 语言 | Java | **21 LTS** | Boot 3.x 基线，虚拟线程可用 |
| 核心框架 | Spring Boot | **3.5.x** | 应用骨架；4.0 生态尚未稳定，暂不采用 |
| 微服务 | Spring Cloud | **2025.0.x** | 与 Boot 3.5 对应 |
| 注册/配置中心 | Spring Cloud Alibaba（Nacos） | 与 Spring Cloud 版本匹配（见官方兼容表，取最新稳定） | 服务注册发现 + 配置中心 |
| 网关 | Spring Cloud Gateway | 随 Spring Cloud | 统一入口、鉴权、限流 |
| 服务调用 | OpenFeign + LoadBalancer | 随 Spring Cloud | 声明式 HTTP 调用 + 客户端负载均衡 |
| 分布式事务 | Seata | 2.x | AT 模式为主 |
| 消息队列 | RocketMQ | 5.x | 异步解耦、最终一致性 |
| ORM | MyBatis-Plus | 3.5.x | CRUD、分页、代码生成 |
| 数据库 | MySQL Community Server | **8.4.13 LTS**（已装，服务名 MySQL84） | 持久化 |
| JDBC 驱动 | `com.mysql:mysql-connector-j` | 8.4.x / 9.x | **必须**支持 `caching_sha2_password`（8.4 已移除 mysql_native_password） |
| 连接池 | HikariCP | Boot 内置 | 默认连接池 |
| 缓存 | Redis（Lettuce）+ Redisson | Redis 7.x | 缓存、分布式锁 |
| 接口文档 | springdoc-openapi 2.x + **Apifox** | — | OpenAPI 3 自动生成，Apifox 统一管理/Mock/测试 |
| 链路追踪 | Micrometer Tracing + Zipkin | — | Boot 3 官方观测方案 |
| 构建工具 | Maven | 3.9.x | 多模块聚合 |
| 部署 | Docker Compose（开发）/ Kubernetes（生产） | — | 基础设施容器化 |

> **版本纪律**：Spring Boot / Spring Cloud / Spring Cloud Alibaba 三者必须按官方兼容表配套升级，禁止只升其一。升级前在 `dev` 环境回归。

## 全局约定

1. **groupId**：`com.gyy`；模块命名 `gyy-<module>`（如 `gyy-user-service`）。
2. **包结构**：`com.gyy.<service>.{controller,service,mapper,entity,config,feign}`。
3. **接口路径**：`/api/v1/<资源>`，统一由网关前缀路由。
4. **响应格式**：统一 `Result<T>{code, message, data, traceId}`，禁止各服务自定义。
5. **数据库命名**：库名/表名**一律小写**（本机 Windows `lower_case_table_names=1` 强制小写，未来迁 Linux 不踩大小写坑）；表字段蛇形命名。
6. **敏感信息**：密码等只放 `.env` / `application-local.yml`（均已在 `.gitignore`），文档中一律用 `${ENV_VAR}` 占位。
