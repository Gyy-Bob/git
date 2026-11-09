package com.gyy.user.dto;

/**
 * 注册返参（见 docs/backend/04-业务逻辑文档.md §1.2）
 */
public class RegisterVO {

    private Long userId;
    private String userName;
    /** yyyy-MM-dd HH:mm:ss */
    private String createdAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
