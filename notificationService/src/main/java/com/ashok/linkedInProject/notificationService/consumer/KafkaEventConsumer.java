package com.ashok.linkedInProject.notificationService.consumer;

import com.ashok.linkedInProject.notificationService.entity.Notification.NotificationType;
import com.ashok.linkedInProject.notificationService.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "post-created", groupId = "notification-service")
    public void handlePostCreated(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            Long userId = event.get("userId").asLong();
            Long postId = event.get("postId").asLong();

            notificationService.createAndSendNotification(
                    userId,
                    "Your post has been published successfully!",
                    NotificationType.POST_CREATED,
                    postId);
            log.info("Processed post_created event for postId: {}", postId);
        } catch (Exception e) {
            log.error("Failed to process post_created event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "post-liked", groupId = "notification-service")
    public void handlePostLiked(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            Long postOwnerId = event.get("postOwnerId").asLong();
            Long likedByUserId = event.get("likedByUserId").asLong();
            Long postId = event.get("postId").asLong();

            // Don't notify if user liked their own post
            if (!postOwnerId.equals(likedByUserId)) {
                notificationService.createAndSendNotification(
                        postOwnerId,
                        "Someone liked your post!",
                        NotificationType.POST_LIKED,
                        postId);
            }
            log.info("Processed post_liked event for postId: {}", postId);
        } catch (Exception e) {
            log.error("Failed to process post_liked event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "connection-events", groupId = "notification-service")
    public void handleConnectionEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            Long requesterId = event.get("requesterId").asLong();
            Long receiverId = event.get("receiverId").asLong();
            Long connectionId = event.get("connectionId").asLong();

            switch (eventType) {
                case "CONNECTION_REQUESTED" -> notificationService.createAndSendNotification(
                        receiverId,
                        "You have a new connection request!",
                        NotificationType.CONNECTION_REQUEST,
                        connectionId);
                case "CONNECTION_ACCEPTED" -> notificationService.createAndSendNotification(
                        requesterId,
                        "Your connection request has been accepted!",
                        NotificationType.CONNECTION_ACCEPTED,
                        connectionId);
                default -> log.debug("Ignoring connection event type: {}", eventType);
            }
            log.info("Processed {} event for connectionId: {}", eventType, connectionId);
        } catch (Exception e) {
            log.error("Failed to process connection event: {}", e.getMessage());
        }
    }
}
