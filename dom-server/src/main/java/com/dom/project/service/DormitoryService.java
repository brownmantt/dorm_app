package com.dom.project.service;

import com.dom.project.entity.Dormitory;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormitoryManagerSaveDTO;
import com.dom.project.entity.dto.DormitorySaveDTO;
import com.dom.project.entity.dto.RoomSaveDTO;
import com.dom.project.entity.view.RoomListView;
import com.dom.project.entity.vo.DormitoryManagerVO;

/**
 * 寮・部屋業務サービスインターフェース。
 */
public interface DormitoryService {

    PageResult<Dormitory> list(String dormitoryId, String name, String genderType, String region, String address,
                               Boolean dormFeeComboSort, Integer page, Integer size);

    Dormitory getById(String id);

    Dormitory create(DormitorySaveDTO dto);

    Dormitory update(String id, DormitorySaveDTO dto);

    void delete(String id);

    DormitoryManagerVO getManager(String dormId);

    /** 未設定時に最初の入居者を責任者として自動登録する */
    void ensureAutoManagerAssigned(String dormId);

    DormitoryManagerVO assignManager(String dormId, DormitoryManagerSaveDTO dto);

    void removeManager(String dormId);

    PageResult<RoomListView> listRooms(String dormId, Integer page, Integer size);

    void createRoom(RoomSaveDTO dto);

    void updateRoom(String id, RoomSaveDTO dto);

    void deleteRoom(String id);
}
