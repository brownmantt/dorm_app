package com.dom.project.controller;

import com.dom.project.entity.dto.RoomSaveDTO;
import com.dom.project.service.DormitoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部屋 API コントローラ。
 */
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final DormitoryService dormitoryService;

    public RoomController(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    /**
     * 部屋登録。
     */
    @PostMapping
    public void create(@Valid @RequestBody RoomSaveDTO dto) {
        dormitoryService.createRoom(dto);
    }

    /**
     * 部屋更新。
     */
    @PutMapping("/{id}")
    public void update(@PathVariable String id, @Valid @RequestBody RoomSaveDTO dto) {
        dormitoryService.updateRoom(id, dto);
    }

    /**
     * 部屋削除。
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        dormitoryService.deleteRoom(id);
    }
}
