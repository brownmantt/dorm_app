package com.dom.project.service;

import com.dom.project.entity.Equipment;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentMoveoutDTO;
import com.dom.project.entity.dto.EquipmentSaveDTO;
import com.dom.project.entity.dto.EquipmentStorageSaveDTO;
import com.dom.project.entity.view.EquipmentStorageView;

/**
 * 備品業務サービスインターフェース。
 */
public interface EquipmentService {

    PageResult<Equipment> list(Integer page, Integer size);

    Equipment create(EquipmentSaveDTO dto);

    Equipment update(String id, EquipmentSaveDTO dto);

    void delete(String id);

    void processMoveout(EquipmentMoveoutDTO dto);

    PageResult<EquipmentStorageView> listStorages(Integer page, Integer size);

    void createStorage(EquipmentStorageSaveDTO dto);
}
