package com.qnocks.analytics_service.handler;

import com.qnocks.analytics_service.entity.ClickEventEntity;
import com.qnocks.analytics_service.repository.ClickEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import shorty.shared.UrlClickEvent;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickEventConsumer {

    private final ClickEntityRepository clickEntityRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.url-click-events.name}",
            groupId = "${app.kafka.topics.url-click-events.group-id}")
    public void handleUrlClickEvent(ConsumerRecord<String, UrlClickEvent> record) {
        UrlClickEvent event = record.value();

        if (event == null || !StringUtils.hasText(event.id())) {
            log.warn("Received invalid click event payload: {}", event);
            return;
        }

        UUID eventId;
        try {
            eventId = UUID.fromString(event.id());
        } catch (IllegalArgumentException e) {
            log.warn("Skipping click event with invalid UUID: {}", event.id());
            return;
        }

        ClickEventEntity clickEventEntity = ClickEventEntity.builder()
                .eventId(eventId)
                .shortCode(event.shortCode())
                .originalUrl(event.originalUrl())
                .userAgent(event.userAgent())
                .ipAddress(event.ipAddress())
                .createdAt(event.createdAt())
                .build();

        try {
            clickEntityRepository.save(clickEventEntity);
            log.debug("Stored click event: {}", clickEventEntity.getEventId());
        } catch (DataIntegrityViolationException e) {
            // event_id is unique in DB, so duplicate deliveries are safe to ignore.
            log.warn("Duplicate click event received, skipping eventId={}", clickEventEntity.getEventId());
        }
    }
}
