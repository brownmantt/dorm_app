package com.dom.project.service.impl;



import com.dom.project.constant.AppConstants;

import com.dom.project.entity.Dormitory;

import com.dom.project.entity.Room;

import com.dom.project.entity.common.PageResult;

import com.dom.project.entity.view.AllocationResidenceView;

import com.dom.project.entity.vo.DormAllocationBlockVO;

import com.dom.project.entity.vo.DormAllocationCalendarVO;

import com.dom.project.entity.vo.DormAllocationPrintVO;

import com.dom.project.entity.vo.DormAllocationRowVO;

import com.dom.project.entity.vo.MoveOutWarningVO;

import com.dom.project.mapper.DormitoryMapper;

import com.dom.project.mapper.DormitoryManagerMapper;

import com.dom.project.mapper.ResidenceHistoryMapper;

import com.dom.project.mapper.RoomMapper;

import com.dom.project.entity.DormitoryManager;

import com.dom.project.service.DormAllocationService;

import com.dom.project.service.DormitoryService;

import com.dom.project.util.DormAllocationHelper;

import com.dom.project.util.PageUtils;

import org.springframework.stereotype.Service;



import java.time.LocalDate;

import java.util.ArrayList;

import java.util.Collections;

import java.util.LinkedHashMap;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;



/**

 * 寮割カレンダーサービス実装。

 */

@Service

public class DormAllocationServiceImpl implements DormAllocationService {



    private final DormitoryMapper dormitoryMapper;

    private final ResidenceHistoryMapper residenceHistoryMapper;

    private final RoomMapper roomMapper;

    private final DormitoryService dormitoryService;

    private final DormitoryManagerMapper dormitoryManagerMapper;



    public DormAllocationServiceImpl(DormitoryMapper dormitoryMapper,

                                     ResidenceHistoryMapper residenceHistoryMapper,

                                     RoomMapper roomMapper,

                                     DormitoryService dormitoryService,

                                     DormitoryManagerMapper dormitoryManagerMapper) {

        this.dormitoryMapper = dormitoryMapper;

        this.residenceHistoryMapper = residenceHistoryMapper;

        this.roomMapper = roomMapper;

        this.dormitoryService = dormitoryService;

        this.dormitoryManagerMapper = dormitoryManagerMapper;

    }



    @Override

    public DormAllocationCalendarVO buildCalendar(String yearMonth, List<String> regions, String dormitoryId, String genderType, String name) {

        String normalizedYearMonth = yearMonth == null || yearMonth.isBlank()

                ? java.time.YearMonth.now().toString() : yearMonth;

        String nameFilter = name == null ? "" : name.trim();
        String dormitoryIdFilter = dormitoryId == null ? "" : dormitoryId.trim();
        String genderTypeFilter = genderType == null ? "" : genderType.trim();

        LocalDate monthStart = DormAllocationHelper.monthStart(normalizedYearMonth);

        LocalDate monthEnd = DormAllocationHelper.monthEnd(normalizedYearMonth);



        List<Dormitory> dormitories = dormitoryMapper.listForCalendar(regions, dormitoryIdFilter, genderTypeFilter);

        if (dormitories != null) {
            for (Dormitory dormitory : dormitories) {
                dormitoryService.ensureAutoManagerAssigned(dormitory.getDormitoryId());
            }
        }

        List<AllocationResidenceView> residences = residenceHistoryMapper.findForCalendar(monthStart, monthEnd, nameFilter);

        Map<String, List<AllocationResidenceView>> byDormitoryId = residences == null

                ? Collections.emptyMap()

                : residences.stream().collect(Collectors.groupingBy(AllocationResidenceView::getDormitoryId));



        List<DormAllocationBlockVO> blocks = new ArrayList<>();

        for (Dormitory dormitory : dormitories) {

            List<AllocationResidenceView> dormResidents = byDormitoryId.getOrDefault(

                    dormitory.getDormitoryId(), Collections.emptyList());

            if (dormResidents.isEmpty() && !nameFilter.isBlank()) {

                continue;

            }

            List<DormAllocationRowVO> rows = buildRoomRows(

                    dormitory.getDormitoryId(), dormResidents, normalizedYearMonth, nameFilter);

            DormAllocationBlockVO block = new DormAllocationBlockVO();

            block.setDormitoryId(dormitory.getDormitoryId());

            block.setDormitoryName(dormitory.getName());

            block.setAddress(dormitory.getAddress());

            block.setPostalCode(dormitory.getPostalCode());

            block.setRegion(dormitory.getRegion() == null || dormitory.getRegion().isBlank()

                    ? DormAllocationHelper.inferRegionFromAddress(dormitory.getAddress()) : dormitory.getRegion());

            block.setLayoutType(dormitory.getLayoutType());

            block.setGenderType(dormitory.getGenderType());

            Integer vacantCount = roomMapper.countVacantRoomsByDormitory(dormitory.getDormitoryId(), LocalDate.now());

            block.setHasVacancy(vacantCount != null && vacantCount > 0);

            block.setRows(rows);

            blocks.add(block);

        }



        DormAllocationCalendarVO calendar = new DormAllocationCalendarVO();

        calendar.setYearMonth(normalizedYearMonth);

        calendar.setDaysInMonth(DormAllocationHelper.daysInMonth(normalizedYearMonth));

        calendar.setFirstDayOfWeek(DormAllocationHelper.weekdayJa(

                DormAllocationHelper.monthStart(normalizedYearMonth).getDayOfWeek()));

        calendar.setWeekdayLabels(DormAllocationHelper.weekdayJaLabels(normalizedYearMonth));

        calendar.setBlocks(blocks);

        return calendar;

    }



