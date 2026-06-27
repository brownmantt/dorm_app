package com.dom.project.constant;

/**
 * アプリケーション全体定数。
 */
public final class AppConstants {

    /** 项目名称，用于 Redis Key 前缀等 */
    public static final String PROJECT_NAME = "dom";

    /** デフォルトページ番号（0 始まり、Element Plus 対応） */
    public static final Integer DEFAULT_PAGE = 0;

    /** デフォルトページサイズ */
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    /** 業務 ID プレフィックスの最大長 */
    public static final Integer ID_PREFIX_MAX_LENGTH = 3;

    /** 論理削除：未削除 */
    public static final Integer NOT_DELETED = 0;

    /** 論理削除：削除済み */
    public static final Integer DELETED = 1;

    /** Redis Key 分隔符 */
    public static final String REDIS_KEY_SEPARATOR = ":";

    /** システム操作者（未認証時のフォールバック） */
    public static final String SYSTEM_OPERATOR = "system";

    /** 管理者ロール */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /** 一般ユーザーロール */
    public static final String ROLE_USER = "ROLE_USER";

    /** 長期利用警告閾値（年） */
    public static final Integer LONG_TERM_USAGE_THRESHOLD_YEARS = 10;

    /** 退寮警告日数 */
    public static final Integer MOVE_OUT_WARNING_DAYS = 14;

    /** Excel 取込ファイル最大サイズ（バイト） */
    public static final Long MAX_EXCEL_FILE_SIZE_BYTES = 10L * 1024 * 1024;

    private AppConstants() {
    }
}
