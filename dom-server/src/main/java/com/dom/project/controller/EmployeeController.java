package com.dom.project.controller;

import com.dom.project.entity.Employee;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EmployeeSaveDTO;
import com.dom.project.entity.view.EmployeeListView;
import com.dom.project.entity.view.FirstUseDateView;
import com.dom.project.entity.vo.TotalUsageDaysVO;
import com.dom.project.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社員 API コントローラ。
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 社員一覧（マスタ画面・検索選択兼用）。
     */
    @GetMapping
    public PageResult<EmployeeListView> list(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String gender,
                                             @RequestParam(required = false) String employeeCategory,
                                             @RequestParam(required = false) String affiliationId,
                                             @RequestParam(required = false) Boolean notResidingOnly,
                                             @RequestParam(required = false) Boolean dormFeeComboSort,
                                             @RequestParam(required = false) String targetYearMonth,
                                             @RequestParam(required = false) String dormitoryId,
                                             @RequestParam(required = false) String roomId,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        return employeeService.list(keyword, gender, employeeCategory, affiliationId, notResidingOnly,
                dormFeeComboSort, targetYearMonth, dormitoryId, roomId, page, size);
    }

    /**
     * 社員詳細取得。
     */
    @GetMapping("/{empId}")
    public Employee getById(@PathVariable String empId) {
        return employeeService.getById(empId);
    }

    /**
     * 社員新規登録。
     */
    @PostMapping
    public Employee create(@Valid @RequestBody EmployeeSaveDTO dto) {
        return employeeService.create(dto);
    }

    /**
     * 社員更新。
     */
    @PutMapping("/{empId}")
    public Employee update(@PathVariable String empId, @Valid @RequestBody EmployeeSaveDTO dto) {
        return employeeService.update(empId, dto);
    }

    /**
     * 社員論理削除。
     */
    @DeleteMapping("/{empId}")
    public void delete(@PathVariable String empId) {
        employeeService.delete(empId);
    }

    /**
     * 初回利用日取得。
     */
    @GetMapping("/{empId}/first-use-date")
    public FirstUseDateView getFirstUseDate(@PathVariable String empId) {
        return employeeService.getFirstUseDate(empId);
    }

    /**
     * 通算利用日数取得。
     */
    @GetMapping("/{empId}/total-usage-days")
    public TotalUsageDaysVO getTotalUsageDays(@PathVariable String empId) {
        return employeeService.getTotalUsageDays(empId);
    }
}
