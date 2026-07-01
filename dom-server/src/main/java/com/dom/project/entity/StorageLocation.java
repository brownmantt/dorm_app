package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 保管場所マスタエンティティ（テーブル storage_location）。
 */
public class StorageLocation {

    /** 保管場所 ID */
    private String storageLocationId;

    /** 保管場所名称 */
    private String name;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    /** 論理削除日時（NULL は未削除） */
    private LocalDateTime deletedAt;

    public String getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(String storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
