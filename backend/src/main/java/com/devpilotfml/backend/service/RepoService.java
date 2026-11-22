package com.devpilotfml.backend.service;

import com.devpilotfml.backend.dto.IndexStatusResponseDto;
import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.Repo;

import java.util.List;
import java.util.UUID;

public interface RepoService {
    public List<RepoResponseDto> syncAndListRepos(UUID userId);
    public List<RepoResponseDto> repoSortedList(UUID userId);
    public RepoResponseDto requiredRepo(UUID repoId, UUID userId);
    public IndexStatusResponseDto indexStatus(UUID repoId, UUID userId);
}
