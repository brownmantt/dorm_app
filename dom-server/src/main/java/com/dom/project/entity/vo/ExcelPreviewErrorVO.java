package com.dom.project.entity.vo;

/**
 * Excel プレビューエラー VO。
 */
public class ExcelPreviewErrorVO {

    private Integer rowNumber;
    private String field;
    private String message;

    public ExcelPreviewErrorVO() {
    }

    public ExcelPreviewErrorVO(Integer rowNumber, String field, String message) {
        this.rowNumber = rowNumber;
        this.field = field;
        this.message = message;
    }

    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
