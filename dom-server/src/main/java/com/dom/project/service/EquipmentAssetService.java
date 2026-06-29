package com.dom.project.service;

import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentAssetSaveDTO;
import com.dom.project.entity.view.EquipmentAssetListView;

/**
 * 備品（個体）サービス。
 */
public interface EquipmentAssetService {

    PageResult<EquipmentAssetListView> list(String equipmentId, Integer page, Integer size);

    EquipmentAsset create(EquipmentAssetSaveDTO dto);

    EquipmentAsset update(String equipmentAssetId, EquipmentAssetSaveDTO dto);

    void delete(String equipmentAssetId);
}
