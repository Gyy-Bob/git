-- =============================================================
-- 用户表 user_db 建表脚本
-- 适用：MySQL 8.4.x（CHECK 约束需 8.0.16+ 才真正生效）
-- 字符集：utf8mb4 / utf8mb4_0900_ai_ci
-- 说明：user_img 使用 LONGBLOB 存图片原始字节；TEXT 会做字符集转换并损坏二进制数据
-- =============================================================

CREATE TABLE IF NOT EXISTS `user_db` (
  `user_id`   BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '用户ID，主键自增',
  `user_name` VARCHAR(50)      NOT NULL                 COMMENT '姓名',
  `user_age`  TINYINT UNSIGNED          DEFAULT NULL    COMMENT '年龄，业务范围 0-150',
  `user_img`  LONGBLOB                  DEFAULT NULL    COMMENT '图片，存图片原始字节码',
  `user_note` LONGTEXT                  DEFAULT NULL    COMMENT '备注，大字符',
  PRIMARY KEY (`user_id`),
  KEY `idx_user_name` (`user_name`),
  CONSTRAINT `chk_user_age` CHECK (`user_age` IS NULL OR (`user_age` >= 0 AND `user_age` <= 150))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC
  COMMENT = '用户信息表';

-- -------------------------------------------------------------
-- 可选：建议补充的通用字段（需要时取消注释执行）
-- -------------------------------------------------------------
-- ALTER TABLE `user_db`
--   ADD COLUMN `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
--   ADD COLUMN `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
--   ADD COLUMN `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删',
--   ADD COLUMN `status`     TINYINT    NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
--   ADD KEY `idx_created_at` (`created_at`);

-- -------------------------------------------------------------
-- 可选：若改用对象存储存图片路径，替换 user_img 字段
-- -------------------------------------------------------------
-- ALTER TABLE `user_db`
--   DROP COLUMN `user_img`,
--   ADD COLUMN `user_img_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL/相对路径';
