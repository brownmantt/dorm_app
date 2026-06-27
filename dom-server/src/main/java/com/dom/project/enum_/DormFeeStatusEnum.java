package com.dom.project.enum_;

/**
 * 寮費ステータス列挙。
 */
public enum DormFeeStatusEnum {

    DRAFT("DRAFT"),
    CONFIRMED("CONFIRMED");

    private final String code;

    DormFeeStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
