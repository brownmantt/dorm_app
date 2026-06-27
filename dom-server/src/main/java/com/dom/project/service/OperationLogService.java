package com.dom.project.service;

import com.dom.project.entity.OperationLog;
import com.dom.project.entity.common.PageResult;

import java.time.LocalDate;

/**
 * 操作ログ業務サービスインターフェース。
 */
public interface OperationLogService {

    PageResult<OperationLog> list(String operationType, String operatedBy,
                                  LocalDate operatedAtFrom, LocalDate operatedAtTo,
                                  Integer page, Integer size);

    /**
     * 操作ログを書き込む（補助メソッド）。
     */
    void writeLog(String operationType, String targetTable, String targetId,
                  String beforeValue, String afterValue, String operatedBy);
}
