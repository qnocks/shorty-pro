package com.qnocks.url_shortener_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJdbcAuditing
@EnableAsync
@SpringBootApplication
public class UrlShortenerServiceApplication {

    // TODO: kafka
    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerServiceApplication.class, args);
    }
}
