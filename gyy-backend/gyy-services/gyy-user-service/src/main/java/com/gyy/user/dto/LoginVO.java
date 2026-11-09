package com.gyy.user.dto;

/**
 * 登录返参（见 docs/backend/04-业务逻辑文档.md §2.2）
 */
public class LoginVO {

    /** JWT，后续请求放 Authorization: Bearer <token> */
    private String token;
    /** 有效期秒数 */
    private Long expiresIn;
    private Long userId;
    private String userName;

    public LoginVO() {
    }

    public LoginVO(String token, Long expiresIn, Long userId, String userName) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

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
}
