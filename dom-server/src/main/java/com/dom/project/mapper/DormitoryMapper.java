package com.dom.project.mapper;

import com.dom.project.entity.Dormitory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 寮マスタ Mapper。
 */
@Mapper
public interface DormitoryMapper {

    /** 寮を新規追加 */
    int insert(Dormitory dormitory);

    /** 寮を更新 */
    int update(Dormitory dormitory);

    /** 寮の論理削除 */
    int logicalDelete(@Param("dormitoryId") String dormitoryId, @Param("deletedAt") LocalDateTime deletedAt);

    /** ID で検索（未削除） */
    Dormitory findById(@Param("dormitoryId") String dormitoryId);

    /** 条件付き寮一覧のページング検索 */
    List<Dormitory> searchList(@Param("dormitoryId") String dormitoryId,
                               @Param("name") String name,
                               @Param("genderType") String genderType,
                               @Param("region") String region,
                               @Param("address") String address,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    /** 条件検索の総件数 */
    Long countSearch(@Param("dormitoryId") String dormitoryId,
                     @Param("name") String name,
                     @Param("genderType") String genderType,
                     @Param("region") String region,
                     @Param("address") String address);

    /** カレンダー表示用の寮一覧 */
    List<Dormitory> listForCalendar(@Param("regions") List<String> regions,
                                    @Param("dormitoryId") String dormitoryId,
                                    @Param("genderType") String genderType);
}
