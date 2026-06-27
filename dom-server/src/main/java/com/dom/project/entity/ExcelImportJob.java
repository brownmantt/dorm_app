package com.dom.project.entity;

import java.time.LocalDateTime;

/**
 * Excel 取込ジョブエンティティ（テーブル excel_import_job）。
 */
public class ExcelImportJob {

    /** ジョブ ID */
    private String jobId;

    /** ファイル名 */
    private String fileName;

    /** ステータス：PENDING / PREVIEWED / SUCCESS / FAILED */
    private String status;

    /** 実行者 */
    private String executedBy;

    /** 総件数 */
    private Integer totalCount;

    /** 成功件数 */
    private Integer successCount;

    /** エラー件数 */
    private Integer errorCount;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 完了日時 */
    private LocalDateTime finishedAt;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
