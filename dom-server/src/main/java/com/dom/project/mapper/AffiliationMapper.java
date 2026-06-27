package com.dom.project.mapper;

import com.dom.project.entity.Affiliation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 所属マスタ Mapper。
 */
@Mapper
public interface AffiliationMapper {

    int insert(Affiliation affiliation);

    int update(Affiliation affiliation);

    int logicalDelete(@Param("affiliationId") String affiliationId, @Param("deletedAt") LocalDateTime deletedAt);

    Affiliation findById(@Param("affiliationId") String affiliationId);

    Affiliation findByCode(@Param("code") String code);

    List<Affiliation> searchList(@Param("code") String code,
                                 @Param("name") String name,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    Long countSearch(@Param("code") String code,
                     @Param("name") String name);

    Long countEmployeesByAffiliationId(@Param("affiliationId") String affiliationId);
}
