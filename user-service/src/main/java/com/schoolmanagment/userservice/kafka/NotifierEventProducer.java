package com.schoolmanagment.userservice.kafka;

import com.schoolmanagment.commonapplication.event.NotificationEvent;
import com.schoolmanagment.commonapplication.event.NotificationType;
import com.schoolmanagment.userservice.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class NotifierEventProducer {

    @Value("${kafka.topics.high}")
    private String notificationEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotifierEventProducer(@Qualifier("defaultKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void sendNotificationEvent(NotificationEvent notificationEvent) {
        try {
            kafkaTemplate.send(notificationEventsTopic, notificationEvent)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published notification event to Kafka: {}", notificationEvent);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing notification event to Kafka", e);
        }
    }

    public void sendWelcomeMessageByEmail(User user) {

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        eventData.put("email", user.getEmail());

        String message = String.format(
                "Hello %s,\n\n" +
                        "Welcome to our platform! Your account has been successfully created.\n\n" +
                        "Username: %s\n\n" +
                        "Best regards,\n" +
                        "The Team",
                user.getFirstName(),
                user.getUsername()
        );

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.EMAIL)
                .recipient(user.getEmail())
                .subject("Welcome to Our Platform!")
                .message(message)
                .eventType("USER_REGISTERED")
                .eventData(eventData)
                .build();

        sendNotificationEvent(notificationEvent);

    }

    public void sendPasswordResetMessageByEmail(User user, String token) {

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        eventData.put("email", user.getEmail());

        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        String message = String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "The Team",
                user.getFirstName(),
                resetUrl
        );

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .externalId(user.getId().toString())
                .type(NotificationType.EMAIL)
                .recipient(user.getEmail())
                .subject("Password Reset Request")
                .message(message)
                .eventType("PASSWORD_RESET_REQUESTED")
                .eventData(eventData)
                .build();

        sendNotificationEvent(notificationEvent);
    }

    public void sendPasswordChangedMessageByEmail(User user) {

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        eventData.put("email", user.getEmail());

        String message = String.format(
                "Hello %s,\n\n" +
                        "Your password has been successfully changed.\n\n" +
                        "If you didn't make this change, please contact us immediately.\n\n" +
                        "Best regards,\n" +
                        "The Team",
                user.getFirstName()
        );

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.EMAIL)
                .recipient(user.getEmail())
                .subject("Password Successfully Changed")
                .message(message)
                .eventType("PASSWORD_CHANGED")
                .eventData(eventData)
                .build();

        sendNotificationEvent(notificationEvent);
    }

    public void sendAccountLockedByEmail(User user) {

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        eventData.put("email", user.getEmail());

        String message = String.format(
                "Hello %s,\n\n" +
                        "Your account has been locked by an administrator.\n\n" +
                        "If you believe this is a mistake, please contact support.\n\n" +
                        "Best regards,\n" +
                        "The Team",
                user.getUsername()
        );

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.EMAIL)
                .recipient(user.getEmail())
                .subject("Password Successfully Changed")
                .message(message)
                .eventType("ACCOUNT_LOCKED")
                .eventData(eventData)
                .build();

        sendNotificationEvent(notificationEvent);

    }

    public void sendAccountUnlockedByEmail(User user) {

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        eventData.put("email", user.getEmail());

        String message = String.format(
                "Hello %s,\n\n" +
                        "Your account has been unlocked. You can now sign in again.\n\n" +
                        "Best regards,\n" +
                        "The Team",
                user.getUsername()
        );

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.EMAIL)
                .recipient(user.getEmail())
                .subject("Password Successfully Changed")
                .message(message)
                .eventType("ACCOUNT_UNLOCKED")
                .eventData(eventData)
                .build();

        sendNotificationEvent(notificationEvent);

    }
}
