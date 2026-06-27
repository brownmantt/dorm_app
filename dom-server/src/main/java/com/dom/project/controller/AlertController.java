package com.dom.project.controller;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.view.LongTermUsageAlertView;
import com.dom.project.entity.vo.MoveOutWarningVO;
import com.dom.project.service.DormAllocationService;
import com.dom.project.service.VacancyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 警告 API コントローラ。
 */
@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final VacancyService vacancyService;
    private final DormAllocationService dormAllocationService;

    public AlertController(VacancyService vacancyService, DormAllocationService dormAllocationService) {
        this.vacancyService = vacancyService;
        this.dormAllocationService = dormAllocationService;
    }

    /**
     * 長期利用警告一覧取得。
     */
    @GetMapping("/long-term-usage")
    public PageResult<LongTermUsageAlertView> listLongTermUsage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return vacancyService.listLongTermAlerts(page, size);
    }

    /**
     * 退寮期限警告一覧取得。
     */
    @GetMapping("/move-out")
    public PageResult<MoveOutWarningVO> listMoveOutWarnings(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return dormAllocationService.listMoveOutWarnings(null, page, size);
    }
}
