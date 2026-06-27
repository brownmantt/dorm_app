package com.dom.project.entity.vo;

import java.math.BigDecimal;

/**
 * 寮費算定根拠 VO。
 */
public class DormFeeBasisVO {

    private BigDecimal roomAreaSqm;
    private String roomType;
    private Integer billableDays;
    private BigDecimal dailyRate;
    private String formula;

    public BigDecimal getRoomAreaSqm() { return roomAreaSqm; }
    public void setRoomAreaSqm(BigDecimal roomAreaSqm) { this.roomAreaSqm = roomAreaSqm; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public Integer getBillableDays() { return billableDays; }
    public void setBillableDays(Integer billableDays) { this.billableDays = billableDays; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }
    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
}
