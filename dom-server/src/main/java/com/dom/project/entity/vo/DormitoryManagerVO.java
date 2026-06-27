package com.dom.project.entity.vo;

/**
 * 寮責任者表示 VO。
 */
public class DormitoryManagerVO {

    private String employeeId;
    private String employeeName;
    private String residenceHistoryId;
    private Boolean autoAssigned;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public Boolean getAutoAssigned() {
        return autoAssigned;
    }

    public void setAutoAssigned(Boolean autoAssigned) {
        this.autoAssigned = autoAssigned;
    }
}
