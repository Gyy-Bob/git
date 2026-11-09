package com.gyy.user.dto;

/**
 * 报表列表行（不含密码与头像大字段）
 */
public class ReportUserVO {

    private Long userId;
    private String userName;
    private Integer userAge;
    /** 1 启用 / 0 禁用 */
    private Integer status;
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

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
