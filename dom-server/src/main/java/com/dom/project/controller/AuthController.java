package com.dom.project.controller;

import com.dom.project.entity.dto.LoginDTO;
import com.dom.project.entity.vo.LoginVO;
import com.dom.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証 API コントローラ。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * ログイン。
     */
    @PostMapping("/login")
    public LoginVO login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
}
