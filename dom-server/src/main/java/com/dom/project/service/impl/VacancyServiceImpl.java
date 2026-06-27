package com.dom.project.service.impl;

import com.dom.project.constant.AppConstants;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.view.AssignableRoomView;
import com.dom.project.entity.view.LongTermUsageAlertView;
import com.dom.project.entity.view.VacancyListView;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.service.VacancyService;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 空き室・長期利用サービス実装。
 */
@Service
public class VacancyServiceImpl implements VacancyService {

    private final RoomMapper roomMapper;
    private final ResidenceHistoryMapper residenceHistoryMapper;

    public VacancyServiceImpl(RoomMapper roomMapper, ResidenceHistoryMapper residenceHistoryMapper) {
        this.roomMapper = roomMapper;
        this.residenceHistoryMapper = residenceHistoryMapper;
    }

    @Override
    public PageResult<VacancyListView> listVacancies(String gender, LocalDate asOfDate, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<VacancyListView> list = roomMapper.searchVacancyList(gender, asOfDate, offset, limit);
        Long total = roomMapper.countVacancyList(gender, asOfDate);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public PageResult<AssignableRoomView> listAssignable(String employeeId, String dormitoryId,
                                                         LocalDate asOfDate, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<AssignableRoomView> list = roomMapper.findAssignableRooms(
                employeeId, dormitoryId, asOfDate, offset, limit);
        Long total = roomMapper.countAssignableRooms(employeeId, dormitoryId, asOfDate);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public PageResult<LongTermUsageAlertView> listLongTermAlerts(Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<LongTermUsageAlertView> list = residenceHistoryMapper.searchLongTermAlerts(
                AppConstants.LONG_TERM_USAGE_THRESHOLD_YEARS, offset, limit);
        Long total = residenceHistoryMapper.countLongTermAlerts(AppConstants.LONG_TERM_USAGE_THRESHOLD_YEARS);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }
}
