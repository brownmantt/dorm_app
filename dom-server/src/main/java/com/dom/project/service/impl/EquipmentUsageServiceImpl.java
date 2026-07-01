package com.dom.project.service.impl;

import com.dom.project.entity.Dormitory;
import com.dom.project.entity.Employee;
import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.EquipmentUsage;
import com.dom.project.entity.Room;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EquipmentUsageReleaseDTO;
import com.dom.project.entity.dto.EquipmentUsageSaveDTO;
import com.dom.project.entity.view.EquipmentUsageListView;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormitoryMapper;
import com.dom.project.mapper.EmployeeMapper;
import com.dom.project.mapper.EquipmentAssetMapper;
import com.dom.project.mapper.EquipmentUsageMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.service.EquipmentUsageService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 備品利用サービス実装。
 */
@Service
public class EquipmentUsageServiceImpl implements EquipmentUsageService {

    private static final int USAGE_ID_LENGTH = 14;

    private final EquipmentUsageMapper equipmentUsageMapper;
    private final EquipmentAssetMapper equipmentAssetMapper;
    private final DormitoryMapper dormitoryMapper;
    private final RoomMapper roomMapper;
    private final EmployeeMapper employeeMapper;
    private final OperationLogWriter operationLogWriter;

    public EquipmentUsageServiceImpl(EquipmentUsageMapper equipmentUsageMapper,
                                     EquipmentAssetMapper equipmentAssetMapper,
                                     DormitoryMapper dormitoryMapper,
                                     RoomMapper roomMapper,
                                     EmployeeMapper employeeMapper,
                                     OperationLogWriter operationLogWriter) {
        this.equipmentUsageMapper = equipmentUsageMapper;
        this.equipmentAssetMapper = equipmentAssetMapper;
        this.dormitoryMapper = dormitoryMapper;
        this.roomMapper = roomMapper;
        this.employeeMapper = employeeMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<EquipmentUsageListView> list(String equipmentAssetId, String dormitoryId, String roomId,
                                                   String employeeId, Boolean activeOnly, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<EquipmentUsageListView> list = equipmentUsageMapper.searchList(
                equipmentAssetId, dormitoryId, roomId, employeeId, activeOnly, offset, limit);
        Long total = equipmentUsageMapper.countSearch(
                equipmentAssetId, dormitoryId, roomId, employeeId, activeOnly);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public EquipmentUsage create(EquipmentUsageSaveDTO dto) {
        EquipmentAsset asset = equipmentAssetMapper.findById(dto.getEquipmentAssetId());
        if (asset == null) {
            throw new BusinessException("EQUIPMENT_ASSET_NOT_FOUND", "備品が見つかりません");
        }

        String dormitoryId = blankToNull(dto.getDormitoryId());
        String roomId = blankToNull(dto.getRoomId());
        String employeeId = blankToNull(dto.getEmployeeId());
        if (!StringUtils.hasText(dormitoryId) && !StringUtils.hasText(employeeId)) {
            throw new BusinessException("DORMITORY_OR_EMPLOYEE_REQUIRED", "寮または入居者のいずれかを指定してください");
        }

        if (StringUtils.hasText(dormitoryId)) {
            Dormitory dormitory = dormitoryMapper.findById(dormitoryId);
            if (dormitory == null) {
                throw new BusinessException("DORMITORY_NOT_FOUND", "寮が見つかりません");
            }
        }

        if (StringUtils.hasText(roomId)) {
            Room room = roomMapper.findById(roomId);
            if (room == null) {
                throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
            }
            if (StringUtils.hasText(dormitoryId) && !dormitoryId.equals(room.getDormitoryId())) {
                throw new BusinessException("ROOM_DORMITORY_MISMATCH", "部屋が選択した寮に属していません");
            }
        }

        if (StringUtils.hasText(employeeId)) {
            Employee employee = employeeMapper.findById(employeeId);
            if (employee == null) {
                throw new BusinessException("EMPLOYEE_NOT_FOUND", "入居者が見つかりません");
            }
        }

        int quantity = dto.getUsageQuantity() == null ? 1 : dto.getUsageQuantity();
        if (quantity < 1) {
            throw new BusinessException("INVALID_USAGE_QUANTITY", "利用個数は1以上で入力してください");
        }

        LocalDate startDate = dto.getUsageStartDate();
        LocalDate endDate = dto.getUsageEndDate();
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("INVALID_USAGE_DATE_RANGE", "利用終了日は利用開始日以降を指定してください");
        }

        assertWithinPurchaseQuantity(dto.getEquipmentAssetId(), quantity, null, asset.getPurchaseQuantity());

        LocalDateTime now = LocalDateTime.now();
        EquipmentUsage entity = new EquipmentUsage();
        entity.setUsageId(nextUsageId());
        entity.setEquipmentAssetId(dto.getEquipmentAssetId());
        entity.setDormitoryId(dormitoryId);
        entity.setRoomId(roomId);
        entity.setEmployeeId(employeeId);
        entity.setUsageStartDate(startDate);
        entity.setUsageEndDate(endDate);
        entity.setUsageQuantity(quantity);
        entity.setRemarks(blankToNull(dto.getRemarks()));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        equipmentUsageMapper.insert(entity);

        operationLogWriter.logCreate(
                OperationTypeEnum.EQUIPMENT_USAGE_CREATE, TargetTableEnum.EQUIPMENT_USAGE,
                entity.getUsageId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public EquipmentUsage release(String usageId, EquipmentUsageReleaseDTO dto) {
        EquipmentUsage before = equipmentUsageMapper.findById(usageId);
        if (before == null) {
            throw new BusinessException("EQUIPMENT_USAGE_NOT_FOUND", "備品利用が見つかりません");
        }
        if (before.getUsageEndDate() != null) {
            throw new BusinessException("EQUIPMENT_USAGE_ALREADY_RELEASED", "既に利用解除済みです");
        }

        LocalDate endDate = dto != null && dto.getUsageEndDate() != null
                ? dto.getUsageEndDate()
                : LocalDate.now();
        if (endDate.isBefore(before.getUsageStartDate())) {
            throw new BusinessException("INVALID_USAGE_DATE_RANGE", "利用終了日は利用開始日以降を指定してください");
        }

        LocalDateTime now = LocalDateTime.now();
        EquipmentUsage update = new EquipmentUsage();
        update.setUsageId(usageId);
        update.setUsageEndDate(endDate);
        update.setUpdatedAt(now);
        equipmentUsageMapper.updateRelease(update);

        EquipmentUsage after = equipmentUsageMapper.findById(usageId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.EQUIPMENT_USAGE_RELEASE, TargetTableEnum.EQUIPMENT_USAGE,
                usageId, before, after);
        return after;
    }

    private void assertWithinPurchaseQuantity(String equipmentAssetId, int additionalQuantity,
                                              String excludeUsageId, Integer purchaseQuantity) {
        int limit = purchaseQuantity == null ? 1 : purchaseQuantity;
        Integer used = equipmentUsageMapper.sumActiveUsageQuantity(equipmentAssetId, excludeUsageId);
        int activeUsed = used == null ? 0 : used;
        if (activeUsed + additionalQuantity > limit) {
            throw new BusinessException("EQUIPMENT_USAGE_QUANTITY_EXCEEDED",
                    "利用個数が購入数量を超えています（購入数量: " + limit + "、利用中: " + activeUsed + "）");
        }
    }

    private String nextUsageId() {
        String id = IdGenerator.nextId("EU");
        if (id.length() != USAGE_ID_LENGTH) {
            throw new BusinessException("EQUIPMENT_USAGE_ID_GENERATION_FAILED", "利用IDの採番に失敗しました");
        }
        return id;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
