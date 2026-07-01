package com.dom.project.service;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentStorageBatchSaveDTO;
import com.dom.project.entity.view.EquipmentStorageView;

import java.util.List;

/**
 * 備品保管サービス。
 */
public interface EquipmentStorageService {

    PageResult<EquipmentStorageView> list(String equipmentAssetId, Integer page, Integer size);

    List<EquipmentStorageView> listByAsset(String equipmentAssetId);

    void saveByAsset(String equipmentAssetId, EquipmentStorageBatchSaveDTO dto);

    void delete(String storageId);
}
