# 用户表 `user_db` 表结构设计文档

| 项目 | 内容 |
| --- | --- |
| 文档版本 | v1.1 |
| 更新日期 | 2026-11-10 |
| 适用数据库 | MySQL Community Server 8.4.13 |
| 部署实例 | `127.0.0.1:3306`，目标库 `user` |
| 存储引擎 | InnoDB |
| 字符集 / 排序规则 | `utf8mb4` / `utf8mb4_0900_ai_ci` |
| 行格式 | `DYNAMIC`（大字段可页外存储） |

---

## 1. 表概述

| 项目 | 说明 |
| --- | --- |
| 表名 | `user_db` |
| 中文名 | 用户信息表 |
| 用途 | 存储用户基础信息，包含姓名、年龄、头像图片与备注 |
| 主键策略 | 自增 `BIGINT UNSIGNED`（代理键，与业务无关） |
| 关联表 | 暂无外键关联（建议业务层维护，不建物理外键） |
| 预估数据量 | 十万级以内；单行含大字段，需关注表空间增长 |

---

## 2. 字段定义

| 序号 | 字段名 | 中文名 | 数据类型 | 允许 NULL | 默认值 | 键 / 索引 | 说明 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | `user_id` | 用户 ID | `BIGINT UNSIGNED` | 否 | `AUTO_INCREMENT` | **PK** | 主键，自增，无业务含义；最大约 1844 京，永不溢出 |
| 2 | `user_name` | 账号/姓名 | `VARCHAR(50)` | 否 | 无 | **UNIQUE** | 兼作登录账号，全库唯一（`uk_user_name`）；最长 50 字符 |
| 3 | `user_age` | 年龄 | `TINYINT UNSIGNED` | 是 | `NULL` | — | 取值 0–255，业务上限制 0–150（CHECK 约束）；1 字节，比 INT 省 3 字节 |
| 4 | `user_img` | 图片 | `LONGBLOB` | 是 | `NULL` | — | **存图片原始字节**；上限 4 GB。<br>⚠️ 原方案用 `TEXT` 有数据损坏风险，详见 5.1 |
| 5 | `user_note` | 备注 | `LONGTEXT` | 是 | `NULL` | — | 大文本，上限 4 GB；按 `utf8mb4` 存储，支持 Emoji 与生僻字 |
| 6 | `user_password` | 登录密码 | `VARCHAR(100)` | 否 | `''` | — | **只存 BCrypt 哈希（60 字符）**，任何接口不回传 |
| 7 | `created_at` | 注册时间 | `DATETIME(3)` | 否 | `CURRENT_TIMESTAMP(3)` | **IDX** | 毫秒精度，报表按此统计 |
| 8 | `updated_at` | 更新时间 | `DATETIME(3)` | 否 | `CURRENT_TIMESTAMP(3)` 自动更新 | — | — |
| 9 | `is_deleted` | 逻辑删除 | `TINYINT(1)` | 否 | `0` | — | 0 正常 / 1 已删；报表口径过滤 `= 0` |
| 10 | `status` | 账号状态 | `TINYINT` | 否 | `1` | — | 1 启用 / 0 禁用；禁用账号不能登录（错误码 40302） |

### 字段类型选型依据

- **`user_id` 用 `BIGINT` 而非 `INT`**：`INT UNSIGNED` 上限约 42.9 亿，用户表长期累积（尤其含软删除历史）可能触顶，迁移主键类型的代价极高，一次选对更划算。若确定是小规模内部系统，`INT UNSIGNED` 亦可。
- **`user_name` 用 `VARCHAR(50)`**：中文姓名通常 2–4 字，50 字符足以容纳少数民族姓名、带后缀的英文名；`VARCHAR` 变长，不像 `CHAR` 会补空格浪费空间。
- **`user_age` 用 `TINYINT UNSIGNED`**：年龄范围天然很小，1 字节即可；配合 CHECK 约束在数据库层兜底。
- **`user_note` 用 `LONGTEXT`**：符合"大字符"的诉求，`TEXT` 只有 64 KB 不够用。

---

## 3. 索引设计

