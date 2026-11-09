# 03 - MySQL 连接配置（连接本机 MySQL 8.4.13）

> 本文档对应**当前已安装**的数据库环境。密码等敏感值只存在于 `.env` / `application-local.yml`（均已 gitignore），文档中一律用 `${ENV}` 占位。

## 1. 当前数据库环境（事实清单）

| 项目 | 值 | 说明 |
| --- | --- | --- |
| 版本 | MySQL Community Server **8.4.13 LTS** | `SELECT VERSION()` 已验证 |
| 部署方式 | Windows 服务 **MySQL84**，开机自启 | 启停：`Get-Service MySQL84` / `Start-Service MySQL84` |
| 监听 | `127.0.0.1:3306`（TCP） | 命令行连接需 `--protocol=TCP` |
| 安装目录 | `C:\Program Files\MySQL\MySQL Server 8.4` | |
| 数据目录 | `C:\ProgramData\MySQL\MySQL Server 8.4\Data` | |
| 配置文件 | `C:\ProgramData\MySQL\MySQL Server 8.4\my.ini` | utf8mb4 / 东八区 / max_connections=200 |
| 目标库 | `user` | **小写**（Windows `lower_case_table_names=1`，元数据里建的是 `User` 但实际存为 `user`） |
| 已有表 | `user_db` | 见 `SQL/user_db.md`；字段 user_id/user_name/user_age/user_img(LONGBLOB)/user_note(LONGTEXT) |
| 账号 | `root` / 密码见 `.env` 的 `MYSQL_PASSWORD` | 认证插件 `caching_sha2_password`（8.4 已移除 mysql_native_password，**不可回退**） |

本机凭据文件 `D:\Project\JAVA\.env`（**已被 .gitignore 忽略**）：

```ini
MYSQL_HOST=127.0.0.1
MYSQL_PORT=3306
MYSQL_DATABASE=user
MYSQL_USERNAME=root
MYSQL_PASSWORD=<见本机 .env，勿提交>
```

## 2. Maven 依赖

```xml
<!-- 注意：8.0.31 起坐标改为 com.mysql:mysql-connector-j（旧 mysql:mysql-connector-java 已废弃） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>   <!-- 版本由 spring-boot-dependencies 托管 -->
</dependency>

<!-- ORM（可选，推荐） -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.x</version>
</dependency>
```

> **版本红线**：驱动必须 ≥ 8.0（支持 `caching_sha2_password`）。任何 `mysql:mysql-connector-java:5.x/8.0.30 以下` 的写法在 8.4 上**必然连接失败**。

## 3. Spring Boot 数据源配置

### 3.1 公共配置 `application.yml`（可提交）

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:user}?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&connectTimeout=3000&socketTimeout=60000
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true   # user_name -> userName
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl   # 仅 dev
  global-config:
    db-config:
      logic-delete-field: isDeleted       # 配合 02 文档的可选字段
      logic-delete-value: 1
      logic-not-delete-value: 0
```

URL 参数逐项说明：

| 参数 | 值 | 原因 |
| --- | --- | --- |
| `characterEncoding=utf8` | — | 连接层 UTF-8，配合库级 utf8mb4，Emoji 正常（已实测） |
| `useSSL=false` | — | 本机连接，免去证书协商 |
| `allowPublicKeyRetrieval=true` | — | **8.4 必须**：`caching_sha2_password` 非 SSL 首连需要取 RSA 公钥，否则报 `Public Key Retrieval is not allowed` |
| `serverTimezone=Asia/Shanghai` | — | 驱动时间戳转换；服务端默认时区已是 `+08:00` |
| `rewriteBatchedStatements=true` | — | 批量插入合并执行，配 LONGBLOB 批量场景提速明显 |
| `connectTimeout/socketTimeout` | 3s/60s | 分布式下防止连接悬挂拖垮线程池 |

### 3.2 本机私密配置 `application-local.yml`（**gitignore，勿提交**）

```yaml
spring:
  datasource:
    username: root
    password: "qwe12345678"   # 与本机 .env 保持一致；此处仅为格式示例，真实文件已在本机
