package com.dom.project.mapper;

import com.dom.project.entity.EquipmentStorage;
import com.dom.project.entity.view.EquipmentStorageView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 備品保管 Mapper。
 */
@Mapper
public interface EquipmentStorageMapper {

    int insert(EquipmentStorage storage);

    int deleteByAssetId(@Param("equipmentAssetId") String equipmentAssetId);

    int deleteById(@Param("storageId") String storageId);

    EquipmentStorage findById(@Param("storageId") String storageId);

    List<EquipmentStorageView> findByAssetId(@Param("equipmentAssetId") String equipmentAssetId);

    List<EquipmentStorageView> searchList(@Param("equipmentAssetId") String equipmentAssetId,
                                          @Param("offset") Integer offset,
                                          @Param("limit") Integer limit);

    Long countSearch(@Param("equipmentAssetId") String equipmentAssetId);

    Long countByAssetId(@Param("equipmentAssetId") String equipmentAssetId);
}
