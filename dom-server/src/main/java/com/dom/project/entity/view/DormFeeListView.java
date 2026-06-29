package com.dom.project.entity.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 寮費一覧表示用 View（名称を JOIN 付与）。
 */
public class DormFeeListView {

    private String dormFeeId;
    private String region;
    private String regionName;
    private String dormitoryId;
    private String dormitoryName;
    private String roomId;
    private String roomName;
    private String employeeId;
    private String employeeName;
    private String targetYearMonth;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    /** 入居履歴の退居日（未退居の場合は null） */
    private LocalDate residenceMoveOutDate;
    private String usageTypeCode;
    private String usageTypeName;
    private Integer usageDays;
    private String unitPriceId;
    private String unitPriceCode;
    private BigDecimal dailyUnitPrice;
    private BigDecimal amount;
    private String residenceHistoryId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getDormFeeId() {
        return dormFeeId;
    }

    public void setDormFeeId(String dormFeeId) {
        this.dormFeeId = dormFeeId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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

    public String getTargetYearMonth() {
        return targetYearMonth;
    }

    public void setTargetYearMonth(String targetYearMonth) {
        this.targetYearMonth = targetYearMonth;
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

    public LocalDate getResidenceMoveOutDate() {
        return residenceMoveOutDate;
    }

    public void setResidenceMoveOutDate(LocalDate residenceMoveOutDate) {
        this.residenceMoveOutDate = residenceMoveOutDate;
    }

    public String getUsageTypeCode() {
        return usageTypeCode;
    }

    public void setUsageTypeCode(String usageTypeCode) {
        this.usageTypeCode = usageTypeCode;
    }

    public String getUsageTypeName() {
        return usageTypeName;
    }

    public void setUsageTypeName(String usageTypeName) {
        this.usageTypeName = usageTypeName;
    }

    public Integer getUsageDays() {
        return usageDays;
    }

    public void setUsageDays(Integer usageDays) {
        this.usageDays = usageDays;
    }

    public String getUnitPriceId() {
        return unitPriceId;
    }

    public void setUnitPriceId(String unitPriceId) {
        this.unitPriceId = unitPriceId;
    }

    public String getUnitPriceCode() {
        return unitPriceCode;
    }

    public void setUnitPriceCode(String unitPriceCode) {
        this.unitPriceCode = unitPriceCode;
    }

    public BigDecimal getDailyUnitPrice() {
        return dailyUnitPrice;
    }

    public void setDailyUnitPrice(BigDecimal dailyUnitPrice) {
        this.dailyUnitPrice = dailyUnitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
