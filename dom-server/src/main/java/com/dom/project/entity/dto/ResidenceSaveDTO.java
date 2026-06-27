package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * 入居登録 DTO。
 */
public class ResidenceSaveDTO {

    @NotBlank(message = "社員IDを入力してください")
    private String employeeId;

    @NotBlank(message = "寮IDを入力してください")
    private String dormitoryId;

    @NotBlank(message = "部屋IDを入力してください")
    private String roomId;

    @NotBlank(message = "入寮日を入力してください")
    private String moveInDate;

    private LocalDate moveOutDate;
    private String moveOutReason;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
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
