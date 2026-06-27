package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 退去備品処理 DTO。
 */
public class EquipmentMoveoutDTO {

    @NotBlank(message = "入居履歴IDを入力してください")
    private String residenceHistoryId;

    @NotBlank(message = "備品IDを入力してください")
    private String equipmentId;

    @NotBlank(message = "処分方法を選択してください")
    private String disposition;

    private String remarks;

    public String getResidenceHistoryId() { return residenceHistoryId; }
    public void setResidenceHistoryId(String residenceHistoryId) { this.residenceHistoryId = residenceHistoryId; }
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }
    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
