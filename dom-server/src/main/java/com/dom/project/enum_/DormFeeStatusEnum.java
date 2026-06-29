package com.dom.project.enum_;

/**
 * 寮費ステータス列挙。
 */
public enum DormFeeStatusEnum {

    /** 仮定（算定成功） */
    PROVISIONAL("PROVISIONAL"),
    /** エラー（算定失敗） */
    ERROR("ERROR");

    private final String code;

    DormFeeStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
