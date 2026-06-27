package com.dom.project.entity.vo;

import java.math.BigDecimal;

/**
 * 寮費算定レスポンス VO。
 */
public class DormFeeCalculateVO {

    private BigDecimal amount;
    private DormFeeBasisVO basis;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public DormFeeBasisVO getBasis() { return basis; }
    public void setBasis(DormFeeBasisVO basis) { this.basis = basis; }
}
