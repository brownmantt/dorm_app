package com.dom.project.service.impl;

import com.dom.project.entity.StorageLocation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.StorageLocationSaveDTO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.StorageLocationMapper;
import com.dom.project.service.StorageLocationService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 保管場所マスタサービス実装。
 */
@Service
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationMapper storageLocationMapper;
    private final OperationLogWriter operationLogWriter;

    public StorageLocationServiceImpl(StorageLocationMapper storageLocationMapper,
                                      OperationLogWriter operationLogWriter) {
        this.storageLocationMapper = storageLocationMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<StorageLocation> list(String name, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<StorageLocation> list = storageLocationMapper.searchList(name, offset, limit);
        Long total = storageLocationMapper.countSearch(name);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public StorageLocation create(StorageLocationSaveDTO dto) {
        assertNameNotDuplicate(dto.getName(), null);
        LocalDateTime now = LocalDateTime.now();
        StorageLocation entity = new StorageLocation();
        entity.setStorageLocationId(IdGenerator.nextId("SL"));
        entity.setName(dto.getName());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        storageLocationMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.STORAGE_LOCATION_CREATE,
                TargetTableEnum.STORAGE_LOCATION,
                entity.getStorageLocationId(),
                entity);
        return entity;
    }

    @Override
    @Transactional
    public StorageLocation update(String storageLocationId, StorageLocationSaveDTO dto) {
        StorageLocation before = storageLocationMapper.findById(storageLocationId);
        if (before == null) {
            throw new BusinessException("STORAGE_LOCATION_NOT_FOUND", "保管場所が見つかりません");
        }
        assertNameNotDuplicate(dto.getName(), storageLocationId);

        StorageLocation entity = new StorageLocation();
        entity.setStorageLocationId(storageLocationId);
        entity.setName(dto.getName());
        entity.setUpdatedAt(LocalDateTime.now());
        storageLocationMapper.update(entity);

        StorageLocation after = storageLocationMapper.findById(storageLocationId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.STORAGE_LOCATION_UPDATE,
                TargetTableEnum.STORAGE_LOCATION,
                storageLocationId,
                before,
                after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String storageLocationId) {
        StorageLocation before = storageLocationMapper.findById(storageLocationId);
        if (before == null) {
            throw new BusinessException("STORAGE_LOCATION_NOT_FOUND", "保管場所が見つかりません");
        }
        Long usedCount = storageLocationMapper.countEquipmentStoragesByLocationId(storageLocationId);
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("STORAGE_LOCATION_IN_USE", "使用中の保管場所は削除できません");
        }
        storageLocationMapper.logicalDelete(storageLocationId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.STORAGE_LOCATION_DELETE,
                TargetTableEnum.STORAGE_LOCATION,
                storageLocationId,
                before);
    }

    private void assertNameNotDuplicate(String name, String selfId) {
        StorageLocation existing = storageLocationMapper.findByName(name);
        if (existing != null && (selfId == null || !selfId.equals(existing.getStorageLocationId()))) {
            throw new BusinessException("STORAGE_LOCATION_NAME_DUPLICATE", "保管場所名が重複しています");
        }
    }
}
