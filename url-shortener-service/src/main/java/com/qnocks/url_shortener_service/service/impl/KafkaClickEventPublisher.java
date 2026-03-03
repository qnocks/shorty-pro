package com.qnocks.url_shortener_service.service.impl;

import com.qnocks.url_shortener_service.config.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import shorty.shared.UrlClickEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaClickEventPublisher {

    private final KafkaTemplate<String, UrlClickEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Async
    public void publish(UrlClickEvent clickEvent) {
        try {
            kafkaTemplate.send(kafkaTopicsProperties.topic("url-click-events").name(), clickEvent.id(), clickEvent)
                    .whenComplete((result, e) -> {
                        if (e == null && result != null) {
                            log.debug(
                                    "Click event sent successfully for shortCode: {}, partition: {}, offset: {}",
                                    clickEvent.shortCode(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                            return;
                        }

                        log.error("Failed to send click event for shortCode: {}", clickEvent.shortCode(), e);
                    });
        } catch (Exception e) {
            log.error("Kafka unavailable, skipping click event for shortCode: {}", clickEvent.shortCode(), e);
        }
    }
}
