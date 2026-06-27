package com.dom.project.enum_;

/**
 * Excel 取込ジョブステータス列挙。
 */
public enum ExcelImportJobStatusEnum {

    PENDING("PENDING"),
    PREVIEWED("PREVIEWED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String code;

    ExcelImportJobStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
