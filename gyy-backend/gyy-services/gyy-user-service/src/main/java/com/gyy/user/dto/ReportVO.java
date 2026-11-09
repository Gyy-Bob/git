package com.gyy.user.dto;

import java.util.List;

/**
 * 报表返参：统计摘要 + 分页列表（见 docs/backend/04-业务逻辑文档.md §3.2）
 */
public class ReportVO {

    private ReportSummaryVO summary;
    private List<ReportUserVO> list;
    private Long pageNum;
    private Long pageSize;
    private Long total;
    private Long pages;

    public ReportSummaryVO getSummary() {
        return summary;
    }

    public void setSummary(ReportSummaryVO summary) {
        this.summary = summary;
    }

    public List<ReportUserVO> getList() {
        return list;
    }

    public void setList(List<ReportUserVO> list) {
        this.list = list;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }
}
