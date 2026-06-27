package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 備品保存 DTO。
 */
public class EquipmentSaveDTO {

    @NotBlank(message = "備品名称を入力してください")
    private String name;

    @NotBlank(message = "備品種別を入力してください")
    private String equipmentType;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
}
