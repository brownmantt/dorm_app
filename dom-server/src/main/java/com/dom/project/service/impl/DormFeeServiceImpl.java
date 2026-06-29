package com.dom.project.service.impl;

import com.dom.project.entity.DormFee;
import com.dom.project.entity.UnitPrice;
import com.dom.project.entity.UsageType;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormFeeCalculateDTO;
import com.dom.project.entity.view.DormFeeListView;
import com.dom.project.entity.view.DormFeeResidenceView;
import com.dom.project.entity.vo.DormFeeCalculateItemVO;
import com.dom.project.entity.vo.DormFeeCalculateVO;
import com.dom.project.enum_.DormFeeStatusEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormFeeMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.mapper.UnitPriceMapper;
import com.dom.project.mapper.UsageTypeMapper;
import com.dom.project.service.DormFeeService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 寮費サービス実装。
 */
@Service
public class DormFeeServiceImpl implements DormFeeService {

    private static final int MAX_USAGE_DAYS_UNLIMITED = -1;

    private final DormFeeMapper dormFeeMapper;
    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final UnitPriceMapper unitPriceMapper;
    private final UsageTypeMapper usageTypeMapper;
    private final OperationLogWriter operationLogWriter;

    public DormFeeServiceImpl(DormFeeMapper dormFeeMapper,
                              ResidenceHistoryMapper residenceHistoryMapper,
                              UnitPriceMapper unitPriceMapper,
                              UsageTypeMapper usageTypeMapper,
                              OperationLogWriter operationLogWriter) {
        this.dormFeeMapper = dormFeeMapper;
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.unitPriceMapper = unitPriceMapper;
        this.usageTypeMapper = usageTypeMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<DormFeeListView> list(String employeeId, String targetYearMonth, String status,
                                            Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<DormFeeListView> list = dormFeeMapper.searchList(employeeId, targetYearMonth, status, offset, limit);
        Long total = dormFeeMapper.countSearch(employeeId, targetYearMonth, status);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public DormFeeCalculateVO calculate(DormFeeCalculateDTO dto) {
        YearMonth ym = YearMonth.parse(dto.getTargetYearMonth());
        LocalDate periodStart = ym.atDay(1);
        LocalDate periodEnd = ym.atEndOfMonth();
        LocalDate filterMoveIn = parseOptionalDate(dto.getMoveInDate());
        LocalDate filterMoveOut = parseOptionalDate(dto.getMoveOutDate());

        List<DormFeeResidenceView> residences = residenceHistoryMapper.findForDormFeeCalculation(
                periodStart, periodEnd,
                blankToNull(dto.getEmployeeId()),
                blankToNull(dto.getDormitoryId()),
                blankToNull(dto.getRoomId()));

        if (residences == null || residences.isEmpty()) {
            throw new BusinessException("RESIDENCE_NOT_FOUND", "対象年月に該当する入居履歴がありません");
        }

        List<DormFeeCalculateItemVO> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        for (DormFeeResidenceView residence : residences) {
            CalculationContext ctx = buildCalculation(residence, periodStart, periodEnd,
                    filterMoveIn, filterMoveOut, dto.getTargetYearMonth());
            DormFee entity = toDormFeeEntity(ctx, now);

            DormFee existing = dormFeeMapper.findByResidenceAndMonth(
                    residence.getResidenceHistoryId(), dto.getTargetYearMonth());
            if (existing != null) {
                entity.setDormFeeId(existing.getDormFeeId());
                entity.setCreatedAt(existing.getCreatedAt());
                dormFeeMapper.update(entity);
                DormFee after = dormFeeMapper.findById(existing.getDormFeeId());
                operationLogWriter.logUpdate(
                        OperationTypeEnum.DORM_FEE_CREATE, TargetTableEnum.DORM_FEE,
                        existing.getDormFeeId(), existing, after);
            } else {
                entity.setDormFeeId(IdGenerator.nextId("DF"));
                entity.setCreatedAt(now);
                dormFeeMapper.insert(entity);
                operationLogWriter.logCreate(
                        OperationTypeEnum.DORM_FEE_CREATE, TargetTableEnum.DORM_FEE,
                        entity.getDormFeeId(), entity);
            }

            DormFeeCalculateItemVO item = toItemVO(residence, entity, ctx.errorMessage);
            items.add(item);
            if (entity.getAmount() != null) {
                totalAmount = totalAmount.add(entity.getAmount());
            }
        }

        DormFeeCalculateVO vo = new DormFeeCalculateVO();
        vo.setItems(items);
        vo.setAmount(totalAmount);
        return vo;
    }

    private CalculationContext buildCalculation(DormFeeResidenceView residence,
                                                LocalDate periodStart,
                                                LocalDate periodEnd,
                                                LocalDate filterMoveIn,
                                                LocalDate filterMoveOut,
                                                String targetYearMonth) {
        CalculationContext ctx = new CalculationContext();
        ctx.targetYearMonth = targetYearMonth;
        ctx.region = residence.getRegion();
        ctx.dormitoryId = residence.getDormitoryId();
        ctx.roomId = residence.getRoomId();
        ctx.employeeId = residence.getEmployeeId();
        ctx.residenceHistoryId = residence.getResidenceHistoryId();
        ctx.usageTypeCode = residence.getUsageTypeCode();
        ctx.moveInDate = residence.getMoveInDate();
        ctx.moveOutDate = residence.getMoveOutDate();

        if (!StringUtils.hasText(residence.getRegion())) {
            ctx.errorMessage = "寮の地域が未設定です";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.region = "UNKNOWN";
            ctx.billStart = residence.getMoveInDate().isAfter(periodStart) ? residence.getMoveInDate() : periodStart;
            ctx.billEnd = residence.getMoveOutDate() == null || residence.getMoveOutDate().isAfter(periodEnd)
                    ? periodEnd : residence.getMoveOutDate();
            return ctx;
        }
        if (!StringUtils.hasText(residence.getUsageTypeCode())) {
            ctx.errorMessage = "利用形態が未設定です";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.billStart = residence.getMoveInDate().isAfter(periodStart) ? residence.getMoveInDate() : periodStart;
            ctx.billEnd = residence.getMoveOutDate() == null || residence.getMoveOutDate().isAfter(periodEnd)
                    ? periodEnd : residence.getMoveOutDate();
            return ctx;
        }

        UnitPrice unitPrice = resolveUnitPrice(
                residence.getRegion(),
                residence.getDormitoryId(),
                residence.getRoomId(),
                residence.getUsageTypeCode());
        if (unitPrice == null) {
            ctx.errorMessage = "合致する単価マスタがありません";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.billStart = residence.getMoveInDate().isAfter(periodStart) ? residence.getMoveInDate() : periodStart;
            ctx.billEnd = residence.getMoveOutDate() == null || residence.getMoveOutDate().isAfter(periodEnd)
                    ? periodEnd : residence.getMoveOutDate();
            return ctx;
        }

        LocalDate billStart = residence.getMoveInDate().isAfter(periodStart)
                ? residence.getMoveInDate() : periodStart;
        LocalDate billEnd = residence.getMoveOutDate() == null || residence.getMoveOutDate().isAfter(periodEnd)
                ? periodEnd : residence.getMoveOutDate();

        if (filterMoveIn != null && filterMoveIn.isAfter(billStart)) {
            billStart = filterMoveIn;
        }
        if (filterMoveOut != null && filterMoveOut.isBefore(billEnd)) {
            billEnd = filterMoveOut;
        }
        if (billEnd.isBefore(billStart)) {
            ctx.errorMessage = "算定期間が不正です";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.billStart = billStart;
            ctx.billEnd = billEnd;
            return ctx;
        }

        int usageDays = Math.toIntExact(billEnd.toEpochDay() - billStart.toEpochDay() + 1);
        UsageType usageType = usageTypeMapper.findByCode(residence.getUsageTypeCode());
        Integer minDays = usageType != null ? usageType.getMinUsageDays() : null;
        Integer maxDays = usageType != null ? usageType.getMaxUsageDays() : null;
        if (minDays != null && usageDays < minDays) {
            ctx.errorMessage = "利用日数が最小利用日数（" + minDays + "日）未満です";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.billStart = billStart;
            ctx.billEnd = billEnd;
            ctx.usageDays = usageDays;
            return ctx;
        }
        if (maxDays != null && maxDays != MAX_USAGE_DAYS_UNLIMITED && usageDays > maxDays) {
            ctx.errorMessage = "利用日数が最大利用日数（" + maxDays + "日）を超過しています";
            ctx.status = DormFeeStatusEnum.ERROR.getCode();
            ctx.billStart = billStart;
            ctx.billEnd = billEnd;
            ctx.usageDays = usageDays;
            return ctx;
        }

        BigDecimal dailyRate = unitPrice.getDailyUnitPrice();
        BigDecimal amount = dailyRate
                .multiply(BigDecimal.valueOf(usageDays))
                .setScale(0, RoundingMode.HALF_UP);

        ctx.billStart = billStart;
        ctx.billEnd = billEnd;
        ctx.usageDays = usageDays;
        ctx.unitPrice = unitPrice;
        ctx.dailyUnitPrice = dailyRate;
        ctx.amount = amount;
        ctx.status = DormFeeStatusEnum.PROVISIONAL.getCode();
        return ctx;
    }

    private DormFee toDormFeeEntity(CalculationContext ctx, LocalDateTime now) {
        DormFee entity = new DormFee();
        entity.setRegion(StringUtils.hasText(ctx.region) ? ctx.region : "UNKNOWN");
        entity.setDormitoryId(ctx.dormitoryId);
        entity.setRoomId(ctx.roomId);
        entity.setEmployeeId(ctx.employeeId);
        entity.setTargetYearMonth(ctx.targetYearMonth);
        entity.setMoveInDate(ctx.billStart != null ? ctx.billStart : ctx.moveInDate);
        entity.setMoveOutDate(ctx.billEnd != null ? ctx.billEnd : ctx.moveOutDate);
        entity.setUsageTypeCode(StringUtils.hasText(ctx.usageTypeCode) ? ctx.usageTypeCode : "UNKNOWN");
        entity.setUsageDays(ctx.usageDays);
        if (ctx.unitPrice != null) {
            entity.setUnitPriceId(ctx.unitPrice.getUnitPriceId());
            entity.setDailyUnitPrice(ctx.dailyUnitPrice);
        }
        entity.setAmount(ctx.amount);
        entity.setResidenceHistoryId(ctx.residenceHistoryId);
        entity.setStatus(ctx.status);
        entity.setUpdatedAt(now);
        return entity;
    }

    private DormFeeCalculateItemVO toItemVO(DormFeeResidenceView residence,
                                              DormFee entity,
                                              String errorMessage) {
        DormFeeCalculateItemVO item = new DormFeeCalculateItemVO();
        item.setDormFeeId(entity.getDormFeeId());
        item.setResidenceHistoryId(entity.getResidenceHistoryId());
        item.setRegion(entity.getRegion());
        item.setDormitoryId(entity.getDormitoryId());
        item.setDormitoryName(residence.getDormitoryName());
        item.setRoomId(entity.getRoomId());
        item.setRoomName(residence.getRoomName());
        item.setEmployeeId(entity.getEmployeeId());
        item.setEmployeeName(residence.getEmployeeName());
        item.setTargetYearMonth(entity.getTargetYearMonth());
        item.setMoveInDate(entity.getMoveInDate());
        item.setMoveOutDate(entity.getMoveOutDate());
        item.setUsageTypeCode(entity.getUsageTypeCode());
        item.setUsageTypeName(residence.getUsageTypeName());
        item.setUsageDays(entity.getUsageDays());
        item.setUnitPriceId(entity.getUnitPriceId());
        item.setDailyUnitPrice(entity.getDailyUnitPrice());
        item.setAmount(entity.getAmount());
        item.setStatus(entity.getStatus());
        item.setErrorMessage(errorMessage);
        return item;
    }

    private LocalDate parseOptionalDate(String value) {
        return StringUtils.hasText(value) ? LocalDate.parse(value) : null;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }

    /**
     * 単価マスタを A→B→C の順で検索する。
     * A: 地域・利用形態・寮・部屋 / B: 地域・利用形態・寮 / C: 地域・利用形態
     */
    private UnitPrice resolveUnitPrice(String region, String dormitoryId, String roomId, String usageTypeCode) {
        if (!StringUtils.hasText(region) || !StringUtils.hasText(usageTypeCode)) {
            return null;
        }
        if (StringUtils.hasText(dormitoryId) && StringUtils.hasText(roomId)) {
            UnitPrice roomLevel = unitPriceMapper.findRoomLevelMatch(region, usageTypeCode, dormitoryId, roomId);
            if (roomLevel != null) {
                return roomLevel;
            }
        }
        if (StringUtils.hasText(dormitoryId)) {
            UnitPrice dormitoryLevel = unitPriceMapper.findDormitoryLevelMatch(region, usageTypeCode, dormitoryId);
            if (dormitoryLevel != null) {
                return dormitoryLevel;
            }
        }
        return unitPriceMapper.findRegionLevelMatch(region, usageTypeCode);
    }

    private static class CalculationContext {
        private String targetYearMonth;
        private String region;
        private String dormitoryId;
        private String roomId;
        private String employeeId;
        private String residenceHistoryId;
        private String usageTypeCode;
        private LocalDate moveInDate;
        private LocalDate moveOutDate;
        private LocalDate billStart;
        private LocalDate billEnd;
        private Integer usageDays;
        private UnitPrice unitPrice;
        private BigDecimal dailyUnitPrice;
        private BigDecimal amount;
        private String status = DormFeeStatusEnum.ERROR.getCode();
        private String errorMessage;
    }
}
