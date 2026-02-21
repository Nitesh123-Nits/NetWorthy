package com.ashok.linkedInProject.userService.controller;

import com.ashok.linkedInProject.userService.dto.LoginRequestDto;
import com.ashok.linkedInProject.userService.dto.LoginResponseDto;
import com.ashok.linkedInProject.userService.dto.SignupRequestDto;
import com.ashok.linkedInProject.userService.dto.UserDto;
import com.ashok.linkedInProject.userService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication APIs")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        UserDto user = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
