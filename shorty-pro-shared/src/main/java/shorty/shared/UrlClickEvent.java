package shorty.shared;

import java.time.LocalDateTime;

public record UrlClickEvent(
        String id,
        String shortCode,
        String originalUrl,
        String ipAddress,
        String userAgent,
        LocalDateTime createdAt
) {
}
