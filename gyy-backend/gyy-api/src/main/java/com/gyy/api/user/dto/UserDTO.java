package com.gyy.api.user.dto;

/**
 * 用户 DTO（服务间传输用，不含图片二进制，头像走独立接口）。
 */
public class UserDTO {

    private Long userId;
    private String userName;
    private Integer userAge;
    private String userNote;

    public UserDTO() {
    }

    public UserDTO(Long userId, String userName, Integer userAge, String userNote) {
        this.userId = userId;
        this.userName = userName;
        this.userAge = userAge;
        this.userNote = userNote;
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
