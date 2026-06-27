package com.dom.project.mapper;

import com.dom.project.entity.view.EquipmentStorageView;
import com.dom.project.entity.EquipmentStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 備品保管 Mapper。
 */
@Mapper
public interface EquipmentStorageMapper {

    int insert(EquipmentStorage storage);

    List<EquipmentStorageView> searchList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    Long countSearch();
}
