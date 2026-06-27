package com.dom.project.entity.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 単価マスタ一覧表示用 View。
 */
public class UnitPriceListView {

    private String unitPriceId;
    private String code;
    private String region;
    private String regionName;
    private String dormitoryId;
    private String dormitoryName;
    private String roomId;
    private String roomName;
    private String usageTypeCode;
    private String usageTypeName;
    private BigDecimal dailyUnitPrice;
    private Integer maxUsageDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
}
