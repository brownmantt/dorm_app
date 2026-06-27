package com.dom.project.service.impl;

import com.dom.project.entity.Employee;
import com.dom.project.entity.EmployeeFirstDormUse;
import com.dom.project.entity.ResidenceHistory;
import com.dom.project.entity.Room;
import com.dom.project.entity.Dormitory;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.CheckoutDTO;
import com.dom.project.entity.dto.ResidenceSaveDTO;
import com.dom.project.entity.view.ResidenceHistoryView;
import com.dom.project.entity.vo.ResidenceCreateVO;
import com.dom.project.entity.vo.ValidateVO;
import com.dom.project.enum_.EmployeeCategoryEnum;
import com.dom.project.enum_.GenderEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormitoryMapper;
import com.dom.project.mapper.DormitoryManagerMapper;
import com.dom.project.mapper.EmployeeFirstDormUseMapper;
import com.dom.project.mapper.EmployeeMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.service.DormitoryService;
import com.dom.project.service.ResidenceService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * 入退寮サービス実装。
 */
@Service
public class ResidenceServiceImpl implements ResidenceService {

    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final EmployeeMapper employeeMapper;
    private final DormitoryMapper dormitoryMapper;
    private final RoomMapper roomMapper;
    private final EmployeeFirstDormUseMapper firstDormUseMapper;
    private final DormitoryManagerMapper dormitoryManagerMapper;
    private final DormitoryService dormitoryService;
    private final OperationLogWriter operationLogWriter;

