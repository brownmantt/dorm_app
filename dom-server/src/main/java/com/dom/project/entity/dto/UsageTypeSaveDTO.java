package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 利用形態保存 DTO。
 */
public class UsageTypeSaveDTO {

    @NotBlank(message = "コード値を入力してください")
    @Size(max = 30, message = "コード値は30文字以内で入力してください")
    private String code;

    @NotBlank(message = "名称を入力してください")
    @Size(max = 100, message = "名称は100文字以内で入力してください")
    private String name;

    @NotNull(message = "表示順を入力してください")
    private Integer displayOrder;

    /** 未指定時は業務層で 1 を設定 */
    private Integer minUsageDays;

    /** 未指定時は業務層で -1 を設定 */
    private Integer maxUsageDays;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getMinUsageDays() {
        return minUsageDays;
    }

    public void setMinUsageDays(Integer minUsageDays) {
        this.minUsageDays = minUsageDays;
    }

    public Integer getMaxUsageDays() {
        return maxUsageDays;
    }

    public void setMaxUsageDays(Integer maxUsageDays) {
        this.maxUsageDays = maxUsageDays;
    }
}
