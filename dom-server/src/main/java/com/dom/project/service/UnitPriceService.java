package com.dom.project.service;

import com.dom.project.entity.UnitPrice;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UnitPriceSaveDTO;
import com.dom.project.entity.view.UnitPriceListView;

/**
 * 単価マスタサービス。
 */
public interface UnitPriceService {

    PageResult<UnitPriceListView> list(String code, String region, String dormitoryId,
                                       String usageTypeCode, Integer page, Integer size);

    UnitPrice create(UnitPriceSaveDTO dto);

    UnitPrice update(String unitPriceId, UnitPriceSaveDTO dto);

    void delete(String unitPriceId);
}
