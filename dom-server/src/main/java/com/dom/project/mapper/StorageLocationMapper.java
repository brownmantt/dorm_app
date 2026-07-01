package com.dom.project.mapper;

import com.dom.project.entity.StorageLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 保管場所マスタ Mapper。
 */
@Mapper
public interface StorageLocationMapper {

    int insert(StorageLocation storageLocation);

    int update(StorageLocation storageLocation);

    int logicalDelete(@Param("storageLocationId") String storageLocationId,
                      @Param("deletedAt") LocalDateTime deletedAt);

    StorageLocation findById(@Param("storageLocationId") String storageLocationId);

    StorageLocation findByName(@Param("name") String name);

    List<StorageLocation> searchList(@Param("name") String name,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    Long countSearch(@Param("name") String name);

    Long countEquipmentStoragesByLocationId(@Param("storageLocationId") String storageLocationId);
}
