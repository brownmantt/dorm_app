package com.dom.project.service.impl;

import com.dom.project.entity.Region;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.RegionSaveDTO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.RegionMapper;
import com.dom.project.service.RegionService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 地域マスタサービス実装。
 */
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionMapper regionMapper;
    private final OperationLogWriter operationLogWriter;

    public RegionServiceImpl(RegionMapper regionMapper, OperationLogWriter operationLogWriter) {
        this.regionMapper = regionMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<Region> list(String name, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<Region> list = regionMapper.searchList(null, name, offset, limit);
        Long total = regionMapper.countSearch(null, name);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public Region create(RegionSaveDTO dto) {
        assertCodeNotDuplicate(dto.getCode(), null);
        LocalDateTime now = LocalDateTime.now();
        Region entity = new Region();
        entity.setRegionId(IdGenerator.nextId("RG"));
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        regionMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.REGION_CREATE, TargetTableEnum.REGION, entity.getRegionId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public Region update(String regionId, RegionSaveDTO dto) {
        Region before = regionMapper.findById(regionId);
        if (before == null) {
            throw new BusinessException("REGION_NOT_FOUND", "地域が見つかりません");
        }
        assertCodeNotDuplicate(dto.getCode(), regionId);

        Region entity = new Region();
        entity.setRegionId(regionId);
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setUpdatedAt(LocalDateTime.now());
        regionMapper.update(entity);

        Region after = regionMapper.findById(regionId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.REGION_UPDATE, TargetTableEnum.REGION, regionId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String regionId) {
        Region before = regionMapper.findById(regionId);
        if (before == null) {
            throw new BusinessException("REGION_NOT_FOUND", "地域が見つかりません");
        }
        Long usedCount = regionMapper.countDormitoriesByRegionCode(before.getCode());
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("REGION_IN_USE", "使用中の地域は削除できません");
        }
        regionMapper.logicalDelete(regionId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.REGION_DELETE, TargetTableEnum.REGION, regionId, before);
    }

    private void assertCodeNotDuplicate(String code, String selfId) {
        Region existing = regionMapper.findByCode(code);
        if (existing != null && (selfId == null || !selfId.equals(existing.getRegionId()))) {
            throw new BusinessException("REGION_CODE_DUPLICATE", "地域コードが重複しています");
        }
    }
}