| 索引名 | 类型 | 字段 | 说明 |
| --- | --- | --- | --- |
| `PRIMARY` | 聚簇索引 | `user_id` | InnoDB 主键即数据组织方式，自增可避免页分裂 |
| `uk_user_name` | 唯一 B+Tree | `user_name` | `user_name` 兼作登录账号，全库唯一；注册查重由应用层 + 唯一索引双兜底 |
| `idx_created_at` | 普通 B+Tree | `created_at` | 报表按注册时间过滤/统计 |

---

## 4. 建表语句

```sql
CREATE TABLE `user_db` (
  `user_id`       BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '用户ID，主键自增',
  `user_name`     VARCHAR(50)      NOT NULL                 COMMENT '账号/姓名，全库唯一',
  `user_age`      TINYINT UNSIGNED          DEFAULT NULL    COMMENT '年龄，业务范围 0-150',
  `user_img`      LONGBLOB                  DEFAULT NULL    COMMENT '图片，存图片原始字节码',
  `user_note`     LONGTEXT                  DEFAULT NULL    COMMENT '备注，大字符',
  `user_password` VARCHAR(100)     NOT NULL DEFAULT ''      COMMENT '登录密码，存 BCrypt 哈希，禁止明文',
  `created_at`    DATETIME(3)      NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '注册时间',
  `updated_at`    DATETIME(3)      NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted`    TINYINT(1)       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0正常 1已删',
  `status`        TINYINT          NOT NULL DEFAULT 1       COMMENT '账号状态：1启用 0禁用',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `chk_user_age` CHECK (`user_age` IS NULL OR (`user_age` >= 0 AND `user_age` <= 150))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC
  COMMENT = '用户信息表';
