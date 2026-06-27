package com.dom.project.service;

import com.dom.project.entity.vo.ExcelPreviewVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel 取込業務サービスインターフェース。
 */
public interface ExcelImportService {

    /**
     * プレビュー：ファイル内容を検証し取込予定件数を返却。
     */
    ExcelPreviewVO preview(MultipartFile file);

    /**
     * 実行：プレビュー済みファイルを DB に取り込む（POI 未導入時はスタブ実装）。
     */
    void execute(MultipartFile file);
}
