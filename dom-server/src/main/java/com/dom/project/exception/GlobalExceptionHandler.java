package com.dom.project.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全体例外ハンドラー。
 * フロントエンド Axios インターセプター向けに、成功時は業務 JSON、失敗時は {detail} または {message} を返す。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 業務例外の処理。
     *
     * @param ex 業務例外
     * @return message フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        log.warn("業務例外 [{}]: {}", ex.getCode(), ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        body.put("code", ex.getCode());
        return ResponseEntity.status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /**
     * リクエストボディ JSON バリデーション失敗（@Valid）。
     *
     * @param ex バリデーション例外
     * @return detail フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        log.warn("パラメータ検証失敗: {}", detail);
        return problemResponse(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * 単一パラメータ検証失敗（@Validated）。
     *
     * @param ex 制約違反例外
     * @return detail フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("パラメータ検証失敗: {}", detail);
        return problemResponse(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * 必須リクエストパラメータ不足。
     *
     * @param ex パラメータ不足例外
     * @return detail フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String detail = String.format("必須パラメータが不足しています: %s", ex.getParameterName());
        log.warn(detail);
        return problemResponse(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * リクエストボディ解析失敗。
     *
     * @param ex メッセージ読み取り不可例外
     * @return detail フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("リクエストボディの解析に失敗しました: {}", ex.getMessage());
        return problemResponse(HttpStatus.BAD_REQUEST, "リクエストボディの形式が正しくありません");
    }

    /**
     * DB 制約違反（文字数超過・一意制約など）。
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String detail = resolveDataIntegrityMessage(ex);
        log.warn("データ整合性エラー: {}", ex.getMessage());
        return problemResponse(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * 未捕捉のシステム例外。
     *
     * @param ex 不明な例外
     * @return detail フィールドを含むエラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        log.error("システム例外", ex);
        return problemResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "サーバー内部エラーが発生しました。しばらくしてから再度お試しください。");
    }

    private String formatFieldError(FieldError error) {
        return error.getDefaultMessage();
    }

    private String resolveDataIntegrityMessage(DataIntegrityViolationException ex) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        if (message.contains("address") || message.contains("varchar(200)")) {
            return "住所は200文字以内で入力してください";
        }
        if (message.contains("name") || message.contains("varchar(100)")) {
            return "寮名称は100文字以内で入力してください";
        }
        if (message.contains("postal_code") || message.contains("varchar(7)")) {
            return "郵便番号は7桁の数字で入力してください";
        }
        if (message.contains("nearest_station") || message.contains("varchar(20)")) {
            return "最寄駅は20文字以内で入力してください";
        }
        if (message.contains("Room capacity exceeded") || message.contains("capacity exceeded")) {
            return "部屋の定員を超えています";
        }
        if (message.contains("pk_") || message.contains("unique") || message.contains("duplicate key")) {
            return "同じデータが既に登録されています。画面を更新して再度お試しください";
        }
        return "入力内容がデータベースの制約に違反しています";
    }

    private ResponseEntity<Map<String, String>> problemResponse(HttpStatus status, String detail) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("detail", detail);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
