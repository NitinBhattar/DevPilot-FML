package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.ChatMessageResponseDto;
import com.devpilotfml.backend.dto.CitationDto;
import com.devpilotfml.backend.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

// ChatMessage has citations as string, so it will look for convertor, which is in CitationMapper.class
// So it will inject citationMapper, convert it from there
@Mapper(uses = CitationMapper.class)
public interface ChatMessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "role", constant = "ASSISTANT")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "citations", source = "citations")
    @Mapping(target = "createdAt", ignore = true)
    public ChatMessage toAssistantMessage(UUID sessionId, String content, List<CitationDto> citations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "citations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public ChatMessage toUserMessage(UUID sessionId, String content);

    public ChatMessageResponseDto toDto(ChatMessage chatMessage);
}
