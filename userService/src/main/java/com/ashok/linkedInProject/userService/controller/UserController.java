package com.ashok.linkedInProject.userService.controller;

import com.ashok.linkedInProject.userService.dto.UserDto;
import com.ashok.linkedInProject.userService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "User profile management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDto> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateProfile(userId, userDto));
    }
}
