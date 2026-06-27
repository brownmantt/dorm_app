package com.dom.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 単価マスタエンティティ（テーブル unit_price）。
 */
public class UnitPrice {

    private String unitPriceId;
    private String code;
    private String region;
    private String dormitoryId;
    private String roomId;
    private String usageTypeCode;
    private BigDecimal dailyUnitPrice;
    private Integer maxUsageDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public String getUnitPriceId() {
        return unitPriceId;
    }

    public void setUnitPriceId(String unitPriceId) {
        this.unitPriceId = unitPriceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getUsageTypeCode() {
        return usageTypeCode;
    }

    public void setUsageTypeCode(String usageTypeCode) {
        this.usageTypeCode = usageTypeCode;
    }

    public BigDecimal getDailyUnitPrice() {
        return dailyUnitPrice;
    }

    public void setDailyUnitPrice(BigDecimal dailyUnitPrice) {
        this.dailyUnitPrice = dailyUnitPrice;
    }

    public Integer getMaxUsageDays() {
        return maxUsageDays;
    }

    public void setMaxUsageDays(Integer maxUsageDays) {
        this.maxUsageDays = maxUsageDays;
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
