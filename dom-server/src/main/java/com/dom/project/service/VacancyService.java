package com.dom.project.service;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.view.AssignableRoomView;
import com.dom.project.entity.view.LongTermUsageAlertView;
import com.dom.project.entity.view.VacancyListView;

import java.time.LocalDate;

/**
 * 空き室・長期利用業務サービスインターフェース。
 */
public interface VacancyService {

    PageResult<VacancyListView> listVacancies(String gender, LocalDate asOfDate, Integer page, Integer size);

    PageResult<AssignableRoomView> listAssignable(String employeeId, String dormitoryId,
                                                  LocalDate asOfDate, Integer page, Integer size);

    PageResult<LongTermUsageAlertView> listLongTermAlerts(Integer page, Integer size);
}
