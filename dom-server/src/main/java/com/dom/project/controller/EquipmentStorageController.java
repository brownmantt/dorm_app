package com.dom.project.controller;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentStorageBatchSaveDTO;
import com.dom.project.entity.view.EquipmentStorageView;
import com.dom.project.service.EquipmentStorageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 備品保管 API コントローラ。
 */
@RestController
@RequestMapping("/equipment-storages")
public class EquipmentStorageController {

    private final EquipmentStorageService equipmentStorageService;

    public EquipmentStorageController(EquipmentStorageService equipmentStorageService) {
        this.equipmentStorageService = equipmentStorageService;
    }

    @GetMapping
    public PageResult<EquipmentStorageView> list(@RequestParam(required = false) String equipmentAssetId,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size) {
        return equipmentStorageService.list(equipmentAssetId, page, size);
    }

    @GetMapping("/by-asset/{equipmentAssetId}")
    public List<EquipmentStorageView> listByAsset(@PathVariable String equipmentAssetId) {
        return equipmentStorageService.listByAsset(equipmentAssetId);
    }

    @PutMapping("/by-asset/{equipmentAssetId}")
    public void saveByAsset(@PathVariable String equipmentAssetId,
                            @Valid @RequestBody EquipmentStorageBatchSaveDTO dto) {
        equipmentStorageService.saveByAsset(equipmentAssetId, dto);
    }

    @DeleteMapping("/{storageId}")
    public void delete(@PathVariable String storageId) {
        equipmentStorageService.delete(storageId);
    }
}
