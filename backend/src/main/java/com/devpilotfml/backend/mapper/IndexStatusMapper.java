package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.IndexStatusResponseDto;
import com.devpilotfml.backend.entity.Repo;
import org.mapstruct.Mapper;

@Mapper
public interface IndexStatusMapper {
    public IndexStatusResponseDto toDto(Repo repo);
}
