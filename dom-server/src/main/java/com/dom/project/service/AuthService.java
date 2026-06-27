package com.dom.project.service;

import com.dom.project.entity.dto.LoginDTO;
import com.dom.project.entity.vo.LoginVO;

/**
 * 認証サービスインターフェース。
 */
public interface AuthService {

    LoginVO login(LoginDTO dto);
}
