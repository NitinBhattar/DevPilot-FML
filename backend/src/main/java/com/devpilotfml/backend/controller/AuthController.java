package com.devpilotfml.backend.controller;

import com.devpilotfml.backend.dto.UserResponseDto;
import com.devpilotfml.backend.entity.User;
import com.devpilotfml.backend.mapper.UserMapper;
import com.devpilotfml.backend.security.AppUserPrincipal;
import com.devpilotfml.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserMapper userMapper;
    private final CurrentUser currentUser;


    @GetMapping("/login-url")
    public Map<String, String> loginUrl() {
        return Map.of("url", "/oauth2/authorization/github");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me() {
        AppUserPrincipal principal = currentUser.require();

        User user = principal.getUser();

        UserResponseDto userResponseDto = userMapper.toDto(user);

        return ResponseEntity.ok().body(userResponseDto);
    }
}
