package com.dom.project.entity.view;

import java.time.LocalDate;

/**
 * 長期利用警告一覧検索結果ビュー。
 */
public class LongTermUsageAlertView {

    private String employeeId;
    private String employeeName;
    private LocalDate firstUseDate;
    private Integer elapsedYears;
    private String dormitoryName;
    private String roomName;

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

    public LocalDate getFirstUseDate() {
        return firstUseDate;
    }

    public void setFirstUseDate(LocalDate firstUseDate) {
        this.firstUseDate = firstUseDate;
    }

    public Integer getElapsedYears() {
        return elapsedYears;
    }

    public void setElapsedYears(Integer elapsedYears) {
        this.elapsedYears = elapsedYears;
    }

    public String getDormitoryName() {
        return dormitoryName;
    }

    public void setDormitoryName(String dormitoryName) {
        this.dormitoryName = dormitoryName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
