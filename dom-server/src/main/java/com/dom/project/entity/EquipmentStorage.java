package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 備品保管エンティティ（テーブル equipment_storage）。
 */
public class EquipmentStorage {

    /** 保管 ID */
    private String storageId;

    /** 備品（個体）ID */
    private String equipmentAssetId;

    /** 保管場所 ID（保管場所マスタ参照） */
    private String storageLocationId;

    /** 保管数量 */
    private Integer storageQuantity;

    /** ステータス：IN_STORAGE / REUSED 等 */
    private String status;

    /** 関連退去処理 ID */
    private String linkedMoveoutId;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

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

    public String getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(String storageLocationId) {
        this.storageLocationId = storageLocationId;
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
