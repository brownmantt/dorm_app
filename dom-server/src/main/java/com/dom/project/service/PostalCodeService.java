package com.dom.project.service;

import com.dom.project.entity.vo.AddressLookupVO;

/**
 * 郵便番号住所検索サービスインターフェース。
 */
public interface PostalCodeService {

    /**
     * 郵便番号から住所を検索する。
     *
     * @param postalCode 郵便番号（7桁）
     * @return 住所情報
     */
    AddressLookupVO lookupAddress(String postalCode);
}
