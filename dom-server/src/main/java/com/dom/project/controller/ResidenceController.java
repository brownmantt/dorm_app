package com.dom.project.controller;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.CheckoutDTO;
import com.dom.project.entity.dto.ResidenceSaveDTO;
import com.dom.project.entity.view.ResidenceHistoryView;
import com.dom.project.entity.vo.ResidenceCreateVO;
import com.dom.project.entity.vo.ValidateVO;
import com.dom.project.service.ResidenceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 入退寮 API コントローラ。
 */
@RestController
@RequestMapping("/residences")
public class ResidenceController {

    private final ResidenceService residenceService;

    public ResidenceController(ResidenceService residenceService) {
        this.residenceService = residenceService;
    }

    /**
     * 入居履歴一覧取得。
     */
    @GetMapping
    public PageResult<ResidenceHistoryView> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String dormitoryName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate moveInDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate moveInDateTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return residenceService.list(name, employeeId, dormitoryName, moveInDateFrom, moveInDateTo, page, size);
    }

    /**
     * 入居登録。
     */
    @PostMapping
    public ResidenceCreateVO create(@Valid @RequestBody ResidenceSaveDTO dto) {
        return residenceService.create(dto);
    }

    /**
     * 入居業務検証。
     */
    @PostMapping("/validate")
    public ValidateVO validate(@Valid @RequestBody ResidenceSaveDTO dto) {
        return residenceService.validate(dto);
    }

    /**
     * 退寮処理。
     */
    @PutMapping("/{id}/checkout")
    public void checkout(@PathVariable String id, @Valid @RequestBody CheckoutDTO dto) {
        residenceService.checkout(id, dto);
    }
}
