package com.dom.project.enum_;

/**
 * 備品保管ステータス列挙。
 */
public enum EquipmentStorageStatusEnum {

    IN_STORAGE("IN_STORAGE"),
    REUSED("REUSED");

    private final String code;

    EquipmentStorageStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
