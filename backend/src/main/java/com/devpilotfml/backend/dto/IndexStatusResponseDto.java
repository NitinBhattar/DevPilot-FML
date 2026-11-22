package com.devpilotfml.backend.dto;

import com.devpilotfml.backend.enums.IndexStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record IndexStatusResponseDto(
    UUID id,
    IndexStatus indexStatus,
    int chunkCount,
    long filesTotal,
    long filesProcessed,
    String errorMessage,
    Instant indexedAt
) {}
