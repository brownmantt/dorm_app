package com.dom.project.mapper;

import com.dom.project.entity.UsageType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 利用形態マスタ Mapper。
 */
@Mapper
public interface UsageTypeMapper {

    int insert(UsageType usageType);

    int update(UsageType usageType);

    int logicalDelete(@Param("usageTypeId") String usageTypeId, @Param("deletedAt") LocalDateTime deletedAt);

    UsageType findById(@Param("usageTypeId") String usageTypeId);

    UsageType findByCode(@Param("code") String code);

    List<UsageType> searchList(@Param("code") String code,
                               @Param("name") String name,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    Long countSearch(@Param("code") String code,
                     @Param("name") String name);
}
