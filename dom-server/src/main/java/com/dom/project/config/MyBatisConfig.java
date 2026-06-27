package com.dom.project.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 永続層設定。
 * Mapper インターフェースのスキャンと XML SQL マッピングを本設定および application.yml で一元管理。
 */
@Configuration
@MapperScan("com.dom.project.mapper")
public class MyBatisConfig {
}
