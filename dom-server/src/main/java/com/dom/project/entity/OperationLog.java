package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 操作ログエンティティ（テーブル operation_log）。
 */
public class OperationLog {

    /** ログ ID */
    private Long logId;

    /** 操作種別 */
    private String operationType;

    /** 対象テーブル */
    private String targetTable;

    /** 対象 ID */
    private String targetId;

    /** 変更前値（JSONB） */
    private String beforeValue;

    /** 変更後値（JSONB） */
    private String afterValue;

    /** 操作者 */
    private String operatedBy;

    /** 操作日時 */
    private LocalDateTime operatedAt;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getBeforeValue() {
        return beforeValue;
    }

    public void setBeforeValue(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getAfterValue() {
        return afterValue;
    }

    public void setAfterValue(String afterValue) {
        this.afterValue = afterValue;
    }

    public String getOperatedBy() {
        return operatedBy;
    }

    public void setOperatedBy(String operatedBy) {
        this.operatedBy = operatedBy;
    }

    public LocalDateTime getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(LocalDateTime operatedAt) {
        this.operatedAt = operatedAt;
    }
}
