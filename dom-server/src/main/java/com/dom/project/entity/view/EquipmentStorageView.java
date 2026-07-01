package com.dom.project.entity.view;

/**
 * 備品保管一覧検索結果ビュー。
 */
public class EquipmentStorageView {

    private String storageId;
    private String equipmentAssetId;
    private String equipmentName;
    private Integer purchaseQuantity;
    private String storageLocationId;
    private String storageLocationName;
    private Integer storageQuantity;
    private String status;
    private String linkedMoveoutId;

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getEquipmentAssetId() {
        return equipmentAssetId;
    }

    public void setEquipmentAssetId(String equipmentAssetId) {
        this.equipmentAssetId = equipmentAssetId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(String storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = storageLocationName;
    }

    public Integer getStorageQuantity() {
        return storageQuantity;
    }

    public void setStorageQuantity(Integer storageQuantity) {
        this.storageQuantity = storageQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkedMoveoutId() {
        return linkedMoveoutId;
    }

    public void setLinkedMoveoutId(String linkedMoveoutId) {
        this.linkedMoveoutId = linkedMoveoutId;
    }
}
