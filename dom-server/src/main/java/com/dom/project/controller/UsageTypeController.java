package com.dom.project.controller;

import com.dom.project.entity.UsageType;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UsageTypeSaveDTO;
import com.dom.project.service.UsageTypeService;
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
 * 利用形態マスタ API コントローラ。
 */
@RestController
@RequestMapping("/usage-types")
public class UsageTypeController {

    private final UsageTypeService usageTypeService;

    public UsageTypeController(UsageTypeService usageTypeService) {
        this.usageTypeService = usageTypeService;
    }

    @GetMapping
    public PageResult<UsageType> list(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size) {
        return usageTypeService.list(name, page, size);
    }

    @PostMapping
    public UsageType create(@Valid @RequestBody UsageTypeSaveDTO dto) {
        return usageTypeService.create(dto);
    }

    @PutMapping("/{id}")
    public UsageType update(@PathVariable("id") String id, @Valid @RequestBody UsageTypeSaveDTO dto) {
        return usageTypeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        usageTypeService.delete(id);
    }
}
