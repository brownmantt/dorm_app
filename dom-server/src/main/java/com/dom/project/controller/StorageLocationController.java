package com.dom.project.controller;

import com.dom.project.entity.StorageLocation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.StorageLocationSaveDTO;
import com.dom.project.service.StorageLocationService;
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
 * 保管場所マスタ API コントローラ。
 */
@RestController
@RequestMapping("/storage-locations")
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    public StorageLocationController(StorageLocationService storageLocationService) {
        this.storageLocationService = storageLocationService;
    }

    @GetMapping
    public PageResult<StorageLocation> list(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size) {
        return storageLocationService.list(name, page, size);
    }

    @PostMapping
    public StorageLocation create(@Valid @RequestBody StorageLocationSaveDTO dto) {
        return storageLocationService.create(dto);
    }

    @PutMapping("/{id}")
    public StorageLocation update(@PathVariable("id") String id,
                                  @Valid @RequestBody StorageLocationSaveDTO dto) {
        return storageLocationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        storageLocationService.delete(id);
    }
}
