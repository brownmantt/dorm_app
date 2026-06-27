package com.dom.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API ルート・ヘルスチェック。
 */
@RestController
public class HealthController {

    @GetMapping({"/", "/health"})
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put("service", "dom-server");
        body.put("apiBase", "/api/v1");
        body.put("login", "POST /auth/login");
        body.put("hint", "ブラウザから API を直接開く場合はログイン後、Authorization: Bearer <token> が必要です。通常はフロント http://localhost:5173 から利用してください。");
        return body;
    }
}
