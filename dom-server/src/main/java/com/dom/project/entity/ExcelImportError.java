package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * Excel 取込エラー明細エンティティ（テーブル excel_import_error）。
 */
public class ExcelImportError {

    /** エラー ID */
    private Long errorId;

    /** ジョブ ID */
    private String jobId;

    /** Excel 行番号 */
    private Integer rowNumber;

    /** エラー項目名 */
    private String field;

    /** エラーメッセージ */
    private String message;

    /** 作成日時 */
    private LocalDateTime createdAt;

    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
