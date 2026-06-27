package com.dom.project.controller;

import com.dom.project.entity.vo.ExcelPreviewVO;
import com.dom.project.service.ExcelImportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel 取込 API コントローラ。
 */
@RestController
@RequestMapping("/imports/excel")
public class ImportController {

    private final ExcelImportService excelImportService;

    public ImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    /**
     * Excel 取込プレビュー。
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExcelPreviewVO preview(@RequestParam("file") MultipartFile file) {
        return excelImportService.preview(file);
    }

    /**
     * Excel 取込実行。
     */
    @PostMapping(value = "/execute", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void execute(@RequestParam("file") MultipartFile file) {
        excelImportService.execute(file);
    }
}
