package com.devpilotfml.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChatMessageRequestDto(
    @NotBlank String content
) {}
