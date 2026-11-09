package com.gyy.user.dto;

/**
 * 报表查询入参（见 docs/backend/04-业务逻辑文档.md §3.2）
 */
public class ReportQuery {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String userName;
    /** yyyy-MM-dd */
    private String startDate;
    /** yyyy-MM-dd */
    private String endDate;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
