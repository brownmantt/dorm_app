package com.dom.project.mapper;

import com.dom.project.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 備品マスタ Mapper。
 */
@Mapper
public interface EquipmentMapper {

    int insert(Equipment equipment);

    int update(Equipment equipment);

    Equipment findById(@Param("equipmentId") String equipmentId);

    List<Equipment> searchList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    Long countSearch();
}
