package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 寮責任者保存 DTO。
 */
public class DormitoryManagerSaveDTO {

    /** 社員 ID（省略時は入居履歴から導出） */
    private String employeeId;

    @NotBlank(message = "入居履歴IDを入力してください")
    private String residenceHistoryId;

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
}
