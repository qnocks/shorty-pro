package com.qnocks.shorty.url_shortener_service.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class TraceResponseHeaderFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        addTraceHeaders(response);
        filterChain.doFilter(request, response);
    }

    private void addTraceHeaders(HttpServletResponse response) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            return;
        }

        response.setHeader(TRACE_ID_HEADER, currentSpan.context().traceId());
        response.setHeader(SPAN_ID_HEADER, currentSpan.context().spanId());
    }
}
