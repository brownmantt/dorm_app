package com.dom.project.controller;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormFeeCalculateDTO;
import com.dom.project.entity.view.DormFeeListView;
import com.dom.project.entity.vo.DormFeeCalculateVO;
import com.dom.project.service.DormFeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 寮費 API コントローラ。
 */
@RestController
@RequestMapping("/dorm-fees")
public class DormFeeController {

    private final DormFeeService dormFeeService;

    public DormFeeController(DormFeeService dormFeeService) {
        this.dormFeeService = dormFeeService;
    }

    /**
     * 寮費一覧取得。
     */
    @GetMapping
    public PageResult<DormFeeListView> list(@RequestParam(required = false) String employeeId,
                                            @RequestParam(required = false) String targetYearMonth,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size) {
        return dormFeeService.list(employeeId, targetYearMonth, status, page, size);
    }

    /**
     * 寮費算定（入居履歴×単価マスタから算出し dorm_fee に保存）。
     */
    @PostMapping("/calculate")
    public DormFeeCalculateVO calculate(@Valid @RequestBody DormFeeCalculateDTO dto) {
        return dormFeeService.calculate(dto);
    }
}
