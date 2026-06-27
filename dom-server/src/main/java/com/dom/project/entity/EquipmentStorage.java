package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 備品保管エンティティ（テーブル equipment_storage）。
 */
public class EquipmentStorage {

    /** 保管 ID */
    private String storageId;

    /** 備品 ID */
    private String equipmentId;

    /** 保管場所 */
    private String storageLocation;

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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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
