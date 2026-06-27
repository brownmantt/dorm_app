package com.dom.project.service;

import com.dom.project.entity.DormFee;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormFeeCalculateDTO;
import com.dom.project.entity.dto.DormFeeSaveDTO;
import com.dom.project.entity.vo.DormFeeCalculateVO;

/**
 * 寮費業務サービスインターフェース。
 */
public interface DormFeeService {

    PageResult<DormFee> list(String employeeId, String targetYearMonth, String status, Integer page, Integer size);

    DormFeeCalculateVO calculate(DormFeeCalculateDTO dto);

    void create(DormFeeSaveDTO dto);

    void confirm(String id);
}
