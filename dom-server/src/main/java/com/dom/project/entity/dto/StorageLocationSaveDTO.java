package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 保管場所マスタ保存 DTO。
 */
public class StorageLocationSaveDTO {

    @NotBlank(message = "保管場所名を入力してください")
    @Size(max = 100, message = "保管場所名は100文字以内で入力してください")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
