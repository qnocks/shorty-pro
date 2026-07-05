package com.qnocks.shorty.url_shortener_service.aspect;

import com.qnocks.shorty.url_shortener_service.metric.RedirectMetrics;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedirectMetricsAspect {

    private final RedirectMetrics redirectMetrics;

    @AfterReturning(
            pointcut = "execution(public void com.qnocks.shorty.url_shortener_service.controller.RedirectControllerV1.redirectToOriginalUrl(..))&& args(shortCode,..)",
            argNames = "shortCode")
    public void afterSuccessfulRedirect(String shortCode) {
        redirectMetrics.incrementSuccessfulRedirect(shortCode);
    }
}
