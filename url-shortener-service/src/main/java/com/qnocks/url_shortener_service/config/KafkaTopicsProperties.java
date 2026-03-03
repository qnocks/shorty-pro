package com.qnocks.url_shortener_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicsProperties {

    private Map<String, TopicProperties> topics = new HashMap<>();

    public TopicProperties topic(String key) {
        TopicProperties topicProperties = topics.get(key);

        if (topicProperties == null || !StringUtils.hasText(topicProperties.name)) {
            throw new IllegalStateException("Missing kafka topic configuration for key: " + key);
        }

        return topicProperties;
    }

    public record TopicProperties(
            String name,
            int partitions,
            int replicas,
            String groupId,
            String dltName
    ) {
    }
}
