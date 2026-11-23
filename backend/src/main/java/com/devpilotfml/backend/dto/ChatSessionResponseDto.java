package com.devpilotfml.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatSessionResponseDto(
    UUID id,
    UUID repositoryId,
    String title,
    Instant createdAt
) {}
