package com.dom.project.mapper;

import com.dom.project.entity.EquipmentUsage;
import com.dom.project.entity.view.EquipmentUsageListView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 備品利用 Mapper。
 */
@Mapper
public interface EquipmentUsageMapper {

    void insert(EquipmentUsage entity);

    void updateRelease(EquipmentUsage entity);

    EquipmentUsage findById(@Param("usageId") String usageId);

    Integer sumActiveUsageQuantity(@Param("equipmentAssetId") String equipmentAssetId,
                                   @Param("excludeUsageId") String excludeUsageId);

    List<EquipmentUsageListView> searchList(@Param("equipmentAssetId") String equipmentAssetId,
                                            @Param("dormitoryId") String dormitoryId,
                                            @Param("roomId") String roomId,
                                            @Param("employeeId") String employeeId,
                                            @Param("activeOnly") Boolean activeOnly,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    Long countSearch(@Param("equipmentAssetId") String equipmentAssetId,
                     @Param("dormitoryId") String dormitoryId,
                     @Param("roomId") String roomId,
                     @Param("employeeId") String employeeId,
                     @Param("activeOnly") Boolean activeOnly);
}
