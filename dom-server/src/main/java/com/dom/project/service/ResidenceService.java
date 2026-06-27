package com.dom.project.service;

import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.CheckoutDTO;
import com.dom.project.entity.dto.ResidenceSaveDTO;
import com.dom.project.entity.view.ResidenceHistoryView;
import com.dom.project.entity.vo.ResidenceCreateVO;
import com.dom.project.entity.vo.ValidateVO;

import java.time.LocalDate;

/**
 * 入退寮業務サービスインターフェース。
 */
public interface ResidenceService {

    PageResult<ResidenceHistoryView> list(String name, String employeeId, String dormitoryName,
                                          LocalDate moveInDateFrom, LocalDate moveInDateTo,
                                          Integer page, Integer size);

    ValidateVO validate(ResidenceSaveDTO dto);

    ResidenceCreateVO create(ResidenceSaveDTO dto);

    void checkout(String id, CheckoutDTO dto);
}
