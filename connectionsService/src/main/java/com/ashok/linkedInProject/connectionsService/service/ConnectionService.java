package com.ashok.linkedInProject.connectionsService.service;

import com.ashok.linkedInProject.connectionsService.dto.ConnectionDto;
import com.ashok.linkedInProject.connectionsService.entity.Connection;
import com.ashok.linkedInProject.connectionsService.entity.Connection.ConnectionStatus;
import com.ashok.linkedInProject.connectionsService.event.ConnectionEvent;
import com.ashok.linkedInProject.connectionsService.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    private static final String CONNECTION_TOPIC = "connection-events";

    @Transactional
    public ConnectionDto sendConnectionRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new RuntimeException("Cannot send connection request to yourself");
        }

        // Check if connection already exists in either direction
        if (connectionRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId) ||
                connectionRepository.existsByRequesterIdAndReceiverId(receiverId, requesterId)) {
            throw new RuntimeException("Connection request already exists");
        }

        Connection connection = new Connection();
        connection.setRequesterId(requesterId);
        connection.setReceiverId(receiverId);
        connection.setStatus(ConnectionStatus.PENDING);

        Connection saved = connectionRepository.save(connection);
        log.info("Connection request sent from {} to {}", requesterId, receiverId);

        // Publish Kafka event
        publishEvent(saved, "CONNECTION_REQUESTED");

        return modelMapper.map(saved, ConnectionDto.class);
    }

    @Transactional
    public ConnectionDto acceptConnectionRequest(Long connectionId, Long userId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!connection.getReceiverId().equals(userId)) {
            throw new RuntimeException("Only the receiver can accept a connection request");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Connection request is not pending");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        Connection saved = connectionRepository.save(connection);
        log.info("Connection accepted: {} and {}", connection.getRequesterId(), connection.getReceiverId());

        publishEvent(saved, "CONNECTION_ACCEPTED");

        return modelMapper.map(saved, ConnectionDto.class);
    }

    @Transactional
    public ConnectionDto rejectConnectionRequest(Long connectionId, Long userId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!connection.getReceiverId().equals(userId)) {
            throw new RuntimeException("Only the receiver can reject a connection request");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        Connection saved = connectionRepository.save(connection);
        log.info("Connection rejected: {} by {}", connection.getRequesterId(), userId);

        publishEvent(saved, "CONNECTION_REJECTED");

        return modelMapper.map(saved, ConnectionDto.class);
    }

    public List<ConnectionDto> getAcceptedConnections(Long userId) {
        return connectionRepository.findByUserIdAndStatus(userId, ConnectionStatus.ACCEPTED).stream()
                .map(c -> modelMapper.map(c, ConnectionDto.class))
                .toList();
    }

    public List<ConnectionDto> getPendingRequests(Long userId) {
        return connectionRepository.findPendingRequestsForUser(userId).stream()
                .map(c -> modelMapper.map(c, ConnectionDto.class))
                .toList();
    }

    public Long getConnectionCount(Long userId) {
        return connectionRepository.countConnectionsByUserId(userId);
    }

    private void publishEvent(Connection connection, String eventType) {
        ConnectionEvent event = new ConnectionEvent(
                connection.getId(),
                connection.getRequesterId(),
                connection.getReceiverId(),
                eventType,
                LocalDateTime.now());
        kafkaTemplate.send(CONNECTION_TOPIC, connection.getId().toString(), event);
    }
}
