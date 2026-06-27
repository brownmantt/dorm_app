package com.dom.project.entity.vo;

import java.util.List;

/**
 * 寮割当カレンダー VO。
 */
public class DormAllocationCalendarVO {

    private String yearMonth;
    private Integer daysInMonth;
    private String firstDayOfWeek;
    private List<String> weekdayLabels;
    private List<DormAllocationBlockVO> blocks;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public Integer getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(Integer daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public String getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(String firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public List<String> getWeekdayLabels() {
        return weekdayLabels;
    }

    public void setWeekdayLabels(List<String> weekdayLabels) {
        this.weekdayLabels = weekdayLabels;
    }

    public List<DormAllocationBlockVO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<DormAllocationBlockVO> blocks) {
        this.blocks = blocks;
    }
}
