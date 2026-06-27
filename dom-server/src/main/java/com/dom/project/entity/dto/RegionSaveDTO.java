package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 地域保存 DTO。
 */
public class RegionSaveDTO {

    @NotBlank(message = "地域コードを入力してください")
    @Size(max = 30, message = "地域コードは30文字以内で入力してください")
    private String code;

    @NotBlank(message = "地域名称を入力してください")
    @Size(max = 100, message = "地域名称は100文字以内で入力してください")
    private String name;

    @NotNull(message = "表示順を入力してください")
    private Integer displayOrder;

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
}
