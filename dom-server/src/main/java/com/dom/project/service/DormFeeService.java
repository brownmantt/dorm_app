package com.dom.project.service;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.DormFeeCalculateDTO;
import com.dom.project.entity.view.DormFeeListView;
import com.dom.project.entity.vo.DormFeeCalculateVO;

/**
 * 寮費業務サービスインターフェース。
 */
public interface DormFeeService {

    PageResult<DormFeeListView> list(String employeeId, String targetYearMonth, String status,
                                     Integer page, Integer size);

    DormFeeCalculateVO calculate(DormFeeCalculateDTO dto);
}
