package com.dom.project.service;

import com.dom.project.entity.Employee;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EmployeeSaveDTO;
import com.dom.project.entity.view.EmployeeListView;
import com.dom.project.entity.view.FirstUseDateView;
import com.dom.project.entity.vo.TotalUsageDaysVO;

/**
 * 社員業務サービスインターフェース。
 */
public interface EmployeeService {

    PageResult<EmployeeListView> list(String keyword, String gender, String employeeCategory,
                                      String affiliationId, Boolean notResidingOnly,
                                      Boolean dormFeeComboSort,
                                      String targetYearMonth,
                                      String dormitoryId,
                                      String roomId,
                                      Integer page, Integer size);

    Employee getById(String employeeId);

    Employee create(EmployeeSaveDTO dto);

    Employee update(String employeeId, EmployeeSaveDTO dto);

    void delete(String employeeId);

    PageResult<Employee> search(String keyword, Integer page, Integer size);

    FirstUseDateView getFirstUseDate(String empId);

    TotalUsageDaysVO getTotalUsageDays(String empId);
}
