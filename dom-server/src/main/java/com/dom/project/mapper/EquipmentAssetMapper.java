package com.dom.project.mapper;

import com.dom.project.entity.EquipmentAsset;
import com.dom.project.entity.view.EquipmentAssetListView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 備品（個体）Mapper。
 */
@Mapper
public interface EquipmentAssetMapper {

    void insert(EquipmentAsset entity);

    void update(EquipmentAsset entity);

    void logicalDelete(@Param("equipmentAssetId") String equipmentAssetId,
                       @Param("deletedAt") LocalDateTime deletedAt);

    EquipmentAsset findById(@Param("equipmentAssetId") String equipmentAssetId);

    List<EquipmentAssetListView> searchList(@Param("equipmentId") String equipmentId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    Long countSearch(@Param("equipmentId") String equipmentId);
}
