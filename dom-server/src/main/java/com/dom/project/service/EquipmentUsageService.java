package com.dom.project.service;

import com.dom.project.entity.EquipmentUsage;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentUsageReleaseDTO;
import com.dom.project.entity.dto.EquipmentUsageSaveDTO;
import com.dom.project.entity.view.EquipmentUsageListView;

/**
 * 備品利用サービス。
 */
public interface EquipmentUsageService {

    PageResult<EquipmentUsageListView> list(String equipmentAssetId, String dormitoryId, String roomId,
                                            String employeeId, Boolean activeOnly, Integer page, Integer size);

    EquipmentUsage create(EquipmentUsageSaveDTO dto);

    EquipmentUsage release(String usageId, EquipmentUsageReleaseDTO dto);
}
