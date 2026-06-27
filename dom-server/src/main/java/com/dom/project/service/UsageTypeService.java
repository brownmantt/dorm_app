package com.dom.project.service;

import com.dom.project.entity.UsageType;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.UsageTypeSaveDTO;

/**
 * 利用形態マスタサービス。
 */
public interface UsageTypeService {

    PageResult<UsageType> list(String name, Integer page, Integer size);

    UsageType create(UsageTypeSaveDTO dto);

    UsageType update(String usageTypeId, UsageTypeSaveDTO dto);

    void delete(String usageTypeId);
}