    @Override

    public DormAllocationPrintVO buildPrint(String yearMonth, List<String> regions, String dormitoryId, String genderType, String name) {

        DormAllocationPrintVO vo = new DormAllocationPrintVO();

        vo.setCalendar(buildCalendar(yearMonth, regions, dormitoryId, genderType, name));

        return vo;

    }



    @Override

    public PageResult<MoveOutWarningVO> listMoveOutWarnings(LocalDate asOfDate, Integer page, Integer size) {

        LocalDate baseDate = asOfDate == null ? LocalDate.now() : asOfDate;

        int limit = PageUtils.limit(size);

        int offset = PageUtils.offset(page, limit);

        List<MoveOutWarningVO> list = residenceHistoryMapper.searchMoveOutWarnings(

                baseDate, AppConstants.MOVE_OUT_WARNING_DAYS, offset, limit);

        Long total = residenceHistoryMapper.countMoveOutWarnings(baseDate, AppConstants.MOVE_OUT_WARNING_DAYS);

        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);

    }



    private List<DormAllocationRowVO> buildRoomRows(String dormitoryId,

                                                    List<AllocationResidenceView> residents,

                                                    String yearMonth,

                                                    String nameFilter) {

        List<Room> rooms = roomMapper.findAllByDormitoryId(dormitoryId);

        if (rooms == null || rooms.isEmpty()) {

            return Collections.emptyList();

        }



        Map<String, List<AllocationResidenceView>> byRoomId = new LinkedHashMap<>();

        if (residents != null) {

            byRoomId = residents.stream().collect(Collectors.groupingBy(

                    AllocationResidenceView::getRoomId, LinkedHashMap::new, Collectors.toList()));

        }



        List<DormAllocationRowVO> rows = new ArrayList<>();

        Map<String, Integer> roomDayCount = new LinkedHashMap<>();
        Map<String, Integer> roomCapacityByRoomId = new LinkedHashMap<>();



        for (Room room : rooms) {

            List<AllocationResidenceView> roomResidents = byRoomId.getOrDefault(room.getRoomId(), Collections.emptyList());

            int capacity = resolveRoomCapacity(room);
            roomCapacityByRoomId.put(room.getRoomId(), capacity);

            if (roomResidents.isEmpty()) {

                if (!nameFilter.isBlank()) {

                    continue;

                }

                for (int slot = 0; slot < capacity; slot++) {

                    rows.add(buildVacantRoomRow(room));

                }

                continue;

            }

            for (AllocationResidenceView view : roomResidents) {

                DormAllocationRowVO row = mapResidentRow(view, room, yearMonth);

                accumulateRoomDayCount(row, roomDayCount);

                rows.add(row);

            }

            int vacantSlots = Math.max(0, capacity - roomResidents.size());

            for (int slot = 0; slot < vacantSlots; slot++) {

                rows.add(buildVacantRoomRow(room));

            }

        }



        for (DormAllocationRowVO row : rows) {

            row.setConflictDays(DormAllocationHelper.detectConflicts(

                    row.getOccupiedDays(), roomDayCount, resolveRoomConflictKey(row),
                    roomCapacityByRoomId.getOrDefault(row.getRoomId(), 1)));

        }

        applyManagerFlag(dormitoryId, rows);

        return rows;

    }



    private int resolveRoomCapacity(Room room) {

        Integer capacity = room.getCapacity();

        return capacity != null && capacity > 0 ? capacity : 1;

    }



    private DormAllocationRowVO buildVacantRoomRow(Room room) {

        DormAllocationRowVO row = new DormAllocationRowVO();

        row.setRoomId(room.getRoomId());

        row.setRoomName(room.getRoomName());

        row.setRoomDetail(room.getRoomDetail());

        row.setHasAirConditioner(room.getHasAirConditioner());

        row.setVacant(Boolean.TRUE);

        row.setAffiliationName("-");

        row.setOccupiedDays(Collections.emptyList());

        row.setConflictDays(Collections.emptyList());

        row.setIsManager(Boolean.FALSE);

        row.setMoveOutInMonth(Boolean.FALSE);

        row.setMoveOutWarning(Boolean.FALSE);

        return row;

    }



    private DormAllocationRowVO mapResidentRow(AllocationResidenceView view, Room room, String yearMonth) {

        DormAllocationRowVO row = new DormAllocationRowVO();

        row.setResidenceHistoryId(view.getResidenceHistoryId());

        row.setEmployeeId(view.getEmployeeId());

        row.setEmployeeName(view.getEmployeeName());

        row.setAffiliationName(view.getAffiliationName() == null ? "-" : view.getAffiliationName());

        row.setRoomId(room.getRoomId());

        row.setRoomName(room.getRoomName());

        row.setRoomDetail(room.getRoomDetail() != null ? room.getRoomDetail() : view.getRoomDetail());

        row.setHasAirConditioner(room.getHasAirConditioner() != null

                ? room.getHasAirConditioner() : view.getHasAirConditioner());

        row.setVacant(Boolean.FALSE);

        row.setIsManager(Boolean.TRUE.equals(view.getIsManager()));

        row.setMoveInDate(view.getMoveInDate());

        row.setMoveOutDate(view.getMoveOutDate());

        row.setMoveOutInMonth(DormAllocationHelper.moveOutInMonth(view.getMoveOutDate(), yearMonth));

        row.setMoveOutWarning(DormAllocationHelper.moveOutWarning(view.getMoveOutDate(), LocalDate.now()));

        List<Integer> occupiedDays = DormAllocationHelper.occupiedDays(

                view.getMoveInDate(), view.getMoveOutDate(), yearMonth);

        row.setOccupiedDays(occupiedDays);

        row.setConflictDays(Collections.emptyList());

        return row;

    }



    private void accumulateRoomDayCount(DormAllocationRowVO row, Map<String, Integer> roomDayCount) {

        String roomKey = resolveRoomConflictKey(row);

        if (row.getOccupiedDays() == null) {

            return;

        }

        for (Integer day : row.getOccupiedDays()) {

            String key = roomKey + ":" + day;

            roomDayCount.put(key, roomDayCount.getOrDefault(key, 0) + 1);

        }

    }



    private String resolveRoomConflictKey(DormAllocationRowVO row) {

        if (row.getRoomId() != null && !row.getRoomId().isBlank()) {

            return row.getRoomId();

        }

        if (row.getRoomName() != null && !row.getRoomName().isBlank()) {

            return row.getRoomName();

        }

        return "";

    }



    private void applyManagerFlag(String dormitoryId, List<DormAllocationRowVO> rows) {

        DormitoryManager manager = dormitoryManagerMapper.findByDormitoryId(dormitoryId);

        if (manager == null || rows == null) {

            return;

        }

        for (DormAllocationRowVO row : rows) {

            if (manager.getResidenceHistoryId() != null

                    && manager.getResidenceHistoryId().equals(row.getResidenceHistoryId())) {

                row.setIsManager(Boolean.TRUE);

            }

        }

    }

}


