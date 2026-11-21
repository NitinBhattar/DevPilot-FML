package com.devpilotfml.backend.service;

import com.devpilotfml.backend.dto.RepoResponseDto;

import java.util.List;
import java.util.UUID;

public interface RepoService {
    public List<RepoResponseDto> syncAndListRepo(UUID userId);
}
