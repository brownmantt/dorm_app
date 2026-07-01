package com.dom.project.service.impl;

import com.dom.project.entity.Equipment;
import com.dom.project.entity.EquipmentMoveout;
import com.dom.project.entity.ResidenceHistory;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentMoveoutDTO;
import com.dom.project.entity.dto.EquipmentSaveDTO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.EquipmentMapper;
import com.dom.project.mapper.EquipmentMoveoutMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.service.EquipmentService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import com.dom.project.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 備品サービス実装。
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentMoveoutMapper moveoutMapper;
    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final OperationLogWriter operationLogWriter;

    public EquipmentServiceImpl(EquipmentMapper equipmentMapper,
                                EquipmentMoveoutMapper moveoutMapper,
                                ResidenceHistoryMapper residenceHistoryMapper,
                                OperationLogWriter operationLogWriter) {
        this.equipmentMapper = equipmentMapper;
        this.moveoutMapper = moveoutMapper;
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<Equipment> list(Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<Equipment> list = equipmentMapper.searchList(offset, limit);
        Long total = equipmentMapper.countSearch();
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public Equipment create(EquipmentSaveDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Equipment entity = new Equipment();
        entity.setEquipmentId(IdGenerator.nextId("EQ"));
        entity.setName(dto.getName());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        equipmentMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.EQUIPMENT_CREATE, TargetTableEnum.EQUIPMENT, entity.getEquipmentId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public Equipment update(String id, EquipmentSaveDTO dto) {
        Equipment before = equipmentMapper.findById(id);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_NOT_FOUND", "品目が見つかりません");
        }
        Equipment entity = new Equipment();
        entity.setEquipmentId(id);
        entity.setName(dto.getName());
        entity.setUpdatedAt(LocalDateTime.now());
        equipmentMapper.update(entity);
        Equipment after = equipmentMapper.findById(id);
        operationLogWriter.logUpdate(
                OperationTypeEnum.EQUIPMENT_UPDATE, TargetTableEnum.EQUIPMENT, id, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Equipment before = equipmentMapper.findById(id);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_NOT_FOUND", "品目が見つかりません");
        }
        Long refCount = equipmentMapper.countReferences(id);
        if (refCount != null && refCount > 0) {
            throw new BusinessException("EQUIPMENT_IN_USE", "使用中の品目は削除できません");
        }
        equipmentMapper.logicalDelete(id, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.EQUIPMENT_DELETE, TargetTableEnum.EQUIPMENT, id, before);
    }

    @Override
    @Transactional
    public void processMoveout(EquipmentMoveoutDTO dto) {
        ResidenceHistory history = residenceHistoryMapper.findById(dto.getResidenceHistoryId());
        if (history == null) {
            throw new BusinessException("RESIDENCE_NOT_FOUND", "入居履歴が見つかりません");
        }
        Equipment equipment = equipmentMapper.findById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException("EQUIPMENT_NOT_FOUND", "品目が見つかりません");
        }

        LocalDateTime now = LocalDateTime.now();
        EquipmentMoveout moveout = new EquipmentMoveout();
        moveout.setMoveoutId(IdGenerator.nextId("MO"));
        moveout.setResidenceHistoryId(dto.getResidenceHistoryId());
        moveout.setEquipmentId(dto.getEquipmentId());
        moveout.setDisposition(dto.getDisposition());
        moveout.setRemarks(dto.getRemarks());
        moveout.setProcessedAt(now);
        moveout.setCreatedBy(SecurityUtils.currentUsername());
        moveoutMapper.insert(moveout);
        operationLogWriter.logCreate(
                OperationTypeEnum.EQUIPMENT_MOVEOUT, TargetTableEnum.EQUIPMENT_MOVEOUT,
                moveout.getMoveoutId(), moveout);
    }
}
