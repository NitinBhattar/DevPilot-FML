package com.devpilotfml.backend.controller;

import com.devpilotfml.backend.dto.ChatMessageRequestDto;
import com.devpilotfml.backend.dto.ChatMessageResponseDto;
import com.devpilotfml.backend.dto.ChatSessionResponseDto;
import com.devpilotfml.backend.dto.CreateChatSessionRequestDto;
import com.devpilotfml.backend.security.CurrentUser;
import com.devpilotfml.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final CurrentUser currentUser;
    private final ChatService chatService;

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponseDto> createSession(@Valid @RequestBody CreateChatSessionRequestDto createChatSessionRequestDto) {
        UUID userId = currentUser.require().getId();
        ChatSessionResponseDto chatSessionResponseDto = chatService.createSession(userId, createChatSessionRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatSessionResponseDto);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponseDto>> listSessions(@RequestParam UUID repositoryId) {
        UUID userId = currentUser.require().getId();
        List<ChatSessionResponseDto> chatSessions = chatService.listSessions(userId, repositoryId);

        return ResponseEntity.ok(chatSessions);
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(@PathVariable UUID id) {
        List<ChatMessageResponseDto> chatMessages = chatService.getMessages(id);

        return ResponseEntity.ok(chatMessages);
    }

    @PostMapping(value = "/sessions/{id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sendMessage(@PathVariable UUID id, @Valid @RequestBody ChatMessageRequestDto chatMessageRequestDto) {
        UUID userId = currentUser.require().getId();
        SseEmitter sseEmitter = chatService.streamReply(userId, id, chatMessageRequestDto.content());

        return ResponseEntity.ok(sseEmitter);
    }
}
