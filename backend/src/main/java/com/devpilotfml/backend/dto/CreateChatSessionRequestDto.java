package com.devpilotfml.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateChatSessionRequestDto(
    @NotNull UUID repositoryId,
    String title
){}
