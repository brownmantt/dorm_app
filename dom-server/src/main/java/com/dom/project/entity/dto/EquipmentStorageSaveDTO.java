package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 備品保管登録 DTO。
 */
public class EquipmentStorageSaveDTO {

    @NotBlank(message = "備品IDを入力してください")
    private String equipmentId;

    @NotBlank(message = "保管場所を入力してください")
    private String storageLocation;

    private String linkedMoveoutId;

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public String getLinkedMoveoutId() { return linkedMoveoutId; }
    public void setLinkedMoveoutId(String linkedMoveoutId) { this.linkedMoveoutId = linkedMoveoutId; }
}
