package com.dom.project.mapper;

import com.dom.project.entity.DormitoryManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 寮責任者 Mapper。
 */
@Mapper
public interface DormitoryManagerMapper {

    DormitoryManager findByDormitoryId(@Param("dormitoryId") String dormitoryId);

    int upsert(DormitoryManager dormitoryManager);

    int deleteByDormitoryId(@Param("dormitoryId") String dormitoryId);

    int deleteByResidenceHistoryId(@Param("residenceHistoryId") String residenceHistoryId);
}
