package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 退去時備品処理エンティティ（テーブル equipment_moveout）。
 */
public class EquipmentMoveout {

    /** 退去処理 ID */
    private String moveoutId;

    /** 入居履歴 ID */
    private String residenceHistoryId;

    /** 備品 ID */
    private String equipmentId;

    /** 処分方法：DISCARD / STORE / REUSE */
    private String disposition;

    /** 処理日時 */
    private LocalDateTime processedAt;

    /** 備考 */
    private String remarks;

    /** 操作者 */
    private String createdBy;

    public String getMoveoutId() {
        return moveoutId;
    }

    public void setMoveoutId(String moveoutId) {
        this.moveoutId = moveoutId;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
