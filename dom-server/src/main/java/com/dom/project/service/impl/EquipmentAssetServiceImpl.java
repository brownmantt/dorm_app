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
import com.dom.project.mapper.EquipmentStorageMapper;
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

    private static final int MAX_PURCHASE_QUANTITY = 999;
    /** 備品番号の桁数（EB + yyyyMMdd + 4桁連番） */
    private static final int EQUIPMENT_ASSET_ID_LENGTH = 14;

    private final EquipmentAssetMapper equipmentAssetMapper;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentStorageMapper equipmentStorageMapper;
    private final OperationLogWriter operationLogWriter;

    public EquipmentAssetServiceImpl(EquipmentAssetMapper equipmentAssetMapper,
                                     EquipmentMapper equipmentMapper,
                                     EquipmentStorageMapper equipmentStorageMapper,
                                     OperationLogWriter operationLogWriter) {
        this.equipmentAssetMapper = equipmentAssetMapper;
        this.equipmentMapper = equipmentMapper;
        this.equipmentStorageMapper = equipmentStorageMapper;
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
        int quantity = resolvePurchaseQuantity(dto.getPurchaseQuantity());
        LocalDateTime now = LocalDateTime.now();
        EquipmentAsset entity = toEntity(dto, nextEquipmentAssetId(), quantity, now, now);
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

        int quantity = resolvePurchaseQuantity(dto.getPurchaseQuantity());
        LocalDateTime now = LocalDateTime.now();
        EquipmentAsset entity = toEntity(dto, equipmentAssetId, quantity, before.getCreatedAt(), now);
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
        Long storageCount = equipmentStorageMapper.countByAssetId(equipmentAssetId);
        if (storageCount != null && storageCount > 0) {
            throw new BusinessException("EQUIPMENT_ASSET_IN_STORAGE", "保管登録がある備品は削除できません");
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

    private int resolvePurchaseQuantity(Integer purchaseQuantity) {
        int quantity = purchaseQuantity == null ? 1 : purchaseQuantity;
        if (quantity < 1) {
            throw new BusinessException("INVALID_PURCHASE_QUANTITY", "購入数量は1以上で入力してください");
        }
        if (quantity > MAX_PURCHASE_QUANTITY) {
            throw new BusinessException("INVALID_PURCHASE_QUANTITY", "購入数量は999以下で入力してください");
        }
        return quantity;
    }

    /**
     * 備品番号を採番する（14桁固定: EB + yyyyMMdd + 4桁連番）。
     */
    private String nextEquipmentAssetId() {
        String id = IdGenerator.nextId("EB");
        if (id.length() != EQUIPMENT_ASSET_ID_LENGTH) {
            throw new BusinessException("EQUIPMENT_ASSET_ID_GENERATION_FAILED", "備品番号の採番に失敗しました");
        }
        return id;
    }

    private EquipmentAsset toEntity(EquipmentAssetSaveDTO dto, String id, Integer purchaseQuantity,
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
        entity.setPurchaseQuantity(purchaseQuantity);
        entity.setRemarks(blankToNull(dto.getRemarks()));
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
