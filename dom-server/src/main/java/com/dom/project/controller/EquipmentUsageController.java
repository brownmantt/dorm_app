package com.dom.project.controller;

import com.dom.project.entity.EquipmentUsage;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentUsageReleaseDTO;
import com.dom.project.entity.dto.EquipmentUsageSaveDTO;
import com.dom.project.entity.view.EquipmentUsageListView;
import com.dom.project.service.EquipmentUsageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 備品利用 API コントローラ。
 */
@RestController
public class EquipmentUsageController {

    private final EquipmentUsageService equipmentUsageService;

    public EquipmentUsageController(EquipmentUsageService equipmentUsageService) {
        this.equipmentUsageService = equipmentUsageService;
    }

    /**
     * 備品利用一覧取得。
     */
    @GetMapping("/equipment-usages")
    public PageResult<EquipmentUsageListView> listEquipmentUsages(
            @RequestParam(required = false) String equipmentAssetId,
            @RequestParam(required = false) String dormitoryId,
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return equipmentUsageService.list(
                equipmentAssetId, dormitoryId, roomId, employeeId, activeOnly, page, size);
    }

    /**
     * 備品利用登録。
     */
    @PostMapping("/equipment-usages")
    public EquipmentUsage createEquipmentUsage(@Valid @RequestBody EquipmentUsageSaveDTO dto) {
        return equipmentUsageService.create(dto);
    }

    /**
     * 備品利用解除。
     */
    @PutMapping("/equipment-usages/{id}/release")
    public EquipmentUsage releaseEquipmentUsage(@PathVariable String id,
                                                @RequestBody(required = false) EquipmentUsageReleaseDTO dto) {
        return equipmentUsageService.release(id, dto);
    }
}
