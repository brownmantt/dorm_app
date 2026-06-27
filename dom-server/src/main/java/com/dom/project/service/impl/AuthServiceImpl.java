package com.dom.project.service.impl;

import com.dom.project.config.AuthProperties;
import com.dom.project.entity.dto.LoginDTO;
import com.dom.project.entity.vo.LoginVO;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.service.AuthService;
import com.dom.project.util.OperationLogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 認証サービス実装。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthProperties authProperties;
    private final OperationLogWriter operationLogWriter;

    public AuthServiceImpl(AuthProperties authProperties, OperationLogWriter operationLogWriter) {
        this.authProperties = authProperties;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        AuthProperties.UserEntry user = authProperties.getUsers().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("AUTH_FAILED", "ユーザー名またはパスワードが正しくありません"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException("AUTH_FAILED", "ユーザー名またはパスワードが正しくありません");
        }

        String token = Base64.getEncoder().encodeToString(
                (dto.getUsername() + ":" + UUID.randomUUID()).getBytes());
        List<String> roles = user.getRoles();
        try {
            operationLogWriter.logAction(
                    OperationTypeEnum.LOGIN,
                    TargetTableEnum.AUTH,
                    dto.getUsername(),
                    null,
                    dto.getUsername(),
                    dto.getUsername());
        } catch (RuntimeException ex) {
            log.warn("ログイン操作ログの書き込みに失敗しました（ログインは継続）: {}", ex.getMessage());
        }
        return new LoginVO(token, dto.getUsername(), roles);
    }
}
