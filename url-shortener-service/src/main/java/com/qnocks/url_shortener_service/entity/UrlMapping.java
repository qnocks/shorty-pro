package com.qnocks.url_shortener_service.entity;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Value
@Builder
@Table("url_mappings")
public class UrlMapping {

    @Id
    Long id;
    String originalUrl;
    String shortUrl;

    @CreatedDate
    LocalDateTime createdAt;
}
