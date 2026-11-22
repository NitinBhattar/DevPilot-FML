package com.devpilotfml.backend.dto;

import com.devpilotfml.backend.enums.MessageRole;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChatMessageResponseDto(
    UUID id,
    MessageRole role,
    String content,
    List<CitationDto> citations,
    Instant createdAt
) {}
