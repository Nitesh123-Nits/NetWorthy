package com.ashok.linkedInProject.userService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String headline;
    private String about;
    private String profilePicture;
    private String location;
    private LocalDateTime createdAt;
}
