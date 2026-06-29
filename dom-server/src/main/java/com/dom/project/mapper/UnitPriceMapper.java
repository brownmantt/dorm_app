package com.dom.project.mapper;

import com.dom.project.entity.UnitPrice;
import com.dom.project.entity.view.UnitPriceListView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 単価マスタ Mapper。
 */
@Mapper
public interface UnitPriceMapper {

    int insert(UnitPrice unitPrice);

    int update(UnitPrice unitPrice);

    int logicalDelete(@Param("unitPriceId") String unitPriceId, @Param("deletedAt") LocalDateTime deletedAt);

    UnitPrice findById(@Param("unitPriceId") String unitPriceId);

    UnitPrice findByCode(@Param("code") String code);

    List<UnitPriceListView> searchList(@Param("code") String code,
                                       @Param("region") String region,
                                       @Param("dormitoryId") String dormitoryId,
                                       @Param("usageTypeCode") String usageTypeCode,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    Long countSearch(@Param("code") String code,
                     @Param("region") String region,
                     @Param("dormitoryId") String dormitoryId,
                     @Param("usageTypeCode") String usageTypeCode);

    /**
     * 単価マスタ一致検索（A: 地域・利用形態・寮・部屋）。
     */
    UnitPrice findRoomLevelMatch(@Param("region") String region,
                                 @Param("usageTypeCode") String usageTypeCode,
                                 @Param("dormitoryId") String dormitoryId,
                                 @Param("roomId") String roomId);

    /**
     * 単価マスタ一致検索（B: 地域・利用形態・寮。部屋は未指定行）。
     */
    UnitPrice findDormitoryLevelMatch(@Param("region") String region,
                                      @Param("usageTypeCode") String usageTypeCode,
                                      @Param("dormitoryId") String dormitoryId);

    /**
     * 単価マスタ一致検索（C: 地域・利用形態のみ。寮・部屋は未指定行）。
     */
    UnitPrice findRegionLevelMatch(@Param("region") String region,
                                   @Param("usageTypeCode") String usageTypeCode);
}
