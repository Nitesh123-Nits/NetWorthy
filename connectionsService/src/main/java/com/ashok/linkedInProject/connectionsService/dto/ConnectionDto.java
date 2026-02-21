package com.ashok.linkedInProject.connectionsService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectionDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String status;
    private LocalDateTime createdAt;
}
