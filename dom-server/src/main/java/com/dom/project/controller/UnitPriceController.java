package com.dom.project.controller;

import com.dom.project.entity.UnitPrice;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UnitPriceSaveDTO;
import com.dom.project.entity.view.UnitPriceListView;
import com.dom.project.service.UnitPriceService;
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
 * 単価マスタ API コントローラ。
 */
@RestController
@RequestMapping("/unit-prices")
public class UnitPriceController {

    private final UnitPriceService unitPriceService;

    public UnitPriceController(UnitPriceService unitPriceService) {
        this.unitPriceService = unitPriceService;
    }

    @GetMapping
    public PageResult<UnitPriceListView> list(@RequestParam(required = false) String code,
                                              @RequestParam(required = false) String region,
                                              @RequestParam(required = false) String dormitoryId,
                                              @RequestParam(required = false) String usageTypeCode,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size) {
        return unitPriceService.list(code, region, dormitoryId, usageTypeCode, page, size);
    }

    @PostMapping
    public UnitPrice create(@Valid @RequestBody UnitPriceSaveDTO dto) {
        return unitPriceService.create(dto);
    }

    @PutMapping("/{id}")
    public UnitPrice update(@PathVariable("id") String id, @Valid @RequestBody UnitPriceSaveDTO dto) {
        return unitPriceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        unitPriceService.delete(id);
    }
}
