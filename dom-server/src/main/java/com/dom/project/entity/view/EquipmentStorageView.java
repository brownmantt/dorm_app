package com.dom.project.entity.view;

/**
 * 備品保管一覧検索結果ビュー（備品名称を関連付け）。
 */
public class EquipmentStorageView {

    private String storageId;
    private String equipmentName;
    private String storageLocation;
    private String status;

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
