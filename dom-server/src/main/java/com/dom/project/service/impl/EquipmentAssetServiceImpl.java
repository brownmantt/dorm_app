package com.dom.project.service.impl;

import com.dom.project.entity.Equipment;
import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentAssetSaveDTO;
import com.dom.project.entity.view.EquipmentAssetListView;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.EquipmentAssetMapper;
import com.dom.project.mapper.EquipmentMapper;
import com.dom.project.service.EquipmentAssetService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 備品（個体）サービス実装。
 */
@Service
public class EquipmentAssetServiceImpl implements EquipmentAssetService {

    private final EquipmentAssetMapper equipmentAssetMapper;
    private final EquipmentMapper equipmentMapper;
    private final OperationLogWriter operationLogWriter;

    public EquipmentAssetServiceImpl(EquipmentAssetMapper equipmentAssetMapper,
                                     EquipmentMapper equipmentMapper,
                                     OperationLogWriter operationLogWriter) {
        this.equipmentAssetMapper = equipmentAssetMapper;
        this.equipmentMapper = equipmentMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<EquipmentAssetListView> list(String equipmentId, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<EquipmentAssetListView> list = equipmentAssetMapper.searchList(equipmentId, offset, limit);
        Long total = equipmentAssetMapper.countSearch(equipmentId);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public EquipmentAsset create(EquipmentAssetSaveDTO dto) {
        assertItemExists(dto.getEquipmentId());
        LocalDateTime now = LocalDateTime.now();
        EquipmentAsset entity = toEntity(dto, IdGenerator.nextId("EB"), now, now);
        equipmentAssetMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.EQUIPMENT_ASSET_CREATE, TargetTableEnum.EQUIPMENT_ASSET,
                entity.getEquipmentAssetId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public EquipmentAsset update(String equipmentAssetId, EquipmentAssetSaveDTO dto) {
        EquipmentAsset before = equipmentAssetMapper.findById(equipmentAssetId);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_ASSET_NOT_FOUND", "備品が見つかりません");
        }
        assertItemExists(dto.getEquipmentId());

        LocalDateTime now = LocalDateTime.now();
        EquipmentAsset entity = toEntity(dto, equipmentAssetId, before.getCreatedAt(), now);
        equipmentAssetMapper.update(entity);

        EquipmentAsset after = equipmentAssetMapper.findById(equipmentAssetId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.EQUIPMENT_ASSET_UPDATE, TargetTableEnum.EQUIPMENT_ASSET,
                equipmentAssetId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String equipmentAssetId) {
        EquipmentAsset before = equipmentAssetMapper.findById(equipmentAssetId);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_ASSET_NOT_FOUND", "備品が見つかりません");
        }
        equipmentAssetMapper.logicalDelete(equipmentAssetId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.EQUIPMENT_ASSET_DELETE, TargetTableEnum.EQUIPMENT_ASSET,
                equipmentAssetId, before);
    }

    private void assertItemExists(String equipmentId) {
        Equipment item = equipmentMapper.findById(equipmentId);
        if (item == null) {
            throw new BusinessException("EQUIPMENT_NOT_FOUND", "品目が見つかりません");
        }
    }

    private EquipmentAsset toEntity(EquipmentAssetSaveDTO dto, String id,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        EquipmentAsset entity = new EquipmentAsset();
        entity.setEquipmentAssetId(id);
        entity.setEquipmentId(dto.getEquipmentId());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setPurchaseAmount(dto.getPurchaseAmount());
        entity.setPurchaseStore(blankToNull(dto.getPurchaseStore()));
        entity.setPurchaseStoreContact(blankToNull(dto.getPurchaseStoreContact()));
        entity.setPurchaseStorePostalCode(blankToNull(dto.getPurchaseStorePostalCode()));
        entity.setPurchaseStoreAddress(blankToNull(dto.getPurchaseStoreAddress()));
        entity.setWarrantyExpiryDate(dto.getWarrantyExpiryDate());
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
