package com.dom.project.util;

import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.service.OperationLogService;
import org.springframework.stereotype.Component;

/**
 * 操作ログ書き込みヘルパー。
 */
@Component
public class OperationLogWriter {

    private final OperationLogService operationLogService;

    public OperationLogWriter(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    public void logCreate(OperationTypeEnum operationType, TargetTableEnum targetTable,
                          String targetId, Object afterValue) {
        write(operationType, targetTable, targetId, null, afterValue, SecurityUtils.currentUsername());
    }

    public void logUpdate(OperationTypeEnum operationType, TargetTableEnum targetTable,
                          String targetId, Object beforeValue, Object afterValue) {
        write(operationType, targetTable, targetId, beforeValue, afterValue, SecurityUtils.currentUsername());
    }

    public void logDelete(OperationTypeEnum operationType, TargetTableEnum targetTable,
                          String targetId, Object beforeValue) {
        write(operationType, targetTable, targetId, beforeValue, null, SecurityUtils.currentUsername());
    }

    public void logAction(OperationTypeEnum operationType, TargetTableEnum targetTable,
                          String targetId, Object beforeValue, Object afterValue) {
        write(operationType, targetTable, targetId, beforeValue, afterValue, SecurityUtils.currentUsername());
    }

    public void logAction(OperationTypeEnum operationType, TargetTableEnum targetTable,
                          String targetId, Object beforeValue, Object afterValue, String operatedBy) {
        write(operationType, targetTable, targetId, beforeValue, afterValue, operatedBy);
    }

    private void write(OperationTypeEnum operationType, TargetTableEnum targetTable,
                       String targetId, Object beforeValue, Object afterValue, String operatedBy) {
        operationLogService.writeLog(
                operationType.getCode(),
                targetTable.getCode(),
                targetId,
                JsonUtils.toJson(beforeValue),
                JsonUtils.toJson(afterValue),
                operatedBy);
    }
}
