package com.devpilotfml.backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RetrievedContextDto(
    List<CitationDto> citations,
    String contextText
) {}
