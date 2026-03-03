package com.qnocks.url_shortener_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String message,
        Integer status,
        HttpStatus error,
        String stackTrace,
        Long timestamp
) {
}
