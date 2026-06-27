package com.dom.project.service;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.vo.DormAllocationCalendarVO;
import com.dom.project.entity.vo.DormAllocationPrintVO;
import com.dom.project.entity.vo.MoveOutWarningVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 寮割カレンダーサービス。
 */
public interface DormAllocationService {

    DormAllocationCalendarVO buildCalendar(String yearMonth, List<String> regions, String dormitoryId, String genderType, String name);

    DormAllocationPrintVO buildPrint(String yearMonth, List<String> regions, String dormitoryId, String genderType, String name);

    PageResult<MoveOutWarningVO> listMoveOutWarnings(LocalDate asOfDate, Integer page, Integer size);
}
