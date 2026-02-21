package com.ashok.linkedInProject.connectionsService.controller;

import com.ashok.linkedInProject.connectionsService.dto.ConnectionDto;
import com.ashok.linkedInProject.connectionsService.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
@Tag(name = "Connections", description = "Connection management APIs")
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request/{receiverId}")
    @Operation(summary = "Send a connection request")
    public ResponseEntity<ConnectionDto> sendConnectionRequest(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable Long receiverId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(connectionService.sendConnectionRequest(requesterId, receiverId));
    }

    @PutMapping("/{connectionId}/accept")
    @Operation(summary = "Accept a connection request")
    public ResponseEntity<ConnectionDto> acceptConnection(
            @PathVariable Long connectionId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.acceptConnectionRequest(connectionId, userId));
    }

    @PutMapping("/{connectionId}/reject")
    @Operation(summary = "Reject a connection request")
    public ResponseEntity<ConnectionDto> rejectConnection(
            @PathVariable Long connectionId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.rejectConnectionRequest(connectionId, userId));
    }

    @GetMapping
    @Operation(summary = "Get all accepted connections")
    public ResponseEntity<List<ConnectionDto>> getConnections(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.getAcceptedConnections(userId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending connection requests")
    public ResponseEntity<List<ConnectionDto>> getPendingRequests(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.getPendingRequests(userId));
    }

    @GetMapping("/count/{userId}")
    @Operation(summary = "Get connection count for a user")
    public ResponseEntity<Long> getConnectionCount(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionService.getConnectionCount(userId));
    }
}
