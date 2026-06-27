package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 寮保存 DTO。
 */
public class DormitorySaveDTO {

    @NotBlank(message = "寮名称を入力してください")
    @Size(max = 100, message = "寮名称は100文字以内で入力してください")
    private String name;

    @NotBlank(message = "地域を選択してください")
    private String region;

    @NotBlank(message = "郵便番号を入力してください")
    @Pattern(regexp = "^\\d{7}$", message = "郵便番号は7桁の数字で入力してください")
    private String postalCode;

    @NotBlank(message = "住所を入力してください")
    @Size(max = 200, message = "住所は200文字以内で入力してください")
    private String address;

    @NotBlank(message = "間取りを選択してください")
    private String layoutType;

    @NotBlank(message = "種別を選択してください")
    private String genderType;

    @Size(max = 20, message = "最寄駅１は20文字以内で入力してください")
    private String nearestStation1;

    @Size(max = 20, message = "最寄駅２は20文字以内で入力してください")
    private String nearestStation2;

    @Size(max = 20, message = "最寄駅３は20文字以内で入力してください")
    private String nearestStation3;

    private String remarks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
