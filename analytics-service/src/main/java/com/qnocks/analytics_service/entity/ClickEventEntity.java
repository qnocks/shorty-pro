package com.qnocks.analytics_service.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "eventId")
@Table(name = "click_events")
public class ClickEventEntity {

    @Id
    private Long id;
    private UUID eventId;
    private String shortCode;
    private String originalUrl;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}