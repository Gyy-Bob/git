package com.gyy.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据源自检：启动即验证 网络/认证/库表/驱动 四层（见 docs/backend/03-MySQL连接配置.md §4）
 */
@Component
public class DatasourceSelfCheck implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatasourceSelfCheck.class);

    private final JdbcTemplate jdbcTemplate;

    public DatasourceSelfCheck(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_db", Long.class);
        log.info("[自检] MySQL {} 连接成功，user_db 现有 {} 行", version, count);
    }
}
