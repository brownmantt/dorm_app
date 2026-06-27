package com.dom.project.service;

import com.dom.project.entity.Region;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.RegionSaveDTO;

/**
 * 地域マスタサービス。
 */
public interface RegionService {

    PageResult<Region> list(String name, Integer page, Integer size);

    Region create(RegionSaveDTO dto);

    Region update(String regionId, RegionSaveDTO dto);

    void delete(String regionId);
}
