-- =============================================================
-- 用户表 user_db 建表脚本
-- 适用：MySQL 8.4.x（CHECK 约束需 8.0.16+ 才真正生效）
-- 字符集：utf8mb4 / utf8mb4_0900_ai_ci
-- 说明：user_img 使用 LONGBLOB 存图片原始字节；TEXT 会做字符集转换并损坏二进制数据
-- v1.1 (2026-11-10)：支撑注册/登录（docs/backend/04-业务逻辑文档.md §0），
--   新增 user_password / created_at / updated_at / is_deleted / status；
--   user_name 升级为唯一索引 uk_user_name（兼作登录账号）
-- =============================================================

CREATE TABLE IF NOT EXISTS `user_db` (
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

-- -------------------------------------------------------------
-- 存量库（v1.0 老表）增量迁移脚本（本次已在 user 库执行）
-- -------------------------------------------------------------
-- ALTER TABLE `user_db`
--   ADD COLUMN `user_password` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '登录密码，存 BCrypt 哈希（60 字符），禁止明文',
--   ADD COLUMN `created_at`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '注册时间',
--   ADD COLUMN `updated_at`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
--   ADD COLUMN `is_deleted`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删',
--   ADD COLUMN `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1启用 0禁用',
--   ADD KEY `idx_created_at` (`created_at`);
-- ALTER TABLE `user_db` DROP INDEX `idx_user_name`, ADD UNIQUE KEY `uk_user_name` (`user_name`);

-- -------------------------------------------------------------
-- 可选：若改用对象存储存图片路径，替换 user_img 字段
-- -------------------------------------------------------------
-- ALTER TABLE `user_db`
--   DROP COLUMN `user_img`,
--   ADD COLUMN `user_img_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL/相对路径';
