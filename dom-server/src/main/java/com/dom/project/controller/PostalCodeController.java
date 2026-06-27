package com.dom.project.controller;

import com.dom.project.entity.vo.AddressLookupVO;
import com.dom.project.service.PostalCodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 郵便番号 API コントローラ。
 */
@RestController
@RequestMapping("/postal-codes")
public class PostalCodeController {

    private final PostalCodeService postalCodeService;

    public PostalCodeController(PostalCodeService postalCodeService) {
        this.postalCodeService = postalCodeService;
    }

    /**
     * 郵便番号から住所を取得する。
     */
    @GetMapping("/{postalCode}/address")
    public AddressLookupVO lookupAddress(@PathVariable String postalCode) {
        return postalCodeService.lookupAddress(postalCode);
    }
}
