package com.dom.project.service.impl;

import com.dom.project.entity.Dormitory;
import com.dom.project.entity.Region;
import com.dom.project.entity.Room;
import com.dom.project.entity.UnitPrice;
import com.dom.project.entity.UsageType;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UnitPriceSaveDTO;
import com.dom.project.entity.view.UnitPriceListView;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormitoryMapper;
import com.dom.project.mapper.RegionMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.mapper.UnitPriceMapper;
import com.dom.project.mapper.UsageTypeMapper;
import com.dom.project.service.UnitPriceService;
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
 * 単価マスタサービス実装。
 */
@Service
public class UnitPriceServiceImpl implements UnitPriceService {

    private static final int MAX_USAGE_DAYS_UNLIMITED = -1;

    private final UnitPriceMapper unitPriceMapper;
    private final RegionMapper regionMapper;
    private final DormitoryMapper dormitoryMapper;
    private final RoomMapper roomMapper;
    private final UsageTypeMapper usageTypeMapper;
    private final OperationLogWriter operationLogWriter;

    public UnitPriceServiceImpl(UnitPriceMapper unitPriceMapper,
                                RegionMapper regionMapper,
                                DormitoryMapper dormitoryMapper,
                                RoomMapper roomMapper,
                                UsageTypeMapper usageTypeMapper,
                                OperationLogWriter operationLogWriter) {
        this.unitPriceMapper = unitPriceMapper;
        this.regionMapper = regionMapper;
        this.dormitoryMapper = dormitoryMapper;
        this.roomMapper = roomMapper;
        this.usageTypeMapper = usageTypeMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<UnitPriceListView> list(String code, String region, String dormitoryId,
                                              String usageTypeCode, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<UnitPriceListView> list = unitPriceMapper.searchList(code, region, dormitoryId, usageTypeCode, offset, limit);
        Long total = unitPriceMapper.countSearch(code, region, dormitoryId, usageTypeCode);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public UnitPrice create(UnitPriceSaveDTO dto) {
        validateReferences(dto);
        LocalDateTime now = LocalDateTime.now();
        UnitPrice entity = toEntity(dto);
        entity.setUnitPriceId(IdGenerator.nextId("UP"));
        entity.setCode(IdGenerator.nextId("UC"));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        unitPriceMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.UNIT_PRICE_CREATE, TargetTableEnum.UNIT_PRICE, entity.getUnitPriceId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public UnitPrice update(String unitPriceId, UnitPriceSaveDTO dto) {
        UnitPrice before = unitPriceMapper.findById(unitPriceId);
        if (before == null) {
            throw new BusinessException("UNIT_PRICE_NOT_FOUND", "単価が見つかりません");
        }
        validateReferences(dto);

        UnitPrice entity = toEntity(dto);
        entity.setUnitPriceId(unitPriceId);
        entity.setCode(before.getCode());
        entity.setUpdatedAt(LocalDateTime.now());
        unitPriceMapper.update(entity);

        UnitPrice after = unitPriceMapper.findById(unitPriceId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.UNIT_PRICE_UPDATE, TargetTableEnum.UNIT_PRICE, unitPriceId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String unitPriceId) {
        UnitPrice before = unitPriceMapper.findById(unitPriceId);
        if (before == null) {
            throw new BusinessException("UNIT_PRICE_NOT_FOUND", "単価が見つかりません");
        }
        unitPriceMapper.logicalDelete(unitPriceId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.UNIT_PRICE_DELETE, TargetTableEnum.UNIT_PRICE, unitPriceId, before);
    }

    private void validateReferences(UnitPriceSaveDTO dto) {
        Region region = regionMapper.findByCode(dto.getRegion());
        if (region == null) {
            throw new BusinessException("REGION_NOT_FOUND", "地域が見つかりません");
        }

        if (StringUtils.hasText(dto.getRoomId()) && !StringUtils.hasText(dto.getDormitoryId())) {
            throw new BusinessException("UNIT_PRICE_ROOM_REQUIRES_DORMITORY", "部屋を指定する場合は寮も選択してください");
        }

        if (StringUtils.hasText(dto.getDormitoryId())) {
            Dormitory dormitory = dormitoryMapper.findById(dto.getDormitoryId());
            if (dormitory == null) {
                throw new BusinessException("DORMITORY_NOT_FOUND", "寮が見つかりません");
            }
            if (StringUtils.hasText(dormitory.getRegion()) && !dormitory.getRegion().equals(dto.getRegion())) {
                throw new BusinessException("UNIT_PRICE_DORMITORY_REGION_MISMATCH", "選択した寮の地域と一致しません");
            }
        }

        if (StringUtils.hasText(dto.getRoomId())) {
            Room room = roomMapper.findById(dto.getRoomId());
            if (room == null) {
                throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
            }
            if (StringUtils.hasText(dto.getDormitoryId()) && !dto.getDormitoryId().equals(room.getDormitoryId())) {
                throw new BusinessException("UNIT_PRICE_ROOM_DORMITORY_MISMATCH", "選択した部屋は指定の寮に属していません");
            }
        }

        UsageType usageType = usageTypeMapper.findByCode(dto.getUsageTypeCode());
        if (usageType == null) {
            throw new BusinessException("USAGE_TYPE_NOT_FOUND", "利用形態が見つかりません");
        }
    }

    private UnitPrice toEntity(UnitPriceSaveDTO dto) {
        UnitPrice entity = new UnitPrice();
        entity.setRegion(dto.getRegion());
        entity.setDormitoryId(blankToNull(dto.getDormitoryId()));
        entity.setRoomId(blankToNull(dto.getRoomId()));
        entity.setUsageTypeCode(dto.getUsageTypeCode());
        entity.setDailyUnitPrice(dto.getDailyUnitPrice());
        entity.setMaxUsageDays(resolveMaxUsageDays(dto.getMaxUsageDays()));
        return entity;
    }

    private Integer resolveMaxUsageDays(Integer maxUsageDays) {
        if (maxUsageDays == null) {
            return MAX_USAGE_DAYS_UNLIMITED;
        }
        if (maxUsageDays < 1) {
            throw new BusinessException("UNIT_PRICE_MAX_DAYS_INVALID", "最大利用日数は1以上を入力してください");
        }
        return maxUsageDays;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
