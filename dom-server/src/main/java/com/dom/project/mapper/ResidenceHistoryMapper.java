package com.dom.project.mapper;

import com.dom.project.entity.ResidenceHistory;
import com.dom.project.entity.view.AllocationResidenceView;
import com.dom.project.entity.view.FirstUseDateView;
import com.dom.project.entity.view.LongTermUsageAlertView;
import com.dom.project.entity.view.DormFeeResidenceView;
import com.dom.project.entity.view.ResidenceHistoryView;
import com.dom.project.entity.vo.MoveOutWarningVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入居履歴 Mapper。
 */
@Mapper
public interface ResidenceHistoryMapper {

    int insert(ResidenceHistory history);

    int update(ResidenceHistory history);

    int updateCheckout(ResidenceHistory history);

    int logicalDelete(@Param("residenceHistoryId") String residenceHistoryId,
                      @Param("deletedAt") LocalDateTime deletedAt);

    ResidenceHistory findById(@Param("residenceHistoryId") String residenceHistoryId);

    List<ResidenceHistoryView> searchList(@Param("name") String name,
                                        @Param("employeeId") String employeeId,
                                        @Param("dormitoryName") String dormitoryName,
                                        @Param("moveInDateFrom") LocalDate moveInDateFrom,
                                        @Param("moveInDateTo") LocalDate moveInDateTo,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    Long countSearch(@Param("name") String name,
                     @Param("employeeId") String employeeId,
                     @Param("dormitoryName") String dormitoryName,
                     @Param("moveInDateFrom") LocalDate moveInDateFrom,
                     @Param("moveInDateTo") LocalDate moveInDateTo);

    Integer countActiveResidents(@Param("roomId") String roomId,
                                 @Param("asOfDate") LocalDate asOfDate);

    /** 寮内の現在有効入居者数（論理削除除外） */
    Integer countActiveResidentsByDormitoryId(@Param("dormitoryId") String dormitoryId,
                                              @Param("asOfDate") LocalDate asOfDate);

    Integer countOverlap(@Param("roomId") String roomId,
                         @Param("moveInDate") LocalDate moveInDate,
                         @Param("moveOutDate") LocalDate moveOutDate,
                         @Param("excludeId") String excludeId);

    /** 指定期間と重なる入居履歴（定員チェック用） */
    List<ResidenceHistory> findOverlappingInPeriod(@Param("roomId") String roomId,
                                                   @Param("moveInDate") LocalDate moveInDate,
                                                   @Param("moveOutDate") LocalDate moveOutDate,
                                                   @Param("excludeId") String excludeId);

    FirstUseDateView findFirstUseDate(@Param("employeeId") String employeeId);

    Integer countTotalUsageDays(@Param("employeeId") String employeeId);

    List<LongTermUsageAlertView> searchLongTermAlerts(@Param("thresholdYears") Integer thresholdYears,
                                                      @Param("offset") Integer offset,
                                                      @Param("limit") Integer limit);

    Long countLongTermAlerts(@Param("thresholdYears") Integer thresholdYears);

    List<AllocationResidenceView> findForCalendar(@Param("monthStart") LocalDate monthStart,
                                                  @Param("monthEnd") LocalDate monthEnd,
                                                  @Param("name") String name);

    List<MoveOutWarningVO> searchMoveOutWarnings(@Param("asOfDate") LocalDate asOfDate,
                                                 @Param("warningDays") Integer warningDays,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);

    Long countMoveOutWarnings(@Param("asOfDate") LocalDate asOfDate,
                              @Param("warningDays") Integer warningDays);

    /** 社員に紐づく入居履歴件数（論理削除除外） */
    Long countByEmployeeId(@Param("employeeId") String employeeId);

    /** 寮内で最も早い入寮履歴（全室・履歴全体） */
    ResidenceHistory findFirstByDormitoryId(@Param("dormitoryId") String dormitoryId);

    /** 寮内の現在有効入居者のうち最も早い入寮履歴（自動責任者候補） */
    ResidenceHistory findFirstActiveByDormitoryId(@Param("dormitoryId") String dormitoryId,
                                                   @Param("asOfDate") LocalDate asOfDate);

    /** 部屋内の指定社員の現在有効入居履歴 */
    ResidenceHistory findActiveByRoomAndEmployee(@Param("roomId") String roomId,
                                                 @Param("employeeId") String employeeId,
                                                 @Param("asOfDate") LocalDate asOfDate);

    /** 寮内の指定社員の現在有効入居履歴 */
    ResidenceHistory findActiveByDormitoryAndEmployee(@Param("dormitoryId") String dormitoryId,
                                                      @Param("employeeId") String employeeId,
                                                      @Param("asOfDate") LocalDate asOfDate);

    /** 指定社員の現在有効入居履歴（いずれかの寮） */
    ResidenceHistory findActiveByEmployee(@Param("employeeId") String employeeId,
                                          @Param("asOfDate") LocalDate asOfDate);

    /** 寮費算定対象の入居履歴（対象月と重なる期間） */
    List<DormFeeResidenceView> findForDormFeeCalculation(@Param("monthStart") LocalDate monthStart,
                                                         @Param("monthEnd") LocalDate monthEnd,
                                                         @Param("employeeId") String employeeId,
                                                         @Param("dormitoryId") String dormitoryId,
                                                         @Param("roomId") String roomId);
}
