package com.dom.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 寮費エンティティ（テーブル dorm_fee）。
 */
public class DormFee {

    /** 寮費 ID */
    private String dormFeeId;

    /** 社員 ID */
    private String employeeId;

    /** 部屋 ID */
    private String roomId;

    /** 対象年月 YYYY-MM */
    private String targetYearMonth;

    /** 算出金額 */
    private BigDecimal amount;

    /** 算出根拠：面積 */
    private BigDecimal basisAreaSqm;

    /** 算出根拠：日数 */
    private Integer basisDays;

    /** 算定内訳（JSONB） */
    private String basisDetail;

    /** ステータス：DRAFT / CONFIRMED */
    private String status;

    /** 関連入居履歴 ID */
    private String residenceHistoryId;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    /** 論理削除日時（NULL は未削除） */
    private LocalDateTime deletedAt;

    public String getDormFeeId() {
        return dormFeeId;
    }

    public void setDormFeeId(String dormFeeId) {
        this.dormFeeId = dormFeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTargetYearMonth() {
        return targetYearMonth;
    }

    public void setTargetYearMonth(String targetYearMonth) {
        this.targetYearMonth = targetYearMonth;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBasisAreaSqm() {
        return basisAreaSqm;
    }

    public void setBasisAreaSqm(BigDecimal basisAreaSqm) {
        this.basisAreaSqm = basisAreaSqm;
    }

    public Integer getBasisDays() {
        return basisDays;
    }

    public void setBasisDays(Integer basisDays) {
        this.basisDays = basisDays;
    }

    public String getBasisDetail() {
        return basisDetail;
    }

    public void setBasisDetail(String basisDetail) {
        this.basisDetail = basisDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
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
