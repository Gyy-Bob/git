package com.gyy.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录入参（见 docs/backend/04-业务逻辑文档.md §2.2）
 */
public class LoginRequest {

    @NotBlank(message = "缺少必填参数：userName")
    @Size(min = 2, max = 50, message = "参数格式错误：userName 长度 2-50")
    private String userName;

    @NotBlank(message = "缺少必填参数：password")
    @Size(min = 8, max = 32, message = "参数格式错误：password 长度 8-32")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
