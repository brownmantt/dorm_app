package com.dom.project.controller;

import com.dom.project.entity.Region;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.RegionSaveDTO;
import com.dom.project.service.RegionService;
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
 * 地域マスタ API コントローラ。
 */
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public PageResult<Region> list(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) {
        return regionService.list(name, page, size);
    }

    @PostMapping
    public Region create(@Valid @RequestBody RegionSaveDTO dto) {
        return regionService.create(dto);
    }

    @PutMapping("/{id}")
    public Region update(@PathVariable("id") String id, @Valid @RequestBody RegionSaveDTO dto) {
        return regionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        regionService.delete(id);
    }
}
