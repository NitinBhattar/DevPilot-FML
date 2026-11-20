package com.devpilotfml.backend.dto;



import java.util.UUID;

public record UserResponseDto (
    UUID id,
    Long githubId,
    String githubUsername,
    String displayName,
    String avatarUrl
) {}
