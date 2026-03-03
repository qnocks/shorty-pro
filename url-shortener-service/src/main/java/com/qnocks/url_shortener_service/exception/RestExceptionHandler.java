package com.qnocks.url_shortener_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${app.exception.trace}")
    private boolean printStackTrace;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        return buildResponse(e, e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(HttpServletRequest request) {
        log.warn("ClientAbortException handled for request: [{} {}] from remote address [{}]",
                request.getMethod(), request.getRequestURL(), request.getRemoteAddr());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request) {
        var errors = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> errors.append(
                String.format("[%s : %s] ", ((FieldError) error).getField(), error.getDefaultMessage())));

        return buildResponse(e, String.format("%d validation errors for request body: %s",
                e.getBindingResult().getErrorCount(), errors), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, WebRequest request) {
        return buildResponse(e, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(Exception e, String message, HttpStatus status, WebRequest request) {
        var responseBuilder = ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .error(status)
                .timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        if (printStackTrace && isTraceOn(request)) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            responseBuilder.stackTrace(stackTrace);
        }

        return ResponseEntity.status(status).body(responseBuilder.build());
    }

    private boolean isTraceOn(WebRequest request) {
        var value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals(Boolean.TRUE.toString());
    }
}
