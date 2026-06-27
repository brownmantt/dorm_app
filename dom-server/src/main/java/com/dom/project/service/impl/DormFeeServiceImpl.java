package com.dom.project.service.impl;

import com.dom.project.entity.DormFee;
import com.dom.project.entity.FeeRateConfig;
import com.dom.project.entity.Room;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormFeeCalculateDTO;
import com.dom.project.entity.dto.DormFeeSaveDTO;
import com.dom.project.entity.vo.DormFeeBasisVO;
import com.dom.project.entity.vo.DormFeeCalculateVO;
import com.dom.project.enum_.DormFeeStatusEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormFeeMapper;
import com.dom.project.mapper.FeeRateConfigMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.service.DormFeeService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.JsonUtils;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

/**
 * 寮費サービス実装。
 */
@Service
public class DormFeeServiceImpl implements DormFeeService {

    private static final int MONTH_DAYS = 30;

    private final DormFeeMapper dormFeeMapper;
    private final RoomMapper roomMapper;
    private final FeeRateConfigMapper feeRateConfigMapper;
    private final OperationLogWriter operationLogWriter;

    public DormFeeServiceImpl(DormFeeMapper dormFeeMapper, RoomMapper roomMapper,
                                FeeRateConfigMapper feeRateConfigMapper,
                                OperationLogWriter operationLogWriter) {
        this.dormFeeMapper = dormFeeMapper;
        this.roomMapper = roomMapper;
        this.feeRateConfigMapper = feeRateConfigMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<DormFee> list(String employeeId, String targetYearMonth, String status,
                                    Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<DormFee> list = dormFeeMapper.searchList(employeeId, targetYearMonth, status, offset, limit);
        Long total = dormFeeMapper.countSearch(employeeId, targetYearMonth, status);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public DormFeeCalculateVO calculate(DormFeeCalculateDTO dto) {
        Room room = roomMapper.findById(dto.getRoomId());
        if (room == null) {
            throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
        }
        FeeRateConfig rate = feeRateConfigMapper.findLatestByRoomType(room.getRoomType());
        if (rate == null) {
            throw new BusinessException("FEE_RATE_NOT_FOUND", "寮費単価が設定されていません");
        }

        YearMonth ym = YearMonth.parse(dto.getTargetYearMonth());
        LocalDate periodStart = ym.atDay(1);
        LocalDate periodEnd = ym.atEndOfMonth();
        LocalDate moveIn = LocalDate.parse(dto.getMoveInDate());
        LocalDate moveOut = dto.getMoveOutDate() != null && !dto.getMoveOutDate().isBlank()
                ? LocalDate.parse(dto.getMoveOutDate()) : periodEnd;

        LocalDate billStart = moveIn.isAfter(periodStart) ? moveIn : periodStart;
        LocalDate billEnd = moveOut.isBefore(periodEnd) ? moveOut : periodEnd;
        if (billEnd.isBefore(billStart)) {
            throw new BusinessException("INVALID_PERIOD", "算定期間が不正です");
        }

        int billableDays = Math.toIntExact(billEnd.toEpochDay() - billStart.toEpochDay() + 1);
        BigDecimal dailyRate = rate.getUnitRatePerSqm()
                .divide(BigDecimal.valueOf(MONTH_DAYS), 2, RoundingMode.HALF_UP);
        BigDecimal amount = room.getAreaSqm()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(billableDays))
                .setScale(0, RoundingMode.HALF_UP);

        DormFeeBasisVO basis = new DormFeeBasisVO();
        basis.setRoomAreaSqm(room.getAreaSqm());
        basis.setRoomType(room.getRoomType());
        basis.setBillableDays(billableDays);
        basis.setDailyRate(dailyRate);
        basis.setFormula("面積 × 日額 × 請求日数");

        DormFeeCalculateVO vo = new DormFeeCalculateVO();
        vo.setAmount(amount);
        vo.setBasis(basis);
        return vo;
    }

    @Override
    @Transactional
    public void create(DormFeeSaveDTO dto) {
        Room room = roomMapper.findById(dto.getRoomId());
        if (room == null) {
            throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
        }
        LocalDateTime now = LocalDateTime.now();
        DormFee entity = new DormFee();
        entity.setDormFeeId(IdGenerator.nextId("DF"));
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setRoomId(dto.getRoomId());
        entity.setTargetYearMonth(dto.getTargetYearMonth());
        entity.setAmount(dto.getAmount());
        entity.setBasisAreaSqm(room.getAreaSqm());
        entity.setBasisDays(dto.getBasisDetail() != null ? dto.getBasisDetail().getBillableDays() : 0);
        entity.setBasisDetail(JsonUtils.toJson(dto.getBasisDetail()));
        entity.setStatus(DormFeeStatusEnum.DRAFT.getCode());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        dormFeeMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.DORM_FEE_CREATE, TargetTableEnum.DORM_FEE, entity.getDormFeeId(), entity);
    }

    @Override
    @Transactional
    public void confirm(String id) {
        DormFee fee = dormFeeMapper.findById(id);
        if (fee == null) {
            throw new BusinessException("DORM_FEE_NOT_FOUND", "寮費が見つかりません");
        }
        if (!DormFeeStatusEnum.DRAFT.getCode().equals(fee.getStatus())) {
            throw new BusinessException("DORM_FEE_ALREADY_CONFIRMED", "既に確定済みです");
        }
        DormFee before = fee;
        int updated = dormFeeMapper.confirm(id, LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("DORM_FEE_CONFIRM_FAILED", "確定に失敗しました");
        }
        DormFee after = dormFeeMapper.findById(id);
        operationLogWriter.logUpdate(
                OperationTypeEnum.DORM_FEE_CONFIRM, TargetTableEnum.DORM_FEE, id, before, after);
    }
}
