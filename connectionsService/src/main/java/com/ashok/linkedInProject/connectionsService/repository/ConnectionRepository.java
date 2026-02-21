package com.ashok.linkedInProject.connectionsService.repository;

import com.ashok.linkedInProject.connectionsService.entity.Connection;
import com.ashok.linkedInProject.connectionsService.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    @Query("SELECT c FROM Connection c WHERE (c.requesterId = :userId OR c.receiverId = :userId) AND c.status = :status")
    List<Connection> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ConnectionStatus status);

    @Query("SELECT c FROM Connection c WHERE c.receiverId = :userId AND c.status = 'PENDING'")
    List<Connection> findPendingRequestsForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Connection c WHERE (c.requesterId = :userId OR c.receiverId = :userId) AND c.status = 'ACCEPTED'")
    Long countConnectionsByUserId(@Param("userId") Long userId);

    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);
}
