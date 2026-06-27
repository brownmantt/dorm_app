package com.dom.project.mapper;

import com.dom.project.entity.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地域マスタ Mapper。
 */
@Mapper
public interface RegionMapper {

    int insert(Region region);

    int update(Region region);

    int logicalDelete(@Param("regionId") String regionId, @Param("deletedAt") LocalDateTime deletedAt);

    Region findById(@Param("regionId") String regionId);

    Region findByCode(@Param("code") String code);

    List<Region> searchList(@Param("code") String code,
                            @Param("name") String name,
                            @Param("offset") Integer offset,
                            @Param("limit") Integer limit);

    Long countSearch(@Param("code") String code,
                     @Param("name") String name);

    Long countDormitoriesByRegionCode(@Param("code") String code);
}
