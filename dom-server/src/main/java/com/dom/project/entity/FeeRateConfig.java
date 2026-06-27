package com.dom.project.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 寮費単価設定エンティティ（テーブル fee_rate_config）。
 */
public class FeeRateConfig {

    /** 部屋種別：STANDARD / SMALL / OTHER */
    private String roomType;

    /** ㎡ あたり月額単価 */
    private BigDecimal unitRatePerSqm;

    /** 生效起始日 */
    private LocalDate validFrom;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getUnitRatePerSqm() {
        return unitRatePerSqm;
    }

    public void setUnitRatePerSqm(BigDecimal unitRatePerSqm) {
        this.unitRatePerSqm = unitRatePerSqm;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
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
