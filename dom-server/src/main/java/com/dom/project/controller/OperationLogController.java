package com.dom.project.controller;

import com.dom.project.entity.OperationLog;
import com.dom.project.entity.common.PageResult;
import com.dom.project.service.OperationLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 操作ログ API コントローラ。
 */
@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 操作ログ一覧取得。
     */
    @GetMapping
    public PageResult<OperationLog> list(
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operatedBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate operatedAtFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate operatedAtTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return operationLogService.list(operationType, operatedBy, operatedAtFrom, operatedAtTo, page, size);
    }
}
