package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 寮費算定 DTO。
 */
public class DormFeeCalculateDTO {

    @NotBlank(message = "社員IDを入力してください")
    private String employeeId;

    @NotBlank(message = "部屋IDを入力してください")
    private String roomId;

    @NotBlank(message = "対象年月を入力してください")
    private String targetYearMonth;

    @NotBlank(message = "入寮日を入力してください")
    private String moveInDate;

    private String moveOutDate;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getTargetYearMonth() { return targetYearMonth; }
    public void setTargetYearMonth(String targetYearMonth) { this.targetYearMonth = targetYearMonth; }
    public String getMoveInDate() { return moveInDate; }
    public void setMoveInDate(String moveInDate) { this.moveInDate = moveInDate; }
    public String getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(String moveOutDate) { this.moveOutDate = moveOutDate; }
}
