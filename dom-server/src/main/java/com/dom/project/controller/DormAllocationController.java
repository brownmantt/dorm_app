package com.dom.project.controller;

import com.dom.project.entity.vo.DormAllocationCalendarVO;
import com.dom.project.entity.vo.DormAllocationPrintVO;
import com.dom.project.service.DormAllocationService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 寮割カレンダー API コントローラ。
 */
@RestController
@RequestMapping("/dorm-allocation")
public class DormAllocationController {

    private final DormAllocationService dormAllocationService;

    public DormAllocationController(DormAllocationService dormAllocationService) {
        this.dormAllocationService = dormAllocationService;
    }

    @GetMapping
    public DormAllocationCalendarVO getCalendar(@RequestParam String yearMonth,
                                                @RequestParam(required = false) String regions,
                                                @RequestParam(required = false) String dormitoryId,
                                                @RequestParam(required = false) String genderType,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String sort) {
        return dormAllocationService.buildCalendar(yearMonth, parseRegions(regions), dormitoryId, genderType, name);
    }

    @GetMapping("/print")
    public DormAllocationPrintVO getPrint(@RequestParam String yearMonth,
                                          @RequestParam(required = false) String regions,
                                          @RequestParam(required = false) String dormitoryId,
                                          @RequestParam(required = false) String genderType,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String sort) {
        return dormAllocationService.buildPrint(yearMonth, parseRegions(regions), dormitoryId, genderType, name);
    }

    private List<String> parseRegions(String regions) {
        if (!StringUtils.hasText(regions)) {
            return Collections.emptyList();
        }
        return Arrays.stream(regions.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
