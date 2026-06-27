package com.dom.project.exception;

import org.springframework.http.HttpStatus;

/**
 * 業務例外。
 * 業務エラーコードと HTTP ステータスを保持し、全体例外ハンドラーがフロントエンド向け JSON に変換する。
 */
public class BusinessException extends RuntimeException {

    /** 業務エラーコード（例：DORM_CAPACITY_EXCEEDED） */
    private final String code;

    /** HTTP レスポンスステータス */
    private final HttpStatus status;

    /**
     * 業務例外を生成（デフォルト 400）。
     *
     * @param code    業務エラーコード
     * @param message エラーメッセージ
     */
    public BusinessException(String code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 業務例外を生成。
     *
     * @param code    業務エラーコード
     * @param message エラーメッセージ
     * @param status  HTTP ステータス
     */
    public BusinessException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
