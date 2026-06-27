package com.dom.project.mapper;

import com.dom.project.entity.Room;
import com.dom.project.entity.view.AssignableRoomView;
import com.dom.project.entity.view.RoomListView;
import com.dom.project.entity.view.VacancyListView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部屋マスタ Mapper。
 */
@Mapper
public interface RoomMapper {

    /** 部屋を新規追加 */
    int insert(Room room);

    /** 部屋を更新 */
    int update(Room room);

    /** 部屋の論理削除 */
    int logicalDelete(@Param("roomId") String roomId, @Param("deletedAt") LocalDateTime deletedAt);

    /** ID で検索（未削除） */
    Room findById(@Param("roomId") String roomId);

    /** 寮 ID で部屋一覧をページング検索（空き状況含む） */
    List<RoomListView> findByDormitoryId(@Param("dormitoryId") String dormitoryId,
                                         @Param("asOfDate") LocalDate asOfDate,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /** 寮 ID で全件取得 */
    List<Room> findAllByDormitoryId(@Param("dormitoryId") String dormitoryId);

    /** 寮 ID で部屋の総件数を検索 */
    Long countByDormitoryId(@Param("dormitoryId") String dormitoryId);

    /** 基準日時点で空きがある部屋数を検索 */
    Integer countVacantRoomsByDormitory(@Param("dormitoryId") String dormitoryId,
                                        @Param("asOfDate") LocalDate asOfDate);

    /** 空き室一覧のページング検索 */
    List<VacancyListView> searchVacancyList(@Param("gender") String gender,
                                            @Param("asOfDate") LocalDate asOfDate,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /** 空き室一覧の総件数 */
    Long countVacancyList(@Param("gender") String gender,
                          @Param("asOfDate") LocalDate asOfDate);

    /** 割当可能部屋一覧の検索 */
    List<AssignableRoomView> findAssignableRooms(@Param("employeeId") String employeeId,
                                                  @Param("dormitoryId") String dormitoryId,
                                                  @Param("asOfDate") LocalDate asOfDate,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);

    /** 割当可能部屋の総件数 */
    Long countAssignableRooms(@Param("employeeId") String employeeId,
                              @Param("dormitoryId") String dormitoryId,
                              @Param("asOfDate") LocalDate asOfDate);
}
