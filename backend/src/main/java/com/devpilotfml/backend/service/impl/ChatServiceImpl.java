package com.devpilotfml.backend.service.impl;

import com.devpilotfml.backend.dto.ChatMessageResponseDto;
import com.devpilotfml.backend.dto.ChatSessionResponseDto;
import com.devpilotfml.backend.dto.CreateChatSessionRequestDto;
import com.devpilotfml.backend.dto.RetrievedContextDto;
import com.devpilotfml.backend.entity.ChatMessage;
import com.devpilotfml.backend.entity.ChatSession;
import com.devpilotfml.backend.entity.Repo;
import com.devpilotfml.backend.enums.IndexStatus;
import com.devpilotfml.backend.exception.BadRequestException;
import com.devpilotfml.backend.exception.NotFoundException;
import com.devpilotfml.backend.mapper.ChatMessageMapper;
import com.devpilotfml.backend.mapper.ChatSessionMapper;
import com.devpilotfml.backend.mapper.CitationMapper;
import com.devpilotfml.backend.repository.ChatMessageRepository;
import com.devpilotfml.backend.repository.ChatSessionRepository;
import com.devpilotfml.backend.service.ChatService;
import com.devpilotfml.backend.service.RepoService;
import com.devpilotfml.backend.service.ai.ChatPromptBuilder;
import com.devpilotfml.backend.service.ai.ChatStreamHandler;
import com.devpilotfml.backend.service.ai.CodeContextRetrieverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RepoService repoService;
    private final CodeContextRetrieverService codeContextRetrieverService;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatStreamHandler chatStreamHandler;
    private final CitationMapper citationMapper;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    public ChatSessionResponseDto createSession(UUID userId, CreateChatSessionRequestDto createChatSessionRequestDto) {
        Repo repo = repoService.requiredRepoEntity(createChatSessionRequestDto.repositoryId(), userId);

        if (repo.getIndexStatus() != IndexStatus.READY) {
            throw new BadRequestException("Repository must be indexed before chatting");
        }

        String title = (createChatSessionRequestDto.title() != null && !createChatSessionRequestDto.title().isBlank())
                    ? createChatSessionRequestDto.title()
                    : "Chat with " + repo.getFullName();

        ChatSession chatSession = chatSessionMapper.toEntity(userId, repo.getId(), title);

        ChatSession savedChatSession = chatSessionRepository.save(chatSession);

        return chatSessionMapper.toDto(savedChatSession);
    }

    public List<ChatSessionResponseDto> listSessions(UUID userId, UUID repositoryId) {
        return chatSessionRepository.findByUserIdAndRepositoryIdOrderByCreatedAtDesc(userId, repositoryId)
                .orElseThrow(() -> new NotFoundException("Chat sessions not found"))
                .stream()
                .map(chatSessionMapper::toDto)
                .toList();
    }

    public List<ChatMessageResponseDto> getMessages(UUID sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAt(sessionId)
                .orElseThrow(() -> new NotFoundException("Chat session not found"))
                .stream()
                .map(chatMessageMapper::toDto)
                .toList();
    }

    public SseEmitter streamReply(UUID userId, UUID sessionId, String userContent) {
        ChatSession chatSession = chatSessionRepository.findByIdAndUserId(sessionId, userId).orElseThrow(() -> new NotFoundException("Chat session not found"));
        Repo repo = repoService.requiredRepoEntity(chatSession.getRepositoryId(), userId);

        if (repo.getIndexStatus() != IndexStatus.READY) {
            throw new BadRequestException("Repository must be indexed before chatting");
        }

        ChatMessage userMessage = chatMessageMapper.toUserMessage(sessionId, userContent);

        ChatMessage savedUserMessage = chatMessageRepository.save(userMessage);

        RetrievedContextDto retrievedContextDto = codeContextRetrieverService.retrieve(repo.getId(), userContent);

        String systemPrompt = chatPromptBuilder.systemPrompt(repo.getFullName());
        String userPrompt = chatPromptBuilder.userPrompt(retrievedContextDto.contextText(), userContent);

        return chatStreamHandler.stream(
                sessionId,
                chatMessageMapper.toDto(savedUserMessage),
                retrievedContextDto.citations(),
                systemPrompt,
                userPrompt
        );
    }
}
