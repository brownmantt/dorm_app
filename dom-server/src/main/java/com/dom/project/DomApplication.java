package com.dom.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 寮管理システム バックエンド起動クラス。
 */
@SpringBootApplication
public class DomApplication {

    /**
     * アプリケーションエントリポイント。
     *
     * @param args 起動引数
     */
    public static void main(String[] args) {
        SpringApplication.run(DomApplication.class, args);
    }
}
