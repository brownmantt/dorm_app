package com.dom.project.entity.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 備品一覧表示用 View。
 */
public class EquipmentAssetListView {

    private String equipmentAssetId;
    private String equipmentId;
    private String equipmentName;
    private LocalDate purchaseDate;
    private BigDecimal purchaseAmount;
    private String purchaseStore;
    private String purchaseStoreContact;
    private String purchaseStorePostalCode;
    private String purchaseStoreAddress;
    private LocalDate warrantyExpiryDate;
    private Integer purchaseQuantity;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getEquipmentAssetId() {
        return equipmentAssetId;
    }

    public void setEquipmentAssetId(String equipmentAssetId) {
        this.equipmentAssetId = equipmentAssetId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
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

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
