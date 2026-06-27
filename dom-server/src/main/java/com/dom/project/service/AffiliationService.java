package com.dom.project.service;

import com.dom.project.entity.Affiliation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.AffiliationSaveDTO;

/**
 * 所属マスタサービス。
 */
public interface AffiliationService {

    PageResult<Affiliation> list(String name, Integer page, Integer size);

    Affiliation create(AffiliationSaveDTO dto);

    Affiliation update(String affiliationId, AffiliationSaveDTO dto);

    void delete(String affiliationId);
}
