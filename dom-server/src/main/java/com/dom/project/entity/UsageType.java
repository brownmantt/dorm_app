package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 利用形態マスタエンティティ（テーブル usage_type）。
 */
public class UsageType {

    /** 利用形態 ID */
    private String usageTypeId;

    /** コード値 */
    private String code;

    /** 名称 */
    private String name;

    /** 表示順 */
    private Integer displayOrder;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    /** 論理削除日時（NULL は未削除） */
    private LocalDateTime deletedAt;

    public String getUsageTypeId() {
        return usageTypeId;
    }

    public void setUsageTypeId(String usageTypeId) {
        this.usageTypeId = usageTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
