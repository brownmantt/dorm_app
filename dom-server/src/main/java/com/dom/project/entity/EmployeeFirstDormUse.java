package com.dom.project.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 初回利用日エンティティ（テーブル employee_first_dorm_use（日本社員のみ）。
 */
public class EmployeeFirstDormUse {

    /** 社員 ID */
    private String employeeId;

    /** 初回寮利用入寮日（不变） */
    private LocalDate firstUseDate;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getFirstUseDate() {
        return firstUseDate;
    }

    public void setFirstUseDate(LocalDate firstUseDate) {
        this.firstUseDate = firstUseDate;
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
