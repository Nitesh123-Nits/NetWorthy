package com.ashok.linkedInProject.connectionsService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionEvent {
    private Long connectionId;
    private Long requesterId;
    private Long receiverId;
    private String eventType; // CONNECTION_REQUESTED, CONNECTION_ACCEPTED, CONNECTION_REJECTED
    private LocalDateTime timestamp;
}
