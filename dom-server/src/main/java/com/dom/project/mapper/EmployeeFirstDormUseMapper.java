package com.dom.project.mapper;

import com.dom.project.entity.EmployeeFirstDormUse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 初回利用日 Mapper。
 */
@Mapper
public interface EmployeeFirstDormUseMapper {

    EmployeeFirstDormUse findByEmployeeId(@Param("employeeId") String employeeId);

    int insert(EmployeeFirstDormUse record);
}
