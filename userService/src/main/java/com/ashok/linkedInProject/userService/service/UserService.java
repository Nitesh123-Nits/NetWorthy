package com.ashok.linkedInProject.userService.service;

import com.ashok.linkedInProject.userService.dto.*;
import com.ashok.linkedInProject.userService.entity.User;
import com.ashok.linkedInProject.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    public UserDto signup(SignupRequestDto signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already registered: " + signupRequest.getEmail());
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        return modelMapper.map(savedUser, UserDto.class);
    }

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        log.info("User logged in: {}", user.getEmail());
        return new LoginResponseDto(token, user.getId());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto updateProfile(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (userDto.getName() != null)
            user.setName(userDto.getName());
        if (userDto.getHeadline() != null)
            user.setHeadline(userDto.getHeadline());
        if (userDto.getAbout() != null)
            user.setAbout(userDto.getAbout());
        if (userDto.getProfilePicture() != null)
            user.setProfilePicture(userDto.getProfilePicture());
        if (userDto.getLocation() != null)
            user.setLocation(userDto.getLocation());

        User updatedUser = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);
        return modelMapper.map(updatedUser, UserDto.class);
    }
}
