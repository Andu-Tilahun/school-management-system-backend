package com.schoolmanagment.commonapplication.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private UUID id;
    private String externalId;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String message;
    private String eventType;
    private Map<String, Object> eventData;
    private Map<String, Object> errorMessage;
    private Integer retryCount;
    private NotificationPriority priority;
}

