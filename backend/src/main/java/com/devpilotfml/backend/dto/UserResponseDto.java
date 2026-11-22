package com.devpilotfml.backend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto (
    UUID id,
    Long githubId,
    String githubUsername,
    String displayName,
    String avatarUrl
) {}
