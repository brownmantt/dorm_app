package com.dom.project.mapper;

import com.dom.project.entity.DormFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 寮費 Mapper。
 */
@Mapper
public interface DormFeeMapper {

    int insert(DormFee dormFee);

    int update(DormFee dormFee);

    DormFee findById(@Param("dormFeeId") String dormFeeId);

    List<DormFee> searchList(@Param("employeeId") String employeeId,
                             @Param("targetYearMonth") String targetYearMonth,
                             @Param("status") String status,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    Long countSearch(@Param("employeeId") String employeeId,
                     @Param("targetYearMonth") String targetYearMonth,
                     @Param("status") String status);

    int confirm(@Param("dormFeeId") String dormFeeId, @Param("updatedAt") LocalDateTime updatedAt);
}
