package com.dom.project.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 備品利用エンティティ。
 */
public class EquipmentUsage {

    private String usageId;
    private String equipmentAssetId;
    private String dormitoryId;
    private String roomId;
    private String employeeId;
    private LocalDate usageStartDate;
    private LocalDate usageEndDate;
    private Integer usageQuantity;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public String getUsageId() {
        return usageId;
    }

    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
