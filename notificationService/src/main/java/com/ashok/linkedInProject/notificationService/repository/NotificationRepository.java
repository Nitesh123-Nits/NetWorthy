package com.ashok.linkedInProject.notificationService.repository;

import com.ashok.linkedInProject.notificationService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndReadFalse(Long userId);
}
