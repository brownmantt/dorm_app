package com.dom.project.controller;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.view.AssignableRoomView;
import com.dom.project.entity.view.VacancyListView;
import com.dom.project.service.VacancyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 空き室 API コントローラ。
 */
@RestController
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    /**
     * 空き室一覧取得。
     */
    @GetMapping
    public PageResult<VacancyListView> list(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return vacancyService.listVacancies(gender, asOfDate, page, size);
    }

    /**
     * 割当可能部屋一覧取得。
     */
    @GetMapping("/assignable")
    public PageResult<AssignableRoomView> listAssignable(
            @RequestParam String employeeId,
            @RequestParam(required = false) String dormitoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return vacancyService.listAssignable(employeeId, dormitoryId, asOfDate, page, size);
    }
}
