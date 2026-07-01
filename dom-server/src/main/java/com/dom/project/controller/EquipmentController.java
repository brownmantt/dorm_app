package com.dom.project.controller;

import com.dom.project.entity.Equipment;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentMoveoutDTO;
import com.dom.project.entity.dto.EquipmentSaveDTO;
import com.dom.project.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 備品 API コントローラ。
 */
@RestController
@RequestMapping
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * 備品一覧取得。
     */
    @GetMapping("/equipments")
    public PageResult<Equipment> listEquipments(@RequestParam(required = false) Integer page,
                                                @RequestParam(required = false) Integer size) {
        return equipmentService.list(page, size);
    }

    /**
     * 備品登録。
     */
    @PostMapping("/equipments")
    public Equipment createEquipment(@Valid @RequestBody EquipmentSaveDTO dto) {
        return equipmentService.create(dto);
    }

    /**
     * 備品更新。
     */
    @PutMapping("/equipments/{id}")
    public Equipment updateEquipment(@PathVariable String id, @Valid @RequestBody EquipmentSaveDTO dto) {
        return equipmentService.update(id, dto);
    }

    /**
     * 品目削除（論理削除）。
     */
    @DeleteMapping("/equipments/{id}")
    public void deleteEquipment(@PathVariable String id) {
        equipmentService.delete(id);
    }

    /**
     * 退去備品処理。
     */
    @PostMapping("/equipment-moveouts")
    public void processMoveout(@Valid @RequestBody EquipmentMoveoutDTO dto) {
        equipmentService.processMoveout(dto);
    }
}
