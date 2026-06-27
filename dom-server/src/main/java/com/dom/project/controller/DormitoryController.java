package com.dom.project.controller;

import com.dom.project.entity.Dormitory;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormitoryManagerSaveDTO;
import com.dom.project.entity.dto.DormitorySaveDTO;
import com.dom.project.entity.view.RoomListView;
import com.dom.project.entity.vo.DormitoryManagerVO;
import com.dom.project.service.DormitoryService;
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
 * 寮 API コントローラ。
 */
@RestController
@RequestMapping("/dormitories")
public class DormitoryController {

    private final DormitoryService dormitoryService;

    public DormitoryController(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    /**
     * 寮一覧取得。
     */
    @GetMapping
    public PageResult<Dormitory> list(@RequestParam(required = false) String dormitoryId,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) String genderType,
                                      @RequestParam(required = false) String region,
                                      @RequestParam(required = false) String address,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size) {
        return dormitoryService.list(dormitoryId, name, genderType, region, address, page, size);
    }

    /**
     * 寮詳細取得。
     */
    @GetMapping("/{id}")
    public Dormitory getById(@PathVariable String id) {
        return dormitoryService.getById(id);
    }

    /**
     * 寮登録。
     */
    @PostMapping
    public Dormitory create(@Valid @RequestBody DormitorySaveDTO dto) {
        return dormitoryService.create(dto);
    }

    /**
     * 寮更新。
     */
    @PutMapping("/{id}")
    public Dormitory update(@PathVariable String id, @Valid @RequestBody DormitorySaveDTO dto) {
        return dormitoryService.update(id, dto);
    }

    /**
     * 寮削除。
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        dormitoryService.delete(id);
    }

    /**
     * 部屋一覧取得。
     */
    @GetMapping("/{dormId}/rooms")
    public PageResult<RoomListView> listRooms(@PathVariable String dormId,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size) {
        return dormitoryService.listRooms(dormId, page, size);
    }

    /**
     * 寮責任者取得。
     */
    @GetMapping("/{dormId}/manager")
    public DormitoryManagerVO getManager(@PathVariable String dormId) {
        return dormitoryService.getManager(dormId);
    }

    /**
     * 寮責任者設定。
     */
    @PutMapping("/{dormId}/manager")
    public DormitoryManagerVO assignManager(@PathVariable String dormId,
                                            @Valid @RequestBody DormitoryManagerSaveDTO dto) {
        return dormitoryService.assignManager(dormId, dto);
    }

    /**
     * 寮責任者解除。
     */
    @DeleteMapping("/{dormId}/manager")
    public void removeManager(@PathVariable String dormId) {
        dormitoryService.removeManager(dormId);
    }
}
