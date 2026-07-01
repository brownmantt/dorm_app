package com.dom.project.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 備品利用登録 DTO。
 */
public class EquipmentUsageSaveDTO {

    @NotBlank(message = "備品を選択してください")
    private String equipmentAssetId;

    private String dormitoryId;

    private String roomId;

    private String employeeId;

    @NotNull(message = "利用開始日を入力してください")
    private LocalDate usageStartDate;

    private LocalDate usageEndDate;

    @NotNull(message = "利用個数を入力してください")
    @Min(value = 1, message = "利用個数は1以上で入力してください")
    private Integer usageQuantity = 1;

    @Size(max = 2000, message = "備考は2000文字以内で入力してください")
    private String remarks;

    public String getEquipmentAssetId() {
        return equipmentAssetId;
    }

    public void setEquipmentAssetId(String equipmentAssetId) {
        this.equipmentAssetId = equipmentAssetId;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getUsageStartDate() {
        return usageStartDate;
    }

    public void setUsageStartDate(LocalDate usageStartDate) {
        this.usageStartDate = usageStartDate;
    }

    public LocalDate getUsageEndDate() {
        return usageEndDate;
    }

    public void setUsageEndDate(LocalDate usageEndDate) {
        this.usageEndDate = usageEndDate;
    }

    public Integer getUsageQuantity() {
        return usageQuantity;
    }

    public void setUsageQuantity(Integer usageQuantity) {
        this.usageQuantity = usageQuantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