```

激活方式：`spring.profiles.active=local`（dev 默认），或 IDEA Run Configuration 加 `--spring.profiles.active=local`。

### 3.3 连接池 HikariCP（Boot 内置）

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10        # 单服务 10~20；= 核心数*2 + 磁盘数 是经验起点
      minimum-idle: 5
      connection-timeout: 5000     # 拿不到连接 5s 报错，快速失败
      max-lifetime: 1800000        # 30min，必须小于 MySQL wait_timeout（默认 8h）
      idle-timeout: 600000
      pool-name: user-service-pool
```

> 8 个微服务 × 10 连接 = 80，远小于 `max_connections=200`，留足余量；后续服务增多时按此约束扩容。

## 4. 连接自检（每个服务集成一次）

```java
@Component
public class DatasourceSelfCheck implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;
    public DatasourceSelfCheck(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public void run(ApplicationArguments args) {
        String ver = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        Long rows  = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_db", Long.class);
        log.info("[自检] MySQL {} 连接成功，user_db 现有 {} 行", ver, rows);
    }
}
```

启动日志出现 `[自检] MySQL 8.4.13 连接成功` 即代表：网络通 → 认证通 → 库表存在 → 驱动正确，四层全部通过。

## 5. 常见错误速查

| 报错 | 根因 | 解决 |
| --- | --- | --- |
| `Access denied for user 'root'@'localhost' (using password: YES)` (1045) | 密码错 | 核对 `.env` / `application-local.yml` |
| `Public Key Retrieval is not allowed` | 未开公钥检索 | URL 加 `allowPublicKeyRetrieval=true` |
| `Client does not support authentication protocol` / `mysql_native_password cannot be loaded` | 驱动太老 | 升级 `mysql-connector-j ≥ 8.0`；**8.4 无法改回旧认证插件** |
| `Communications link failure` | MySQL84 服务未启动 | `Start-Service MySQL84`（需管理员） |
| `Unknown database 'User'` | 大小写 | Windows 下库名实际是**小写** `user` |
| `The server time zone value ...` | 未指定时区 | URL 加 `serverTimezone=Asia/Shanghai` |
| `ERROR 1406 Data too long` | 图片字节超限 | LONGBLOB 上限 4GB，但受 `max_allowed_packet`（默认 64MB）约束；大文件走对象存储 |
| `ERROR 3819 Check constraint violated` | `user_age` 超出 0–150 | 正常业务拦截，前端先校验 |
| `Table 'user.user_db' doesn't exist`（迁 Linux 后） | Linux 区分大小写 | 库表名已统一小写，保持即可 |

## 6. 分布式多数据源（服务扩容后）

- **默认一服务一库**（见 01 文档），`user-service` 只连 `user` 库，**不配多数据源**。
- 确需单服务连多库时，用 `dynamic-datasource-spring-boot3-starter` + `@DS("slave")` 注解切换；跨库写入走 Seata（各库建 `undo_log`），不要手写补偿。
- 所有库的账号**禁止用 root**：为每个服务建独立账号并最小授权，例如：

```sql
CREATE USER 'user_svc'@'%' IDENTIFIED BY '<强密码>';
GRANT SELECT, INSERT, UPDATE, DELETE ON `user`.* TO 'user_svc'@'%';
FLUSH PRIVILEGES;
```

> 应用侧账号迁移到 `user_svc` 后，`MYSQL_USERNAME` 同步替换；root 仅保留给运维与 DDL（Flyway 迁移执行账号）。

## 7. 演进路线

1. 当前：单机 MySQL84 + 单服务（user-service）。
2. 第二步：接 Flyway 管理 DDL（`SQL/user_db.sql` 作为 V1 基线），表结构变更全部走版本化迁移脚本。
3. 第三步：读写分离/云数据库时，仅改 URL 与账号，Hikari 与代码不动。
