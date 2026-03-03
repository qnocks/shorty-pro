package com.qnocks.url_shortener_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic urlClickEventsTopic() {
        KafkaTopicsProperties.TopicProperties topic = kafkaTopicsProperties.topic("url-click-events");

        return TopicBuilder.name(topic.name())
                .partitions(topic.partitions())
                .replicas(topic.replicas())
                .build();
    }
}
