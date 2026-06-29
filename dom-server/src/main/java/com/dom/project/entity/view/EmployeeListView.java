package com.dom.project.entity.view;

import java.time.LocalDateTime;

/**
 * 社員一覧表示用 View。
 */
public class EmployeeListView {

    private String employeeId;
    private String name;
    private String gender;
    private String employeeCategory;
    private String affiliationId;
    private String affiliationName;
    private String businessDivision;
    private String nearestStation1;
    private String nearestStation2;
    private String nearestStation3;
    private String contactInfo;
    /** 入居中フラグ（当日時点で有効な入居履歴あり） */
    private Boolean residing;
    /** 更新日時 */
    private LocalDateTime updatedAt;

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

    public String getAffiliationName() {
        return affiliationName;
    }

    public void setAffiliationName(String affiliationName) {
        this.affiliationName = affiliationName;
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

    public Boolean getResiding() {
        return residing;
    }

    public void setResiding(Boolean residing) {
        this.residing = residing;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
