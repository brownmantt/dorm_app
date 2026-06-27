package com.dom.project.service.impl;

import com.dom.project.entity.UsageType;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UsageTypeSaveDTO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.UsageTypeMapper;
import com.dom.project.service.UsageTypeService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 利用形態マスタサービス実装。
 */
@Service
public class UsageTypeServiceImpl implements UsageTypeService {

    private final UsageTypeMapper usageTypeMapper;
    private final OperationLogWriter operationLogWriter;

    public UsageTypeServiceImpl(UsageTypeMapper usageTypeMapper, OperationLogWriter operationLogWriter) {
        this.usageTypeMapper = usageTypeMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<UsageType> list(String name, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<UsageType> list = usageTypeMapper.searchList(null, name, offset, limit);
        Long total = usageTypeMapper.countSearch(null, name);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public UsageType create(UsageTypeSaveDTO dto) {
        assertCodeNotDuplicate(dto.getCode(), null);
        LocalDateTime now = LocalDateTime.now();
        UsageType entity = new UsageType();
        entity.setUsageTypeId(IdGenerator.nextId("UT"));
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        usageTypeMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.USAGE_TYPE_CREATE, TargetTableEnum.USAGE_TYPE, entity.getUsageTypeId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public UsageType update(String usageTypeId, UsageTypeSaveDTO dto) {
        UsageType before = usageTypeMapper.findById(usageTypeId);
        if (before == null) {
            throw new BusinessException("USAGE_TYPE_NOT_FOUND", "利用形態が見つかりません");
        }
        assertCodeNotDuplicate(dto.getCode(), usageTypeId);

        UsageType entity = new UsageType();
        entity.setUsageTypeId(usageTypeId);
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setUpdatedAt(LocalDateTime.now());
        usageTypeMapper.update(entity);

        UsageType after = usageTypeMapper.findById(usageTypeId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.USAGE_TYPE_UPDATE, TargetTableEnum.USAGE_TYPE, usageTypeId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String usageTypeId) {
        UsageType before = usageTypeMapper.findById(usageTypeId);
        if (before == null) {
            throw new BusinessException("USAGE_TYPE_NOT_FOUND", "利用形態が見つかりません");
        }
        usageTypeMapper.logicalDelete(usageTypeId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.USAGE_TYPE_DELETE, TargetTableEnum.USAGE_TYPE, usageTypeId, before);
    }

    private void assertCodeNotDuplicate(String code, String selfId) {
        UsageType existing = usageTypeMapper.findByCode(code);
        if (existing != null && (selfId == null || !selfId.equals(existing.getUsageTypeId()))) {
            throw new BusinessException("USAGE_TYPE_CODE_DUPLICATE", "コード値が重複しています");
        }
    }
}
