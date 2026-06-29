package com.dom.project.mapper;

import com.dom.project.entity.Employee;
import com.dom.project.entity.view.EmployeeListView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社員 Mapper。
 */
@Mapper
public interface EmployeeMapper {

    /** 社員を新規追加 */
    int insert(Employee employee);

    /** 社員を更新 */
    int update(Employee employee);

    /** 社員の論理削除 */
    int logicalDelete(@Param("employeeId") String employeeId, @Param("deletedAt") LocalDateTime deletedAt);

    /** ID で検索（未削除） */
    Employee findById(@Param("employeeId") String employeeId);

    /** キーワードで社員をページング検索（ID または氏名） */
    List<Employee> searchByKeyword(@Param("keyword") String keyword,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    /** キーワード検索の総件数 */
    Long countByKeyword(@Param("keyword") String keyword);

    /** 所属 ID で社員一覧を検索 */
    List<Employee> findByAffiliationId(@Param("affiliationId") String affiliationId);

    /** 条件付き社員一覧（所属名 JOIN） */
    List<EmployeeListView> searchList(@Param("keyword") String keyword,
                                      @Param("gender") String gender,
                                      @Param("employeeCategory") String employeeCategory,
                                      @Param("affiliationId") String affiliationId,
                                      @Param("notResidingOnly") Boolean notResidingOnly,
                                      @Param("dormFeeComboSort") Boolean dormFeeComboSort,
                                      @Param("targetYearMonth") String targetYearMonth,
                                      @Param("dormitoryId") String dormitoryId,
                                      @Param("roomId") String roomId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    /** 条件付き社員一覧の総件数 */
    Long countSearch(@Param("keyword") String keyword,
                     @Param("gender") String gender,
                     @Param("employeeCategory") String employeeCategory,
                     @Param("affiliationId") String affiliationId,
                     @Param("notResidingOnly") Boolean notResidingOnly,
                     @Param("targetYearMonth") String targetYearMonth,
                     @Param("dormitoryId") String dormitoryId,
                     @Param("roomId") String roomId);
}
