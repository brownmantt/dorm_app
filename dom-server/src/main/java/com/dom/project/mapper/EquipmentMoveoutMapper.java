package com.dom.project.mapper;

import com.dom.project.entity.EquipmentMoveout;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退去備品処理 Mapper。
 */
@Mapper
public interface EquipmentMoveoutMapper {

    int insert(EquipmentMoveout moveout);
}
