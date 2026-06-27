package com.dom.project.mapper;

import com.dom.project.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 操作ログ Mapper。
 */
@Mapper
public interface OperationLogMapper {

    int insert(OperationLog log);

    List<OperationLog> searchList(@Param("operationType") String operationType,
                                  @Param("operatedBy") String operatedBy,
                                  @Param("operatedAtFrom") LocalDate operatedAtFrom,
                                  @Param("operatedAtTo") LocalDate operatedAtTo,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

    Long countSearch(@Param("operationType") String operationType,
                     @Param("operatedBy") String operatedBy,
                     @Param("operatedAtFrom") LocalDate operatedAtFrom,
                     @Param("operatedAtTo") LocalDate operatedAtTo);
}
