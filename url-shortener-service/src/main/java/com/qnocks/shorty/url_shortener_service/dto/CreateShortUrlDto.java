package com.qnocks.shorty.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlDto(@NotBlank String url) {
}
