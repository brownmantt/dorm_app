package com.dom.project.service.impl;

import com.dom.project.entity.Affiliation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.AffiliationSaveDTO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.AffiliationMapper;
import com.dom.project.service.AffiliationService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 所属マスタサービス実装。
 */
@Service
public class AffiliationServiceImpl implements AffiliationService {

    private final AffiliationMapper affiliationMapper;
    private final OperationLogWriter operationLogWriter;

    public AffiliationServiceImpl(AffiliationMapper affiliationMapper, OperationLogWriter operationLogWriter) {
        this.affiliationMapper = affiliationMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<Affiliation> list(String name, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<Affiliation> list = affiliationMapper.searchList(null, name, offset, limit);
        Long total = affiliationMapper.countSearch(null, name);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public Affiliation create(AffiliationSaveDTO dto) {
        assertCodeNotDuplicate(dto.getCode(), null);
        LocalDateTime now = LocalDateTime.now();
        Affiliation entity = new Affiliation();
        entity.setAffiliationId(IdGenerator.nextId("AF"));
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        affiliationMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.AFFILIATION_CREATE, TargetTableEnum.AFFILIATION, entity.getAffiliationId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public Affiliation update(String affiliationId, AffiliationSaveDTO dto) {
        Affiliation before = affiliationMapper.findById(affiliationId);
        if (before == null) {
            throw new BusinessException("AFFILIATION_NOT_FOUND", "所属が見つかりません");
        }
        assertCodeNotDuplicate(dto.getCode(), affiliationId);

        Affiliation entity = new Affiliation();
        entity.setAffiliationId(affiliationId);
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setUpdatedAt(LocalDateTime.now());
        affiliationMapper.update(entity);

        Affiliation after = affiliationMapper.findById(affiliationId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.AFFILIATION_UPDATE, TargetTableEnum.AFFILIATION, affiliationId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String affiliationId) {
        Affiliation before = affiliationMapper.findById(affiliationId);
        if (before == null) {
            throw new BusinessException("AFFILIATION_NOT_FOUND", "所属が見つかりません");
        }
        Long usedCount = affiliationMapper.countEmployeesByAffiliationId(affiliationId);
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("AFFILIATION_IN_USE", "使用中の所属は削除できません");
        }
        affiliationMapper.logicalDelete(affiliationId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.AFFILIATION_DELETE, TargetTableEnum.AFFILIATION, affiliationId, before);
    }

    private void assertCodeNotDuplicate(String code, String selfId) {
        Affiliation existing = affiliationMapper.findByCode(code);
        if (existing != null && (selfId == null || !selfId.equals(existing.getAffiliationId()))) {
            throw new BusinessException("AFFILIATION_CODE_DUPLICATE", "所属コードが重複しています");
        }
    }
}
