package com.dom.project.entity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 単価マスタ保存 DTO。
 */
public class UnitPriceSaveDTO {

    @NotBlank(message = "地域を選択してください")
    @Size(max = 30, message = "地域コードは30文字以内で入力してください")
    private String region;

    @Size(max = 20, message = "寮IDは20文字以内で入力してください")
    private String dormitoryId;

    @Size(max = 20, message = "部屋IDは20文字以内で入力してください")
    private String roomId;

    @NotBlank(message = "利用形態を選択してください")
    @Size(max = 30, message = "利用形態コードは30文字以内で入力してください")
    private String usageTypeCode;

    @NotNull(message = "日単価を入力してください")
    @DecimalMin(value = "0", message = "日単価は0以上の値を入力してください")
    private BigDecimal dailyUnitPrice;

    /** 未指定時は業務層で -1 を設定 */
    private Integer maxUsageDays;

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
}
