package com.dom.project.service.impl;

import com.dom.project.entity.OperationLog;
import com.dom.project.entity.common.PageResult;
import com.dom.project.mapper.OperationLogMapper;
import com.dom.project.service.OperationLogService;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 操作ログ業務サービス実装。
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public PageResult<OperationLog> list(String operationType, String operatedBy,
                                         LocalDate operatedAtFrom, LocalDate operatedAtTo,
                                         Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<OperationLog> list = operationLogMapper.searchList(
                operationType, operatedBy, operatedAtFrom, operatedAtTo, offset, limit);
        Long total = operationLogMapper.countSearch(operationType, operatedBy, operatedAtFrom, operatedAtTo);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public void writeLog(String operationType, String targetTable, String targetId,
                         String beforeValue, String afterValue, String operatedBy) {
        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setTargetTable(targetTable);
        log.setTargetId(targetId);
        log.setBeforeValue(beforeValue);
        log.setAfterValue(afterValue);
        log.setOperatedBy(operatedBy);
        log.setOperatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
