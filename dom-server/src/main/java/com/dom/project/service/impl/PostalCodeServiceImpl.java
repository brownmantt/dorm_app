package com.dom.project.service.impl;

import com.dom.project.entity.vo.AddressLookupVO;
import com.dom.project.exception.BusinessException;
import com.dom.project.service.PostalCodeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * 郵便番号住所検索サービス実装（zipcloud API 連携）。
 */
@Service
public class PostalCodeServiceImpl implements PostalCodeService {

    private static final Logger log = LoggerFactory.getLogger(PostalCodeServiceImpl.class);
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{7}$");
    private static final String ZIPCLOUD_URL = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PostalCodeServiceImpl(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public AddressLookupVO lookupAddress(String postalCode) {
        String normalized = normalizePostalCode(postalCode);
        if (!POSTAL_CODE_PATTERN.matcher(normalized).matches()) {
            throw new BusinessException("INVALID_POSTAL_CODE", "郵便番号は7桁の数字で入力してください");
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(ZIPCLOUD_URL + normalized, String.class);
            JsonNode body = objectMapper.readTree(response.getBody());
            if (body == null || body.path("results").isNull() || !body.path("results").isArray()
                    || body.path("results").isEmpty()) {
                String message = body != null && body.hasNonNull("message")
                        ? body.get("message").asText()
                        : "該当する住所が見つかりません";
                throw new BusinessException("POSTAL_CODE_NOT_FOUND", message);
            }

            JsonNode first = body.path("results").get(0);
            String prefecture = first.path("address1").asText("");
            String city = first.path("address2").asText("");
            String town = first.path("address3").asText("");
            String address = prefecture + city + town;

            AddressLookupVO vo = new AddressLookupVO();
            vo.setPostalCode(normalized);
            vo.setPrefecture(prefecture);
            vo.setCity(city);
            vo.setTown(town);
            vo.setAddress(address);
            return vo;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("郵便番号検索 API 呼び出し失敗: {}", ex.getMessage());
            throw new BusinessException("POSTAL_CODE_LOOKUP_FAILED", "住所の自動取得に失敗しました。しばらくしてから再度お試しください。");
        }
    }

    private String normalizePostalCode(String postalCode) {
        if (postalCode == null) {
            return "";
        }
        return postalCode.replaceAll("[^0-9]", "");
    }
}
