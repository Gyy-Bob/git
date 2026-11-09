package com.gyy.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页查询参数
 */
public class UserPageQuery {

    @Min(value = 1, message = "pageNum 从 1 开始")
    private long pageNum = 1;

    @Min(value = 1, message = "pageSize 至少为 1")
    @Max(value = 100, message = "pageSize 最大 100")
    private long pageSize = 10;

    /** 姓名模糊匹配，可空 */
    private String userName;

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
