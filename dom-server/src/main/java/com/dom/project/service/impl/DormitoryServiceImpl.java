package com.dom.project.service.impl;

import com.dom.project.entity.Dormitory;
import com.dom.project.entity.DormitoryManager;
import com.dom.project.entity.Employee;
import com.dom.project.entity.ResidenceHistory;
import com.dom.project.entity.Room;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormitoryManagerSaveDTO;
import com.dom.project.entity.dto.DormitorySaveDTO;
import com.dom.project.entity.dto.RoomSaveDTO;
import com.dom.project.entity.view.RoomListView;
import com.dom.project.entity.vo.DormitoryManagerVO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.DormitoryMapper;
import com.dom.project.mapper.DormitoryManagerMapper;
import com.dom.project.mapper.EmployeeMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.mapper.RoomMapper;
import com.dom.project.service.DormitoryService;
import com.dom.project.util.IdGenerator;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 寮・部屋サービス実装。
 */
@Service
public class DormitoryServiceImpl implements DormitoryService {

    private final DormitoryMapper dormitoryMapper;
    private final RoomMapper roomMapper;
    private final DormitoryManagerMapper dormitoryManagerMapper;
    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final EmployeeMapper employeeMapper;
    private final OperationLogWriter operationLogWriter;

    public DormitoryServiceImpl(DormitoryMapper dormitoryMapper,
                                RoomMapper roomMapper,
                                DormitoryManagerMapper dormitoryManagerMapper,
                                ResidenceHistoryMapper residenceHistoryMapper,
                                EmployeeMapper employeeMapper,
                                OperationLogWriter operationLogWriter) {
        this.dormitoryMapper = dormitoryMapper;
        this.roomMapper = roomMapper;
        this.dormitoryManagerMapper = dormitoryManagerMapper;
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.employeeMapper = employeeMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<Dormitory> list(String dormitoryId, String name, String genderType, String region, String address, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<Dormitory> list = dormitoryMapper.searchList(dormitoryId, name, genderType, region, address, offset, limit);
        Long total = dormitoryMapper.countSearch(dormitoryId, name, genderType, region, address);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public Dormitory getById(String id) {
        Dormitory dormitory = dormitoryMapper.findById(id);
        if (dormitory == null) {
            throw new BusinessException("DORM_NOT_FOUND", "寮が見つかりません");
        }
        return dormitory;
    }

    @Override
    @Transactional
    public Dormitory create(DormitorySaveDTO dto) {
        normalizeAndValidateSaveDto(dto);
        LocalDateTime now = LocalDateTime.now();
        Dormitory entity = toEntity(dto, IdGenerator.nextId("D"), now, now);
        dormitoryMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.DORMITORY_CREATE, TargetTableEnum.DORMITORY, entity.getDormitoryId(), entity);
        return entity;
    }

    @Override
    @Transactional
    public Dormitory update(String id, DormitorySaveDTO dto) {
        Dormitory before = getById(id);
        normalizeAndValidateSaveDto(dto);
        LocalDateTime now = LocalDateTime.now();
        Dormitory entity = toEntity(dto, id, before.getCreatedAt(), now);
        dormitoryMapper.update(entity);
        Dormitory after = dormitoryMapper.findById(id);
        operationLogWriter.logUpdate(
                OperationTypeEnum.DORMITORY_UPDATE, TargetTableEnum.DORMITORY, id, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Dormitory before = getById(id);
        assertNoActiveResidentsInDormitory(id);
        dormitoryMapper.logicalDelete(id, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.DORMITORY_DELETE, TargetTableEnum.DORMITORY, id, before);
    }

    @Override
    @Transactional
    public DormitoryManagerVO getManager(String dormId) {
        getById(dormId);
        boolean hadManager = dormitoryManagerMapper.findByDormitoryId(dormId) != null;
        ensureAutoManagerAssigned(dormId);
        DormitoryManager manager = dormitoryManagerMapper.findByDormitoryId(dormId);
        if (manager == null) {
            return null;
        }
        return toManagerVo(manager, !hadManager);
    }

    @Override
    @Transactional
    public void ensureAutoManagerAssigned(String dormId) {
        if (dormitoryManagerMapper.findByDormitoryId(dormId) != null) {
            return;
        }
        ResidenceHistory candidate = resolveAutoManagerCandidate(dormId);
        if (candidate == null) {
            return;
        }
        persistManager(dormId, candidate, true);
    }

    @Override
    @Transactional
    public DormitoryManagerVO assignManager(String dormId, DormitoryManagerSaveDTO dto) {
        getById(dormId);
        ResidenceHistory history = residenceHistoryMapper.findById(dto.getResidenceHistoryId());
        if (history == null) {
            throw new BusinessException("RESIDENCE_NOT_FOUND", "入居履歴が見つかりません");
        }
        if (!dormId.equals(history.getDormitoryId())) {
            throw new BusinessException("MANAGER_NOT_RESIDENT", "指定社員はこの寮の入居者ではありません");
        }
        LocalDate today = LocalDate.now();
        if (history.getMoveInDate() == null || history.getMoveInDate().isAfter(today)
                || (history.getMoveOutDate() != null && history.getMoveOutDate().isBefore(today))) {
            throw new BusinessException("MANAGER_NOT_RESIDENT", "指定社員はこの寮の現入居者ではありません");
        }
        String employeeId = dto.getEmployeeId();
        if (employeeId == null || employeeId.isBlank()) {
            employeeId = history.getEmployeeId();
        }
        if (!employeeId.equals(history.getEmployeeId())) {
            throw new BusinessException("MANAGER_NOT_RESIDENT", "指定社員はこの入居履歴の対象者と一致しません");
        }

        persistManager(dormId, history, false);
        DormitoryManager after = dormitoryManagerMapper.findByDormitoryId(dormId);
        return toManagerVo(after, false);
    }

    @Override
    @Transactional
    public void removeManager(String dormId) {
        getById(dormId);
        DormitoryManager before = dormitoryManagerMapper.findByDormitoryId(dormId);
        if (before == null) {
            return;
        }
        dormitoryManagerMapper.deleteByDormitoryId(dormId);
        operationLogWriter.logAction(
                OperationTypeEnum.DORMITORY_MANAGER_REMOVE, TargetTableEnum.DORMITORY_MANAGER, dormId, before, null);
    }

    @Override
    public PageResult<RoomListView> listRooms(String dormId, Integer page, Integer size) {
        getById(dormId);
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<RoomListView> list = roomMapper.findByDormitoryId(dormId, LocalDate.now(), offset, limit);
        Long total = roomMapper.countByDormitoryId(dormId);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    @Transactional
    public void createRoom(RoomSaveDTO dto) {
        getById(dto.getDormitoryId());
        LocalDateTime now = LocalDateTime.now();
        Room room = new Room();
        room.setRoomId(IdGenerator.nextId("R"));
        room.setDormitoryId(dto.getDormitoryId());
        room.setRoomName(dto.getRoomName());
        room.setRoomDetail(dto.getRoomDetail());
        room.setHasAirConditioner(dto.getHasAirConditioner());
        room.setMonthlyFee(dto.getMonthlyFee());
        room.setAreaSqm(dto.getAreaSqm());
        room.setCapacity(dto.getCapacity());
        room.setRoomType(dto.getRoomType());
        room.setCreatedAt(now);
        room.setUpdatedAt(now);
        roomMapper.insert(room);
        operationLogWriter.logCreate(
                OperationTypeEnum.ROOM_CREATE, TargetTableEnum.ROOM, room.getRoomId(), room);
    }

    @Override
    @Transactional
    public void updateRoom(String id, RoomSaveDTO dto) {
        Room before = roomMapper.findById(id);
        if (before == null) {
            throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
        }
        Room room = new Room();
        room.setRoomId(id);
        room.setDormitoryId(dto.getDormitoryId());
        room.setRoomName(dto.getRoomName());
        room.setRoomDetail(dto.getRoomDetail());
        room.setHasAirConditioner(dto.getHasAirConditioner());
        room.setMonthlyFee(dto.getMonthlyFee());
        room.setAreaSqm(dto.getAreaSqm());
        room.setCapacity(dto.getCapacity());
        room.setRoomType(dto.getRoomType());
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.update(room);
        Room after = roomMapper.findById(id);
        operationLogWriter.logUpdate(
                OperationTypeEnum.ROOM_UPDATE, TargetTableEnum.ROOM, id, before, after);
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        Room before = roomMapper.findById(id);
        if (before == null) {
            throw new BusinessException("ROOM_NOT_FOUND", "部屋が見つかりません");
        }
        assertNoActiveResidentsInRoom(id);
        roomMapper.logicalDelete(id, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.ROOM_DELETE, TargetTableEnum.ROOM, id, before);
    }

    private void assertNoActiveResidentsInDormitory(String dormitoryId) {
        Integer count = residenceHistoryMapper.countActiveResidentsByDormitoryId(dormitoryId, LocalDate.now());
        if (count != null && count > 0) {
            throw new BusinessException("DORMITORY_IN_USE", "入居者がいる寮は削除できません");
        }
    }

    private void assertNoActiveResidentsInRoom(String roomId) {
        Integer count = residenceHistoryMapper.countActiveResidents(roomId, LocalDate.now());
        if (count != null && count > 0) {
            throw new BusinessException("ROOM_IN_USE", "入居者がいる部屋は削除できません");
        }
    }

    private void normalizeAndValidateSaveDto(DormitorySaveDTO dto) {
        String postalCode = normalizePostalCode(dto.getPostalCode());
        if (postalCode.length() != 7) {
            throw new BusinessException("INVALID_POSTAL_CODE", "郵便番号は7桁の数字で入力してください");
        }
        dto.setPostalCode(postalCode);

        if (dto.getRegion() == null || dto.getRegion().isBlank()) {
            throw new BusinessException("INVALID_REGION", "地域を選択してください");
        }
        if (dto.getName() != null && dto.getName().length() > 100) {
            throw new BusinessException("INVALID_DORMITORY_NAME", "寮名称は100文字以内で入力してください");
        }
        if (dto.getAddress() != null && dto.getAddress().length() > 200) {
            throw new BusinessException("INVALID_ADDRESS", "住所は200文字以内で入力してください");
        }
        validateNearestStationLength(dto.getNearestStation1(), "最寄駅１は20文字以内で入力してください");
        validateNearestStationLength(dto.getNearestStation2(), "最寄駅２は20文字以内で入力してください");
        validateNearestStationLength(dto.getNearestStation3(), "最寄駅３は20文字以内で入力してください");
    }

    private void validateNearestStationLength(String value, String message) {
        if (value != null && value.length() > 20) {
            throw new BusinessException("INVALID_NEAREST_STATION", message);
        }
    }

    private String normalizePostalCode(String postalCode) {
        if (postalCode == null) {
            return "";
        }
        return postalCode.replaceAll("[^0-9]", "");
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Dormitory toEntity(DormitorySaveDTO dto, String dormitoryId,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        Dormitory entity = new Dormitory();
        entity.setDormitoryId(dormitoryId);
        entity.setName(dto.getName());
        entity.setRegion(dto.getRegion());
        entity.setPostalCode(dto.getPostalCode());
        entity.setAddress(dto.getAddress());
        entity.setLayoutType(dto.getLayoutType());
        entity.setGenderType(dto.getGenderType());
        entity.setNearestStation1(blankToNull(dto.getNearestStation1()));
        entity.setNearestStation2(blankToNull(dto.getNearestStation2()));
        entity.setNearestStation3(blankToNull(dto.getNearestStation3()));
        entity.setRemarks(dto.getRemarks());
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }

    private ResidenceHistory resolveAutoManagerCandidate(String dormId) {
        LocalDate today = LocalDate.now();
        ResidenceHistory firstActive = residenceHistoryMapper.findFirstActiveByDormitoryId(dormId, today);
        if (firstActive != null) {
            return firstActive;
        }
        ResidenceHistory firstEver = residenceHistoryMapper.findFirstByDormitoryId(dormId);
        if (firstEver == null) {
            return null;
        }
        if (isActiveResidence(firstEver, today)) {
            return firstEver;
        }
        return residenceHistoryMapper.findActiveByDormitoryAndEmployee(
                dormId, firstEver.getEmployeeId(), today);
    }

    private boolean isActiveResidence(ResidenceHistory history, LocalDate asOfDate) {
        if (history == null || history.getMoveInDate() == null) {
            return false;
        }
        if (history.getMoveInDate().isAfter(asOfDate)) {
            return false;
        }
        return history.getMoveOutDate() == null || !history.getMoveOutDate().isBefore(asOfDate);
    }

    private void persistManager(String dormId, ResidenceHistory history, boolean autoAssigned) {
        DormitoryManager before = dormitoryManagerMapper.findByDormitoryId(dormId);
        DormitoryManager manager = new DormitoryManager();
        manager.setDormitoryId(dormId);
        manager.setEmployeeId(history.getEmployeeId());
        manager.setResidenceHistoryId(history.getResidenceHistoryId());
        manager.setAssignedAt(LocalDateTime.now());
        manager.setVersion(before == null ? 0 : before.getVersion());
        dormitoryManagerMapper.upsert(manager);
        DormitoryManager after = dormitoryManagerMapper.findByDormitoryId(dormId);
        operationLogWriter.logAction(
                OperationTypeEnum.DORMITORY_MANAGER_ASSIGN, TargetTableEnum.DORMITORY_MANAGER, dormId, before, after);
    }

    private DormitoryManagerVO toManagerVo(DormitoryManager manager, Boolean autoAssigned) {
        Employee employee = employeeMapper.findById(manager.getEmployeeId());
        DormitoryManagerVO vo = new DormitoryManagerVO();
        vo.setEmployeeId(manager.getEmployeeId());
        vo.setResidenceHistoryId(manager.getResidenceHistoryId());
        vo.setEmployeeName(employee == null ? null : employee.getName());
        vo.setAutoAssigned(autoAssigned);
        return vo;
    }
}
