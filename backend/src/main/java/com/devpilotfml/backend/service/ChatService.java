package com.devpilotfml.backend.service;

import com.devpilotfml.backend.dto.ChatMessageResponseDto;
import com.devpilotfml.backend.dto.ChatSessionResponseDto;
import com.devpilotfml.backend.dto.CreateChatSessionRequestDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    public ChatSessionResponseDto createSession(UUID userId, CreateChatSessionRequestDto createChatSessionRequestDto);
    public List<ChatSessionResponseDto> listSessions(UUID userId, UUID repositoryId);
    public List<ChatMessageResponseDto> getMessages(UUID sessionId);
    public SseEmitter streamReply(UUID userId, UUID sessionId, String userContent);
}
