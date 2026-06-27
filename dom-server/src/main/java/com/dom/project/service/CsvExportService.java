package com.dom.project.service;

/**
 * CSV エクスポートサービス。
 */
public interface CsvExportService {

    byte[] exportResidencesCsv();

    byte[] exportDormFeesCsv();
}
