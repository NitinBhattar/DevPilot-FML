package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.Repo;
import org.mapstruct.Mapper;

@Mapper
public interface RepoMapper {
    public RepoResponseDto toDto(Repo repo);
}
