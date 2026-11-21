package com.devpilotfml.backend.dto;

import com.devpilotfml.backend.enums.IndexStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record RepoResponseDto(
    UUID id,
    Long githubRepoId,
    String owner,
    String name,
    String fullName,
    @JsonProperty("isPrivate") boolean isPrivate,
    String defaultBranch,
    String language,
    String htmlUrl,
    String description,
    IndexStatus indexStatus,
    int chunkCount,
    long filesTotal,
    long filesProcessed,
    String errorMessage,
    Instant indexedAt
) {}
