package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.ChatSessionResponseDto;
import com.devpilotfml.backend.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface ChatSessionMapper {
    public ChatSessionResponseDto toDto(ChatSession chatSession);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "repositoryId", source = "repositoryId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "createdAt", ignore = true)
    public ChatSession toEntity(UUID userId, UUID repositoryId, String title);
}
