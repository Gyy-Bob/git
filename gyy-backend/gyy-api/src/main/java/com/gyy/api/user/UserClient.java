package com.gyy.api.user;

import com.gyy.api.user.dto.UserDTO;
import com.gyy.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 契约。
 * dev 环境未启用注册中心时，由调用方在 application-local.yml 里指定：
 * spring.cloud.openfeign.client.config 或 url 直连（预留）。
 */
@FeignClient(name = "gyy-user-service", path = "/api/v1/users")
public interface UserClient {

    @GetMapping("/{userId}")
    Result<UserDTO> getUser(@PathVariable("userId") Long userId);
}
