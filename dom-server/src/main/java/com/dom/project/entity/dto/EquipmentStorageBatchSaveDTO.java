package com.dom.project.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 備品保管一括保存 DTO（備品ごとの保管場所・数量）。
 */
public class EquipmentStorageBatchSaveDTO {

    @NotEmpty(message = "保管明細を1件以上入力してください")
    @Valid
    private List<EquipmentStorageLineSaveDTO> lines;

    public List<EquipmentStorageLineSaveDTO> getLines() {
        return lines;
    }

    public void setLines(List<EquipmentStorageLineSaveDTO> lines) {
        this.lines = lines;
    }

    /**
     * 保管明細行 DTO。
     */
    public static class EquipmentStorageLineSaveDTO {

        @NotBlank(message = "保管場所を選択してください")
        private String storageLocationId;

        @NotNull(message = "保管数量を入力してください")
        @Min(value = 1, message = "保管数量は1以上で入力してください")
        private Integer storageQuantity;

        private String status;

        private String linkedMoveoutId;

        public String getStorageLocationId() {
            return storageLocationId;
        }

        public void setStorageLocationId(String storageLocationId) {
            this.storageLocationId = storageLocationId;
        }

        public Integer getStorageQuantity() {
            return storageQuantity;
        }

        public void setStorageQuantity(Integer storageQuantity) {
            this.storageQuantity = storageQuantity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLinkedMoveoutId() {
            return linkedMoveoutId;
        }

        public void setLinkedMoveoutId(String linkedMoveoutId) {
            this.linkedMoveoutId = linkedMoveoutId;
        }
    }
}
