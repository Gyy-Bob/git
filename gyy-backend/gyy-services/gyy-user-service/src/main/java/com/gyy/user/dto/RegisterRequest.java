package com.gyy.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 注册入参（见 docs/backend/04-业务逻辑文档.md §1.2）
 */
public class RegisterRequest {

    /** 账号/姓名：2-50 字符，仅中文、字母、数字、下划线 */
    @NotNull(message = "缺少必填参数：userName")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9_]{2,50}$",
            message = "参数格式错误：userName 仅限中文、字母、数字、下划线，长度 2-50")
    private String userName;

    /** 密码：8-32 位，须同时包含字母与数字 */
    @NotNull(message = "缺少必填参数：password")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*_.+-]{8,32}$",
            message = "参数格式错误：password 须 8-32 位且包含字母和数字")
    private String password;

    @Min(value = 0, message = "参数超出允许范围：userAge 须在 0-150")
    @Max(value = 150, message = "参数超出允许范围：userAge 须在 0-150")
    private Integer userAge;

    @Size(max = 500, message = "参数超出允许范围：userNote 最多 500 字符")
    private String userNote;

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

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }
}
