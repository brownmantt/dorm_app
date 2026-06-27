package com.dom.project.service.impl;

import com.dom.project.entity.ExcelImportJob;
import com.dom.project.entity.vo.ExcelPreviewVO;
import com.dom.project.enum_.ExcelImportJobStatusEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.ExcelImportMapper;
import com.dom.project.service.ExcelImportService;
import com.dom.project.constant.AppConstants;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Excel 取込サービス実装（POI 未導入のためスタブ）。
 */
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private final ExcelImportMapper excelImportMapper;
    private final OperationLogWriter operationLogWriter;

    public ExcelImportServiceImpl(ExcelImportMapper excelImportMapper,
                                  OperationLogWriter operationLogWriter) {
        this.excelImportMapper = excelImportMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public ExcelPreviewVO preview(MultipartFile file) {
        validateFile(file);
        ExcelPreviewVO vo = new ExcelPreviewVO();
        vo.setInsertCount(0);
        vo.setTotalCount(0);
        vo.setErrorCount(0);
        return vo;
    }

    @Override
    @Transactional
    public void execute(MultipartFile file) {
        validateFile(file);
        LocalDateTime now = LocalDateTime.now();
        ExcelImportJob job = new ExcelImportJob();
        job.setJobId(IdGenerator.nextId("JOB"));
        job.setFileName(file.getOriginalFilename());
        job.setStatus(ExcelImportJobStatusEnum.SUCCESS.getCode());
        job.setExecutedBy(SecurityUtils.currentUsername());
        job.setTotalCount(0);
        job.setSuccessCount(0);
        job.setErrorCount(0);
        job.setCreatedAt(now);
        job.setFinishedAt(now);
        excelImportMapper.insertJob(job);
        operationLogWriter.logCreate(
                OperationTypeEnum.EXCEL_IMPORT_EXECUTE, TargetTableEnum.EXCEL_IMPORT_JOB,
                job.getJobId(), job);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("FILE_REQUIRED", "ファイルが指定されていません");
        }
        if (file.getSize() > AppConstants.MAX_EXCEL_FILE_SIZE_BYTES) {
            throw new BusinessException("FILE_TOO_LARGE", "ファイルサイズは10MB以下にしてください");
        }
        String name = file.getOriginalFilename();
        if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
            throw new BusinessException("FILE_INVALID", "xlsx 形式のファイルを指定してください");
        }
    }
}
