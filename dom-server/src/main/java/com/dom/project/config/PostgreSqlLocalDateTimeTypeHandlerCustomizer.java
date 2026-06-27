package com.dom.project.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * PostgreSQL TIMESTAMPTZ 列の LocalDateTime マッピングを最優先で上書きする。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class PostgreSqlLocalDateTimeTypeHandlerCustomizer implements ConfigurationCustomizer {

    @Override
    public void customize(Configuration configuration) {
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        PostgreSqlLocalDateTimeTypeHandler handler = new PostgreSqlLocalDateTimeTypeHandler();
        registry.register(LocalDateTime.class, handler);
        registry.register(LocalDateTime.class, JdbcType.TIMESTAMP, handler);
        registry.register(LocalDateTime.class, JdbcType.TIMESTAMP_WITH_TIMEZONE, handler);
    }
}
