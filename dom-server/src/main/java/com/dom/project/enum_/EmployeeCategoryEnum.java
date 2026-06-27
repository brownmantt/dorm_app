package com.dom.project.enum_;

/**
 * 社員区分列挙。
 */
public enum EmployeeCategoryEnum {

    JAPAN("JAPAN"),
    CHINA_ASSIGN("CHINA_ASSIGN");

    private final String code;

    EmployeeCategoryEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
