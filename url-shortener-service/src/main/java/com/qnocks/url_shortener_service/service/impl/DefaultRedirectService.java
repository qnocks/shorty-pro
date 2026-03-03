package com.qnocks.url_shortener_service.service.impl;

import com.qnocks.url_shortener_service.service.RedirectService;
import com.qnocks.url_shortener_service.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shorty.shared.UrlClickEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRedirectService implements RedirectService {

    private final UrlService urlService;
    private final KafkaClickEventPublisher kafkaClickEventPublisher;

    @Override
    public String getRedirectUrl(HttpServletRequest request, String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        String ipAddress = getClientIpAddress(request);
        String userAgent = getUserAgent(request);

        UrlClickEvent clickEvent = new UrlClickEvent(
                UUID.randomUUID().toString(),
                shortCode,
                originalUrl,
                ipAddress,
                userAgent,
                LocalDateTime.now());

        kafkaClickEventPublisher.publish(clickEvent);

        log.debug("Redirecting to [{}]", originalUrl);

        return originalUrl;
    }

    /**
     * Extracts the client's real IP address considering proxies and load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        try {
            // Check common headers for real IP (in order of preference)
            String[] ipHeaders = {
                    "X-Forwarded-For",
                    "X-Real-IP",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_X_FORWARDED_FOR",
                    "HTTP_X_FORWARDED",
                    "HTTP_X_CLUSTER_CLIENT_IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_FORWARDED_FOR",
                    "HTTP_FORWARDED",
                    "HTTP_VIA",
                    "X-Cluster-Client-IP"
            };

            for (String header : ipHeaders) {
                String ip = request.getHeader(header);
                if (isValidIpAddress(ip)) {
                    return extractFirstIp(ip);
                }
            }

            // Fallback to remote address
            String remoteAddr = request.getRemoteAddr();
            return remoteAddr;

        } catch (Exception e) {
            log.warn("Error extracting IP address, using fallback: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Validates if the IP address is not empty, unknown, or local
     */
    private boolean isValidIpAddress(String ip) {
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        // Check for common placeholder values
        String lowerIp = ip.toLowerCase();
        return !lowerIp.contains("unknown") &&
                !lowerIp.equals("0:0:0:0:0:0:0:1") &&
                !lowerIp.equals("127.0.0.1");
    }

    /**
     * Extracts the first IP from X-Forwarded-For header (client, proxy1, proxy2...)
     */
    private String extractFirstIp(String ipHeader) {
        if (ipHeader.contains(",")) {
            String[] ips = ipHeader.split(",");
            for (String ip : ips) {
                String trimmedIp = ip.trim();
                if (isValidIpAddress(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        return ipHeader.trim();
    }

    /**
     * Extracts User-Agent from request headers
     */
    private String getUserAgent(HttpServletRequest request) {
        try {
            String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
            if (userAgent != null && !userAgent.trim().isEmpty()) {
                // Limit length to prevent overly large user agents
                return userAgent.length() > 1000 ? userAgent.substring(0, 1000) : userAgent;
            }
            return "unknown";
        } catch (Exception e) {
            log.warn("Error extracting User-Agent: {}", e.getMessage());
            return "unknown";
        }
    }
}
