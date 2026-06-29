package com.dom.project.entity.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寮費算定レスポンス VO。
 */
public class DormFeeCalculateVO {

    /** 算定合計金額（成功分の合計） */
    private BigDecimal amount;
    /** 1件のみの場合の算定根拠（後方互換） */
    private DormFeeBasisVO basis;
    /** 入居履歴ごとの算定結果 */
    private List<DormFeeCalculateItemVO> items;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public DormFeeBasisVO getBasis() { return basis; }
    public void setBasis(DormFeeBasisVO basis) { this.basis = basis; }
    public List<DormFeeCalculateItemVO> getItems() { return items; }
    public void setItems(List<DormFeeCalculateItemVO> items) { this.items = items; }
}
