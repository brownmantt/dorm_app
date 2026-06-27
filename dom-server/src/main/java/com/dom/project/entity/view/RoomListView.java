package com.dom.project.entity.view;

import java.math.BigDecimal;

/**
 * 部屋一覧検索結果ビュー（含空き状況）。
 */
public class RoomListView {

    private String roomId;
    private String roomName;
    private String roomDetail;
    private Boolean hasAirConditioner;
    private BigDecimal monthlyFee;
    private BigDecimal areaSqm;
    private Integer capacity;
    private String roomType;
    /** 空き状況：VACANT / OCCUPIED */
    private String vacancyStatus;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDetail() {
        return roomDetail;
    }

    public void setRoomDetail(String roomDetail) {
        this.roomDetail = roomDetail;
    }

    public Boolean getHasAirConditioner() {
        return hasAirConditioner;
    }

    public void setHasAirConditioner(Boolean hasAirConditioner) {
        this.hasAirConditioner = hasAirConditioner;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(BigDecimal areaSqm) {
        this.areaSqm = areaSqm;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getVacancyStatus() {
        return vacancyStatus;
    }

    public void setVacancyStatus(String vacancyStatus) {
        this.vacancyStatus = vacancyStatus;
    }
}
