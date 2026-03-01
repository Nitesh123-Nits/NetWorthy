package com.ashok.linkedInProject.notificationService.service;

import com.ashok.linkedInProject.notificationService.dto.NotificationDto;
import com.ashok.linkedInProject.notificationService.entity.Notification;
import com.ashok.linkedInProject.notificationService.entity.Notification.NotificationType;
import com.ashok.linkedInProject.notificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;

    public NotificationDto createAndSendNotification(Long userId, String message,
            NotificationType type, Long referenceId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);

        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = modelMapper.map(saved, NotificationDto.class);

        // Push real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                dto);
        log.info("Notification sent to user {}: {}", userId, message);

        return dto;
    }

    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> modelMapper.map(n, NotificationDto.class))
                .toList();
    }

    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(n -> modelMapper.map(n, NotificationDto.class))
                .toList();
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("You cannot mark this notification as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        log.info("Marked all notifications as read for user {}", userId);
    }
}
