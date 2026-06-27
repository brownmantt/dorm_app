package com.dom.project.controller;

import com.dom.project.service.CsvExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CSV エクスポート API コントローラ。
 */
@RestController
@RequestMapping("/exports")
public class ExportController {

    private final CsvExportService csvExportService;

    public ExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping("/csv/residences")
    public ResponseEntity<byte[]> exportResidencesCsv() {
        return toCsvResponse("residences.csv", csvExportService.exportResidencesCsv());
    }

    @GetMapping("/csv/dorm-fees")
    public ResponseEntity<byte[]> exportDormFeesCsv() {
        return toCsvResponse("dorm-fees.csv", csvExportService.exportDormFeesCsv());
    }

    private ResponseEntity<byte[]> toCsvResponse(String fileName, byte[] content) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                .body(content);
    }
}
