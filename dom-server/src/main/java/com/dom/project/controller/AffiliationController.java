package com.dom.project.controller;

import com.dom.project.entity.Affiliation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.AffiliationSaveDTO;
import com.dom.project.service.AffiliationService;
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
 * 所属マスタ API コントローラ。
 */
@RestController
@RequestMapping("/affiliations")
public class AffiliationController {

    private final AffiliationService affiliationService;

    public AffiliationController(AffiliationService affiliationService) {
        this.affiliationService = affiliationService;
    }

    @GetMapping
    public PageResult<Affiliation> list(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size) {
        return affiliationService.list(name, page, size);
    }

    @PostMapping
    public Affiliation create(@Valid @RequestBody AffiliationSaveDTO dto) {
        return affiliationService.create(dto);
    }

    @PutMapping("/{id}")
    public Affiliation update(@PathVariable("id") String id, @Valid @RequestBody AffiliationSaveDTO dto) {
        return affiliationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        affiliationService.delete(id);
    }
}
