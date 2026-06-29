package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 備品保存 DTO。
 */
public class EquipmentSaveDTO {

    @NotBlank(message = "品目名称を入力してください")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