    public ResidenceServiceImpl(ResidenceHistoryMapper residenceHistoryMapper,
                                EmployeeMapper employeeMapper,
                                DormitoryMapper dormitoryMapper,
                                RoomMapper roomMapper,
                                EmployeeFirstDormUseMapper firstDormUseMapper,
                                DormitoryManagerMapper dormitoryManagerMapper,
                                DormitoryService dormitoryService,
                                OperationLogWriter operationLogWriter) {
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.employeeMapper = employeeMapper;
        this.dormitoryMapper = dormitoryMapper;
        this.roomMapper = roomMapper;
        this.firstDormUseMapper = firstDormUseMapper;
        this.dormitoryManagerMapper = dormitoryManagerMapper;
        this.dormitoryService = dormitoryService;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<ResidenceHistoryView> list(String name, String employeeId, String dormitoryName,
                                                 LocalDate moveInDateFrom, LocalDate moveInDateTo,
                                                 Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<ResidenceHistoryView> list = residenceHistoryMapper.searchList(
                name, employeeId, dormitoryName, moveInDateFrom, moveInDateTo, offset, limit);
        Long total = residenceHistoryMapper.countSearch(name, employeeId, dormitoryName, moveInDateFrom, moveInDateTo);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public ValidateVO validate(ResidenceSaveDTO dto) {
        return doValidate(dto);
    }

    @Override
    @Transactional
    public ResidenceCreateVO create(ResidenceSaveDTO dto) {
        ValidateVO validation = doValidate(dto);
        if (Boolean.FALSE.equals(validation.getValid())) {
            throw new BusinessException("RESIDENCE_INVALID", validation.getMessage());
        }

        LocalDate moveInDate = LocalDate.parse(dto.getMoveInDate());
        LocalDateTime now = LocalDateTime.now();
        String historyId = IdGenerator.nextId("RH");

        ResidenceHistory history = new ResidenceHistory();
        history.setResidenceHistoryId(historyId);
        history.setEmployeeId(dto.getEmployeeId());
        history.setDormitoryId(dto.getDormitoryId());
        history.setRoomId(dto.getRoomId());
        history.setMoveInDate(moveInDate);
        history.setMoveOutDate(dto.getMoveOutDate());
        history.setMoveOutReason(dto.getMoveOutReason());
        history.setCreatedAt(now);
        history.setUpdatedAt(now);
        residenceHistoryMapper.insert(history);

        String firstUseDateStr = null;
        Employee employee = employeeMapper.findById(dto.getEmployeeId());
        if (employee != null && EmployeeCategoryEnum.JAPAN.getCode().equals(employee.getEmployeeCategory())) {
            EmployeeFirstDormUse existing = firstDormUseMapper.findByEmployeeId(dto.getEmployeeId());
            if (existing == null) {
                EmployeeFirstDormUse record = new EmployeeFirstDormUse();
                record.setEmployeeId(dto.getEmployeeId());
                record.setFirstUseDate(moveInDate);
                record.setCreatedAt(now);
                record.setUpdatedAt(now);
                firstDormUseMapper.insert(record);
                firstUseDateStr = moveInDate.toString();
            } else {
                firstUseDateStr = existing.getFirstUseDate().toString();
            }
        }

        operationLogWriter.logCreate(
                OperationTypeEnum.RESIDENCE_CREATE, TargetTableEnum.RESIDENCE_HISTORY, historyId, history);

        dormitoryService.ensureAutoManagerAssigned(dto.getDormitoryId());

        ResidenceCreateVO vo = new ResidenceCreateVO();
        vo.setResidenceHistoryId(historyId);
        vo.setFirstUseDate(firstUseDateStr);
        return vo;
    }

    @Override
    @Transactional
    public void checkout(String id, CheckoutDTO dto) {
        ResidenceHistory before = residenceHistoryMapper.findById(id);
        if (before == null) {
            throw new BusinessException("RESIDENCE_NOT_FOUND", "入居履歴が見つかりません");
        }
        LocalDate moveOutDate = LocalDate.parse(dto.getMoveOutDate());
        if (moveOutDate.isBefore(before.getMoveInDate())) {
            throw new BusinessException("INVALID_MOVE_OUT_DATE", "退寮日は入寮日以降である必要があります");
        }
        ResidenceHistory updateEntity = new ResidenceHistory();
        updateEntity.setResidenceHistoryId(id);
        updateEntity.setMoveOutDate(moveOutDate);
        updateEntity.setMoveOutReason(dto.getMoveOutReason());
        updateEntity.setUpdatedAt(LocalDateTime.now());
        residenceHistoryMapper.update(updateEntity);
        dormitoryManagerMapper.deleteByResidenceHistoryId(id);
        ResidenceHistory after = residenceHistoryMapper.findById(id);
        operationLogWriter.logUpdate(
                OperationTypeEnum.RESIDENCE_CHECKOUT, TargetTableEnum.RESIDENCE_HISTORY, id, before, after);
    }

    private ValidateVO doValidate(ResidenceSaveDTO dto) {
        Employee employee = employeeMapper.findById(dto.getEmployeeId());
        if (employee == null) {
            return ValidateVO.fail("社員が見つかりません");
        }
        Dormitory dormitory = dormitoryMapper.findById(dto.getDormitoryId());
        if (dormitory == null) {
            return ValidateVO.fail("寮が見つかりません");
        }
        Room room = roomMapper.findById(dto.getRoomId());
        if (room == null) {
            return ValidateVO.fail("部屋が見つかりません");
        }
        if (!room.getDormitoryId().equals(dto.getDormitoryId())) {
            return ValidateVO.fail("部屋は指定した寮に属していません");
        }

        String expectedGender = GenderEnum.MALE.getCode().equals(employee.getGender())
                ? GenderEnum.MALE.getCode() : GenderEnum.FEMALE.getCode();
        if (!expectedGender.equals(dormitory.getGenderType())) {
            return ValidateVO.fail("性別と寮種別が一致しません");
        }

        LocalDate moveInDate = LocalDate.parse(dto.getMoveInDate());
        int capacity = resolveRoomCapacity(room);
        List<ResidenceHistory> overlapping = residenceHistoryMapper.findOverlappingInPeriod(
                dto.getRoomId(), moveInDate, dto.getMoveOutDate(), null);
        int maxConcurrent = countMaxConcurrentResidents(overlapping, moveInDate, dto.getMoveOutDate());
        if (maxConcurrent >= capacity) {
            return ValidateVO.fail("部屋の定員を超えています");
        }

        return ValidateVO.ok();
    }

    private int resolveRoomCapacity(Room room) {
        Integer capacity = room.getCapacity();
        return capacity != null && capacity > 0 ? capacity : 1;
    }

    private int countMaxConcurrentResidents(List<ResidenceHistory> overlapping,
                                            LocalDate periodStart,
                                            LocalDate periodEnd) {
        if (overlapping == null || overlapping.isEmpty()) {
            return 0;
        }
        LocalDate effectiveEnd = periodEnd == null ? LocalDate.of(9999, 12, 31) : periodEnd;
        TreeSet<LocalDate> checkDates = new TreeSet<>();
        checkDates.add(periodStart);
        for (ResidenceHistory history : overlapping) {
            LocalDate moveInDate = history.getMoveInDate();
            if (moveInDate != null
                    && !moveInDate.isBefore(periodStart)
                    && !moveInDate.isAfter(effectiveEnd)) {
                checkDates.add(moveInDate);
            }
            LocalDate moveOutDate = history.getMoveOutDate();
            if (moveOutDate != null) {
                LocalDate dayAfterMoveOut = moveOutDate.plusDays(1);
                if (!dayAfterMoveOut.isBefore(periodStart) && !dayAfterMoveOut.isAfter(effectiveEnd)) {
                    checkDates.add(dayAfterMoveOut);
                }
            }
        }

        int maxConcurrent = 0;
        for (LocalDate checkDate : checkDates) {
            int activeCount = 0;
            for (ResidenceHistory history : overlapping) {
                if (isResidentActiveOn(history, checkDate)) {
                    activeCount++;
                }
            }
            maxConcurrent = Math.max(maxConcurrent, activeCount);
        }
        return maxConcurrent;
    }

    private boolean isResidentActiveOn(ResidenceHistory history, LocalDate date) {
        LocalDate moveInDate = history.getMoveInDate();
        if (moveInDate == null || date.isBefore(moveInDate)) {
            return false;
        }
        LocalDate moveOutDate = history.getMoveOutDate();
        return moveOutDate == null || !date.isAfter(moveOutDate);
    }
}
