package com.dom.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring Security 設定。
 * フロントの ROLE_ADMIN / ROLE_USER と API パス権限を整合させる。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;
    private final RequestLoggingFilter requestLoggingFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter,
                          RequestLoggingFilter requestLoggingFilter,
                          ObjectMapper objectMapper) {
        this.bearerTokenAuthenticationFilter = bearerTokenAuthenticationFilter;
        this.requestLoggingFilter = requestLoggingFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/employees/**",
                                "/dormitories/**",
                                "/dorm-allocation/**",
                                "/affiliations",
                                "/regions",
                                "/usage-types",
                                "/postal-codes/**",
                                "/residences",
                                "/alerts/**",
                                "/vacancies/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/dorm-fees/**",
                                "/unit-prices/**",
                                "/equipments/**",
                                "/equipment-assets/**",
                                "/equipment-moveouts/**",
                                "/equipment-storages/**",
                                "/operation-logs/**",
                                "/imports/**",
                                "/exports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/affiliations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/affiliations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/affiliations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/regions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/regions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/regions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/usage-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usage-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usage-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/residences/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/residences/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/dormitories/**", "/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/dormitories/**", "/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/dormitories/**", "/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/dormitories/*/manager").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/dormitories/*/manager").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(this::writeUnauthorized)
                        .accessDeniedHandler(this::writeForbidden))
                .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(requestLoggingFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response,
                                   org.springframework.security.core.AuthenticationException ex)
            throws java.io.IOException {
        String authHeader = request.getHeader("Authorization");
        String message = StringUtils.hasText(authHeader)
                ? "認証の有効期限が切れました。再度ログインしてください。"
                : "認証が必要です。POST /auth/login でログインするか、フロント画面からアクセスしてください。";
        writeError(response, HttpStatus.UNAUTHORIZED, message);
    }

    private void writeForbidden(HttpServletRequest request, HttpServletResponse response,
                                org.springframework.security.access.AccessDeniedException ex)
            throws java.io.IOException {
        writeError(response, HttpStatus.FORBIDDEN, "この操作を行う権限がありません。");
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("detail", message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
