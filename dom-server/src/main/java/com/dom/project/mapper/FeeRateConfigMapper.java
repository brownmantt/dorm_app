package com.dom.project.mapper;

import com.dom.project.entity.FeeRateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 寮費単価 Mapper。
 */
@Mapper
public interface FeeRateConfigMapper {

    FeeRateConfig findLatestByRoomType(@Param("roomType") String roomType);
}
