package com.dom.project.entity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 備品保存 DTO。
 */
public class EquipmentAssetSaveDTO {

    @NotBlank(message = "品目を選択してください")
    private String equipmentId;

    @NotNull(message = "購入日を入力してください")
    private LocalDate purchaseDate;

    @NotNull(message = "購入金額を入力してください")
    @DecimalMin(value = "0", message = "購入金額は0以上で入力してください")
    private BigDecimal purchaseAmount;

    @Size(max = 100, message = "購入店は100文字以内で入力してください")
    private String purchaseStore;

    @Size(max = 50, message = "購入店連絡先は50文字以内で入力してください")
    private String purchaseStoreContact;

    @Pattern(regexp = "^(\\d{7})?$", message = "購入店郵便番号は7桁の数字で入力してください")
    private String purchaseStorePostalCode;

    @Size(max = 500, message = "購入店住所は500文字以内で入力してください")
    private String purchaseStoreAddress;

    private LocalDate warrantyExpiryDate;

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getPurchaseStore() {
        return purchaseStore;
    }

    public void setPurchaseStore(String purchaseStore) {
        this.purchaseStore = purchaseStore;
    }

    public String getPurchaseStoreContact() {
        return purchaseStoreContact;
    }

    public void setPurchaseStoreContact(String purchaseStoreContact) {
        this.purchaseStoreContact = purchaseStoreContact;
    }

    public String getPurchaseStorePostalCode() {
        return purchaseStorePostalCode;
    }

    public void setPurchaseStorePostalCode(String purchaseStorePostalCode) {
        this.purchaseStorePostalCode = purchaseStorePostalCode;
    }

    public String getPurchaseStoreAddress() {
        return purchaseStoreAddress;
    }

    public void setPurchaseStoreAddress(String purchaseStoreAddress) {
        this.purchaseStoreAddress = purchaseStoreAddress;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }
}
