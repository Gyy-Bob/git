package com.gyy.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc 配置：/v3/api-docs 供 Apifox 定时导入（见 docs/backend/02-接口规范与Apifox.md §2）
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userApiInfo() {
        return new OpenAPI().info(new Info()
                .title("gyy-user-service API")
                .description("用户服务：user_db CRUD 与头像存取")
                .version("v1"));
    }
}
