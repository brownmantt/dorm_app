package com.dom.project.enum_;

/**
 * 性別列挙（社員・寮種別判定用）。
 */
public enum GenderEnum {

    MALE("MALE"),
    FEMALE("FEMALE");

    private final String code;

    GenderEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
