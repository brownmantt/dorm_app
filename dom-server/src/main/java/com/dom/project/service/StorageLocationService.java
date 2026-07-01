package com.dom.project.service;

import com.dom.project.entity.StorageLocation;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.StorageLocationSaveDTO;

/**
 * 保管場所マスタサービス。
 */
public interface StorageLocationService {

    PageResult<StorageLocation> list(String name, Integer page, Integer size);

    StorageLocation create(StorageLocationSaveDTO dto);

    StorageLocation update(String storageLocationId, StorageLocationSaveDTO dto);

    void delete(String storageLocationId);
}
