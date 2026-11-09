package com.gyy.user.dto;

/**
 * 报表统计摘要（口径：is_deleted = 0）
 */
public class ReportSummaryVO {

    /** 注册总人数 */
    private Long total;
    /** 今日新增人数（created_at 落在当天 [00:00,24:00)） */
    private Long todayCount;
    /** 被禁用账号数 */
    private Long disabledCount;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(Long todayCount) {
        this.todayCount = todayCount;
    }

    public Long getDisabledCount() {
        return disabledCount;
    }

    public void setDisabledCount(Long disabledCount) {
        this.disabledCount = disabledCount;
    }
}
