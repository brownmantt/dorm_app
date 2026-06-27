package com.dom.project.entity.view;

import java.time.LocalDate;

/**
 * 入居履歴一覧検索結果ビュー（社員・寮・部屋名称を関連付け）。
 */
public class ResidenceHistoryView {

    private String residenceHistoryId;
    private String employeeId;
    private String employeeName;
    private String dormitoryId;
    private String dormitoryName;
    private String roomId;
    private String roomName;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private String moveOutReason;

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

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

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }

    public String getDormitoryName() {
        return dormitoryName;
    }

    public void setDormitoryName(String dormitoryName) {
        this.dormitoryName = dormitoryName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public String getMoveOutReason() {
        return moveOutReason;
    }

    public void setMoveOutReason(String moveOutReason) {
        this.moveOutReason = moveOutReason;
    }
}
