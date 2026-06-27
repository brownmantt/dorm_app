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
}
