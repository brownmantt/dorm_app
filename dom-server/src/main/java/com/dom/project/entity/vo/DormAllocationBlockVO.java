package com.dom.project.entity.vo;

import java.util.List;

/**
 * 寮割当カレンダーブロック VO。
 */
public class DormAllocationBlockVO {

    private String dormitoryId;
    private String dormitoryName;
    private String address;
    private String postalCode;
    private String region;
    private String layoutType;
    private String genderType;
    private Boolean hasVacancy;
    private List<DormAllocationRowVO> rows;

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }

    public String getDormitoryName() {
        return dormitoryName;
    }

    public void setDormitoryName(String dormitoryName) {
        this.dormitoryName = dormitoryName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public Boolean getHasVacancy() {
        return hasVacancy;
    }

    public void setHasVacancy(Boolean hasVacancy) {
        this.hasVacancy = hasVacancy;
    }

    public List<DormAllocationRowVO> getRows() {
        return rows;
    }

    public void setRows(List<DormAllocationRowVO> rows) {
        this.rows = rows;
    }
}
