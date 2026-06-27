package com.dom.project.entity.vo;

import java.util.Collections;
import java.util.List;

/**
 * Excel プレビューレスポンス VO。
 */
public class ExcelPreviewVO {

    private Integer insertCount;
    private Integer totalCount;
    private Integer errorCount;
    private List<ExcelPreviewErrorVO> errors = Collections.emptyList();

    public Integer getInsertCount() { return insertCount; }
    public void setInsertCount(Integer insertCount) { this.insertCount = insertCount; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    public List<ExcelPreviewErrorVO> getErrors() { return errors; }
    public void setErrors(List<ExcelPreviewErrorVO> errors) { this.errors = errors == null ? Collections.emptyList() : errors; }
}
