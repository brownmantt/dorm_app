package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 社員保存 DTO（新規・更新共通。新規時は employeeId 必須）。
 */
public class EmployeeSaveDTO {

    @Size(max = 20, message = "社員IDは20文字以内で入力してください")
    private String employeeId;

    @NotBlank(message = "氏名を入力してください")
    @Size(max = 100, message = "氏名は100文字以内で入力してください")
    private String name;

    @NotBlank(message = "性別を選択してください")
    private String gender;

    @NotBlank(message = "入居者区分を選択してください")
    private String employeeCategory;

    @Size(max = 20, message = "所属IDは20文字以内で入力してください")
    private String affiliationId;

    @Size(max = 30, message = "事業部は30文字以内で入力してください")
    private String businessDivision;

    @Size(max = 20, message = "最寄駅１は20文字以内で入力してください")
    private String nearestStation1;

    @Size(max = 20, message = "最寄駅２は20文字以内で入力してください")
    private String nearestStation2;

    @Size(max = 20, message = "最寄駅３は20文字以内で入力してください")
    private String nearestStation3;

    @Size(max = 30, message = "携帯電話は30文字以内で入力してください")
    private String mobilePhone;

    @Size(max = 100, message = "メールは100文字以内で入力してください")
    private String email;

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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
