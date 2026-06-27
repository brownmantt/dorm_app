package com.dom.project.util;

import com.dom.project.constant.AppConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 認証コンテキストユーティリティ。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 現在の操作者ユーザー名を取得する。
     *
     * @return ユーザー名。未認証時は {@link AppConstants#SYSTEM_OPERATOR}
     */
    public static String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return AppConstants.SYSTEM_OPERATOR;
        }
        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) {
            return AppConstants.SYSTEM_OPERATOR;
        }
        return authentication.getName();
    }
}
