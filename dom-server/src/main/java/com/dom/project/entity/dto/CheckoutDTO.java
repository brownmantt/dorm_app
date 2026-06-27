package com.dom.project.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 退寮 DTO。
 */
public class CheckoutDTO {

    @NotBlank(message = "退寮日を入力してください")
    private String moveOutDate;

    private String moveOutReason;

    public String getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(String moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public String getMoveOutReason() {
        return moveOutReason;
    }

    public void setMoveOutReason(String moveOutReason) {
        this.moveOutReason = moveOutReason;
    }
}
