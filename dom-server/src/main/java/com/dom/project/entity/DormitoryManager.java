package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 寮責任者エンティティ（テーブル dormitory_manager）。
 */
public class DormitoryManager {

    /** 寮 ID */
    private String dormitoryId;

    /** 社員 ID */
    private String employeeId;

    /** 入居履歴 ID */
    private String residenceHistoryId;

    /** 割当日時 */
    private LocalDateTime assignedAt;

    /** 排他制御用バージョン */
    private Integer version;

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
