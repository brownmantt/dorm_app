package com.dom.project.entity.vo;

/**
 * 入居業務検証レスポンス VO。
 */
public class ValidateVO {

    private Boolean valid;
    private String message;

    public ValidateVO() {
    }

    public ValidateVO(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static ValidateVO ok() {
        return new ValidateVO(Boolean.TRUE, null);
    }

    public static ValidateVO fail(String message) {
        return new ValidateVO(Boolean.FALSE, message);
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