```

> MySQL 8.0.16 起 `CHECK` 约束才真正生效（此前只是语法解析）。8.4 完整支持，非法年龄会直接报 `ERROR 3819`。

---

## 5. 关键设计说明

### 5.1 `user_img` 为什么不能直接用 `TEXT` 存字节码

这是本次设计中最需要纠正的一点。`TEXT` 是**字符类型**，`BLOB` 是**二进制类型**，两者在存储层的处理完全不同：

| 对比项 | `TEXT` / `LONGTEXT` | `BLOB` / `LONGBLOB` |
| --- | --- | --- |
| 数据性质 | 字符序列 | 原始字节序列 |
| 字符集转换 | **会做**，写入/读取时按列字符集编解码 | 不做，原样存取 |
| 图片字节写入后果 | 非法字节被替换或拒绝；读出时**图片损坏、无法打开** | 完好 |
| 容量上限 | `TEXT` 64 KB / `MEDIUMTEXT` 16 MB / `LONGTEXT` 4 GB | 同上，仅对应 BLOB 系 |
| 排序/比较 | 按字符集规则 | 按字节 |

**两个致命问题**：

1. **字符集转换会破坏二进制**：JPEG/PNG 的字节流里包含大量非 UTF-8 序列。写入 `utf8mb4` 的 TEXT 列时，MySQL 会尝试按字符集解释，遇到非法序列要么报错，要么被静默替换成 `?`，读出来的图片必然打不开。
2. **`TEXT` 只有 64 KB**：一张普通的手机照片就有 2–5 MB，远超上限。非严格模式下**静默截断**（你能插进去，但图片是坏的），严格模式下报 `ERROR 1406 Data too long`。

**结论**：存图片原始字节，必须用 `LONGBLOB`（≤4 GB）或 `MEDIUMBLOB`（≤16 MB，多数头像够用且更省）。本设计的 DDL 采用 `LONGBLOB`。

如果你确实希望保留"文本类型"的形态，唯一正确的做法是**先 Base64 编码再存入 `LONGTEXT`**：

```sql
-- 仅当你坚持用文本类型时采用，代价是体积膨胀 33%
`user_img_base64` LONGTEXT DEFAULT NULL COMMENT '图片Base64编码，体积为原图1.33倍'
```

但 Base64 方案会额外浪费 1/3 存储、增加编解码开销，且无法做流式读取，**不推荐**。

### 5.2 大字段对查询性能的影响

`LONGBLOB` / `LONGTEXT` 在 InnoDB 的 `DYNAMIC` 行格式下会存到**页外（off-page）**，行内只留 20 字节指针，因此不会撑爆数据页。但仍有两点要注意：

- **禁止 `SELECT *`**：一条 SQL 拉回几 MB 图片，网络与内存开销直接爆炸。查询列表时必须显式列出字段，例如 `SELECT user_id, user_name, user_age FROM user_db`。
- **备份与 DDL 成本**：含大字段的表，`mysqldump`、`ALTER TABLE` 的耗时与磁盘占用都会显著上升。

三种存储方案对比（建议按场景选）：

| 方案 | 做法 | 优点 | 缺点 | 适用 |
| --- | --- | --- | --- | --- |
| A. 单表大字段（本方案） | `user_img` 存 `LONGBLOB` | 简单，事务一致，无外部依赖 | 表体积膨胀，易被 `SELECT *` 拖垮 | 头像、缩略图、小规模系统 |
| B. 拆表一对一 | 新建 `user_image(user_id, img_data)` | 主表保持轻量，按需 JOIN | 多一次查询 | 中等规模，图片非必读 |
| C. 对象存储 + 路径 | `user_img_url VARCHAR(255)` 存 URL/相对路径 | 数据库轻量，CDN 加速，可断点续传 | 需维护文件服务，跨库事务一致性弱 | 生产环境推荐 |

### 5.3 表名建议

`user_db` 这个命名带了 `_db` 后缀，而 `_db` 在业界约定里通常标识**数据库**而非表。虽然 MySQL 允许（不冲突），但阅读时会误以为是库名。可考虑：

- `user`（最直观，但需注意与 `mysql.user` 系统表在阅读上的混淆）
- `sys_user` / `t_user`（加前缀区分业务模块，中大型项目常用）

当前按你指定的 `user_db` 建表，如需改名告诉我。

---

## 6. 建议补充字段（可选）

用户表几乎一定会用到以下字段，建议在建表时一并加上，避免后期 `ALTER TABLE`：

| 字段名 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `created_at` | `DATETIME(3)` | `CURRENT_TIMESTAMP(3)` | 创建时间，毫秒精度 |
| `updated_at` | `DATETIME(3)` | `CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)` | 更新时间，自动维护 |
| `is_deleted` | `TINYINT(1)` | `0` | 逻辑删除标记（0 正常 / 1 已删） |
| `status` | `TINYINT` | `1` | 账号状态（1 启用 / 0 禁用） |

对应 DDL：

```sql
ALTER TABLE `user_db`
  ADD COLUMN `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  ADD COLUMN `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  ADD COLUMN `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删',
  ADD COLUMN `status`     TINYINT    NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  ADD KEY `idx_created_at` (`created_at`);
```

---

## 7. 容量估算

| 数据项 | 单行估算 | 说明 |
| --- | --- | --- |
| 基础字段（id + 姓名 + 年龄） | 约 40–90 字节 | 变长字段 + 行头 |
| `user_note` | 0–2 KB（典型） | 按 500 汉字估算 |
| `user_img` | 0–5 MB（典型） | 头像压缩后 100–300 KB，原图 2–5 MB |
| 索引 `idx_user_name` | 约 60 字节/行 | 姓名 + 主键指针 |

**关键结论**：1 万用户 × 平均 200 KB 头像 ≈ **2 GB**；若存原图（3 MB），则膨胀到 **30 GB**。这也是方案 C（对象存储）在生产环境更常见的原因。

另外注意 `max_allowed_packet`：MySQL 8.4 默认 64 MB，单条 `INSERT` 的图片超过该值会报 `ERROR 2006 / 1153`。超大图片务必拆分写入或改用文件存储。

---

## 8. 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
| --- | --- | --- | --- |
| v1.0 | 2026-11-09 | 初版：定义 5 个字段、2 个索引，`user_img` 由 `TEXT` 修正为 `LONGBLOB` | — |
| v1.1 | 2026-11-10 | 注册/登录落地（对应 docs/backend/04-业务逻辑文档.md §0）：新增 `user_password / created_at / updated_at / is_deleted / status` 5 列；`idx_user_name` 升级为唯一索引 `uk_user_name`；新增 `idx_created_at` | — |
