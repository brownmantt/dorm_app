package com.dom.project.mapper;

import com.dom.project.entity.ExcelImportError;
import com.dom.project.entity.ExcelImportJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Excel 取込 Mapper。
 */
@Mapper
public interface ExcelImportMapper {

    int insertJob(ExcelImportJob job);

    int updateJob(ExcelImportJob job);

    int insertError(ExcelImportError error);

    List<ExcelImportError> findErrorsByJobId(@Param("jobId") String jobId);
}
