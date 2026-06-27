package com.dom.project.entity.vo;

/**
 * 通算利用日数 VO。
 */
public class TotalUsageDaysVO {

    private Integer totalUsageDays;

    public TotalUsageDaysVO() {
    }

    public TotalUsageDaysVO(Integer totalUsageDays) {
        this.totalUsageDays = totalUsageDays;
    }

    public Integer getTotalUsageDays() { return totalUsageDays; }
    public void setTotalUsageDays(Integer totalUsageDays) { this.totalUsageDays = totalUsageDays; }
    public Integer getDays() { return totalUsageDays; }
}
