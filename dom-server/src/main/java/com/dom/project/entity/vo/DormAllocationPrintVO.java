package com.dom.project.entity.vo;

/**
 * 寮割当印刷 VO。
 */
public class DormAllocationPrintVO {

    private DormAllocationCalendarVO calendar;
    private Boolean pageBreakPerDormitory = Boolean.TRUE;
    private String orientation = "landscape";

    public DormAllocationCalendarVO getCalendar() {
        return calendar;
    }

    public void setCalendar(DormAllocationCalendarVO calendar) {
        this.calendar = calendar;
    }

    public Boolean getPageBreakPerDormitory() {
        return pageBreakPerDormitory;
    }

    public void setPageBreakPerDormitory(Boolean pageBreakPerDormitory) {
        this.pageBreakPerDormitory = pageBreakPerDormitory;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}
