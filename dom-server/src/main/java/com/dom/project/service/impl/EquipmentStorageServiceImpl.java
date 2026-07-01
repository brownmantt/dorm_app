package com.dom.project.service.impl;

import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.EquipmentStorage;
import com.dom.project.entity.StorageLocation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentStorageBatchSaveDTO;
import com.dom.project.entity.dto.EquipmentStorageBatchSaveDTO.EquipmentStorageLineSaveDTO;
import com.dom.project.entity.view.EquipmentStorageView;
import com.dom.project.enum_.EquipmentStorageStatusEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.EquipmentAssetMapper;
import com.dom.project.mapper.EquipmentStorageMapper;
import com.dom.project.mapper.StorageLocationMapper;
import com.dom.project.service.EquipmentStorageService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 備品保管サービス実装。
 */
@Service
public class EquipmentStorageServiceImpl implements EquipmentStorageService {

    private final EquipmentStorageMapper storageMapper;
    private final EquipmentAssetMapper equipmentAssetMapper;
    private final StorageLocationMapper storageLocationMapper;
    private final OperationLogWriter operationLogWriter;

    public EquipmentStorageServiceImpl(EquipmentStorageMapper storageMapper,
                                       EquipmentAssetMapper equipmentAssetMapper,
                                       StorageLocationMapper storageLocationMapper,
                                       OperationLogWriter operationLogWriter) {
        this.storageMapper = storageMapper;
        this.equipmentAssetMapper = equipmentAssetMapper;
        this.storageLocationMapper = storageLocationMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<EquipmentStorageView> list(String equipmentAssetId, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<EquipmentStorageView> list = storageMapper.searchList(equipmentAssetId, offset, limit);
        Long total = storageMapper.countSearch(equipmentAssetId);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public List<EquipmentStorageView> listByAsset(String equipmentAssetId) {
        List<EquipmentStorageView> list = storageMapper.findByAssetId(equipmentAssetId);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    @Transactional
    public void saveByAsset(String equipmentAssetId, EquipmentStorageBatchSaveDTO dto) {
        EquipmentAsset asset = equipmentAssetMapper.findById(equipmentAssetId);
        if (asset == null) {
            throw new BusinessException("EQUIPMENT_ASSET_NOT_FOUND", "備品が見つかりません");
        }

        int purchaseQuantity = asset.getPurchaseQuantity() == null ? 1 : asset.getPurchaseQuantity();
        List<EquipmentStorageLineSaveDTO> lines = dto.getLines();
        validateLines(lines, purchaseQuantity);

        storageMapper.deleteByAssetId(equipmentAssetId);

        LocalDateTime now = LocalDateTime.now();
        for (EquipmentStorageLineSaveDTO line : lines) {
            StorageLocation location = storageLocationMapper.findById(line.getStorageLocationId());
            if (location == null) {
                throw new BusinessException("STORAGE_LOCATION_NOT_FOUND", "保管場所が見つかりません");
            }

            EquipmentStorage storage = new EquipmentStorage();
            storage.setStorageId(IdGenerator.nextId("ST"));
            storage.setEquipmentAssetId(equipmentAssetId);
            storage.setStorageLocationId(line.getStorageLocationId());
            storage.setStorageQuantity(line.getStorageQuantity());
            storage.setStatus(resolveStatus(line.getStatus()));
            storage.setLinkedMoveoutId(StringUtils.hasText(line.getLinkedMoveoutId())
                    ? line.getLinkedMoveoutId() : null);
            storage.setCreatedAt(now);
            storage.setUpdatedAt(now);
            storageMapper.insert(storage);
        }

        operationLogWriter.logAction(
                OperationTypeEnum.EQUIPMENT_STORAGE_CREATE,
                TargetTableEnum.EQUIPMENT_STORAGE,
                equipmentAssetId,
                null,
                dto);
    }

    @Override
    @Transactional
    public void delete(String storageId) {
        EquipmentStorage before = storageMapper.findById(storageId);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_STORAGE_NOT_FOUND", "保管情報が見つかりません");
        }
        storageMapper.deleteById(storageId);
        operationLogWriter.logDelete(
                OperationTypeEnum.EQUIPMENT_STORAGE_DELETE,
                TargetTableEnum.EQUIPMENT_STORAGE,
                storageId,
                before);
    }

    private void validateLines(List<EquipmentStorageLineSaveDTO> lines, int purchaseQuantity) {
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException("EQUIPMENT_STORAGE_LINES_REQUIRED", "保管明細を1件以上入力してください");
        }

        if (purchaseQuantity == 1 && lines.size() != 1) {
            throw new BusinessException("EQUIPMENT_STORAGE_LINE_COUNT_INVALID",
                    "購入数量が1の備品は保管場所を1件のみ登録できます");
        }
        if (purchaseQuantity > 1 && lines.size() > purchaseQuantity) {
            throw new BusinessException("EQUIPMENT_STORAGE_LINE_COUNT_EXCEEDED",
                    "保管場所の件数が購入数量を超えています");
        }

        Set<String> locationIds = new HashSet<>();
        int totalQuantity = 0;
        for (EquipmentStorageLineSaveDTO line : lines) {
            if (!locationIds.add(line.getStorageLocationId())) {
                throw new BusinessException("EQUIPMENT_STORAGE_LOCATION_DUPLICATE",
                        "同じ保管場所を複数行に指定できません");
            }
            totalQuantity += line.getStorageQuantity();
        }

        if (totalQuantity != purchaseQuantity) {
            throw new BusinessException("EQUIPMENT_STORAGE_QUANTITY_MISMATCH",
                    String.format("保管数量の合計が購入数量と一致しません（購入数量: %d、保管合計: %d）",
                            purchaseQuantity, totalQuantity));
        }
    }

    private String resolveStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return EquipmentStorageStatusEnum.IN_STORAGE.getCode();
        }
        return status;
    }
}
