package com.dom.project.entity.dto;

import com.dom.project.entity.vo.DormFeeBasisVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 寮費登録 DTO。
 */
public class DormFeeSaveDTO {

    @NotBlank(message = "社員IDを入力してください")
    private String employeeId;

    @NotBlank(message = "部屋IDを入力してください")
    private String roomId;

    @NotBlank(message = "対象年月を入力してください")
    private String targetYearMonth;

    @NotNull(message = "金額を入力してください")
    private BigDecimal amount;

    private DormFeeBasisVO basisDetail;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getTargetYearMonth() { return targetYearMonth; }
    public void setTargetYearMonth(String targetYearMonth) { this.targetYearMonth = targetYearMonth; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public DormFeeBasisVO getBasisDetail() { return basisDetail; }
    public void setBasisDetail(DormFeeBasisVO basisDetail) { this.basisDetail = basisDetail; }
}
