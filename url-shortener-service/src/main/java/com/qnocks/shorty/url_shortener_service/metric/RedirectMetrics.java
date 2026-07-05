package com.qnocks.shorty.url_shortener_service.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedirectMetrics {

    public static final String REDIRECT_COUNT = "shorty.redirect.count";

    private final MeterRegistry meterRegistry;
    public void incrementSuccessfulRedirect(String shortCode) {
        Counter.builder(REDIRECT_COUNT)
                .description("Total number of successful redirects by short code")
                .tag("short_code", shortCode)
                .register(meterRegistry)
                .increment();
    }
}
