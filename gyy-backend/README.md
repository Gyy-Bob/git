# gyy-backend 后端骨架

> 设计文档见 `docs/backend/`（架构 / 接口规范 / MySQL 连接）。本工程为文档的第一个落地实现。

## 模块

| 模块 | 类型 | 端口 | 说明 |
| --- | --- | --- | --- |
| gyy-common | 库 | — | Result / PageResult / ErrorCode / 全局异常 |
| gyy-api | 库 | — | Feign 契约（UserClient）+ DTO |
| gyy-gateway | 应用 | 8080 | 网关，dev 静态路由到 8101 |
| gyy-services/gyy-user-service | 应用 | 8101 | 用户服务：user_db CRUD + 头像 |

## 环境

- JDK 21（`JAVA_HOME=D:\Tools\jdk-21`）
- Maven 3.9.16（`MAVEN_HOME=D:\Tools\apache-maven-3.9.16`，`~/.m2/settings.xml` 已配阿里云镜像）
- MySQL 8.4.13 本机服务 MySQL84（连接参数见 `docs/backend/03-MySQL连接配置.md`）

## 本机私密配置（勿提交）

`gyy-services/gyy-user-service/src/main/resources/application-local.yml` 已在 `.gitignore`
（规则 `application-local.yml`）。克隆仓库后需手工创建：

```yaml
spring:
  datasource:
    username: root
    password: "<你的密码>"
```

## 常用命令

```bash
# 全量构建（首次会从阿里云镜像拉依赖）
mvn -f gyy-backend/pom.xml clean package -DskipTests

# 启动用户服务（默认 local profile）
java -jar gyy-services/gyy-user-service/target/gyy-user-service-1.0.0-SNAPSHOT.jar

# 启动网关
java -jar gyy-gateway/target/gyy-gateway-1.0.0-SNAPSHOT.jar

# 接口
# 直连:      http://127.0.0.1:8101/api/v1/users
# 经网关:    http://127.0.0.1:8080/api/v1/users
# OpenAPI:   http://127.0.0.1:8101/v3/api-docs  → Apifox 定时导入
```

## 接口清单

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | /api/v1/users?pageNum=1&pageSize=10&userName=xx | 分页（排除大字段） |
| GET | /api/v1/users/{userId} | 详情（hasAvatar 标记） |
| POST | /api/v1/users | 新增 |
| PUT | /api/v1/users/{userId} | 更新（可选字段） |
| DELETE | /api/v1/users/{userId} | 删除（物理删除，逻辑删除字段就绪后改造） |
| POST | /api/v1/users/{userId}/avatar | 上传头像（≤16MB 存 LONGBLOB） |
| GET | /api/v1/users/{userId}/avatar | 下载头像原始字节 |
