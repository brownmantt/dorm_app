package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * 社員／入居者エンティティ（テーブル employee）。
 */
public class Employee {

    /** 社員 ID */
    private String employeeId;

    /** 氏名 */
    private String name;

    /** 性别：MALE / FEMALE */
    private String gender;

    /** 入居者区分：JAPAN / CHINA_ASSIGN */
    private String employeeCategory;

    /** 所属 ID */
    private String affiliationId;

    /** 事業部 */
    private String businessDivision;

    /** 最寄駅１ */
    private String nearestStation1;

    /** 最寄駅２ */
    private String nearestStation2;

    /** 最寄駅３ */
    private String nearestStation3;

    /** 連絡先情報（JSONB 文字列） */
    private String contactInfo;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    /** 論理削除日時（NULL は未削除） */
    private LocalDateTime deletedAt;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmployeeCategory() {
        return employeeCategory;
    }

    public void setEmployeeCategory(String employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public String getAffiliationId() {
        return affiliationId;
    }

    public void setAffiliationId(String affiliationId) {
        this.affiliationId = affiliationId;
    }

    public String getBusinessDivision() {
        return businessDivision;
    }

    public void setBusinessDivision(String businessDivision) {
        this.businessDivision = businessDivision;
    }

    public String getNearestStation1() {
        return nearestStation1;
    }

    public void setNearestStation1(String nearestStation1) {
        this.nearestStation1 = nearestStation1;
    }

    public String getNearestStation2() {
        return nearestStation2;
    }

    public void setNearestStation2(String nearestStation2) {
        this.nearestStation2 = nearestStation2;
    }

    public String getNearestStation3() {
        return nearestStation3;
    }

    public void setNearestStation3(String nearestStation3) {
        this.nearestStation3 = nearestStation3;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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
