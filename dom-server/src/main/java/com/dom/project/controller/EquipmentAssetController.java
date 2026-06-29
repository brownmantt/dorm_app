package com.dom.project.controller;

import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentAssetSaveDTO;
import com.dom.project.entity.view.EquipmentAssetListView;
import com.dom.project.service.EquipmentAssetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 備品（個体）API コントローラ。
 */
@RestController
public class EquipmentAssetController {

    private final EquipmentAssetService equipmentAssetService;

    public EquipmentAssetController(EquipmentAssetService equipmentAssetService) {
        this.equipmentAssetService = equipmentAssetService;
    }

    /**
     * 備品一覧取得。
     */
    @GetMapping("/equipment-assets")
    public PageResult<EquipmentAssetListView> listEquipmentAssets(
            @RequestParam(required = false) String equipmentId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return equipmentAssetService.list(equipmentId, page, size);
    }

    /**
     * 備品登録。
     */
    @PostMapping("/equipment-assets")
    public EquipmentAsset createEquipmentAsset(@Valid @RequestBody EquipmentAssetSaveDTO dto) {
        return equipmentAssetService.create(dto);
    }

    /**
     * 備品更新。
     */
    @PutMapping("/equipment-assets/{id}")
    public EquipmentAsset updateEquipmentAsset(@PathVariable String id,
                                               @Valid @RequestBody EquipmentAssetSaveDTO dto) {
        return equipmentAssetService.update(id, dto);
    }

    /**
     * 備品削除（論理削除）。
     */
    @DeleteMapping("/equipment-assets/{id}")
    public void deleteEquipmentAsset(@PathVariable String id) {
        equipmentAssetService.delete(id);
    }
}
