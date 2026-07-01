package com.dom.project.entity.dto;

import java.time.LocalDate;

/**
 * 備品利用解除 DTO。
 */
public class EquipmentUsageReleaseDTO {

    /** 未指定時はサーバ側で当日を設定 */
    private LocalDate usageEndDate;

    public LocalDate getUsageEndDate() {
        return usageEndDate;
    }

    public void setUsageEndDate(LocalDate usageEndDate) {
        this.usageEndDate = usageEndDate;
    }
}
