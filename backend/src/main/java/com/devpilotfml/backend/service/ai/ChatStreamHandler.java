package com.devpilotfml.backend.service.ai;

import com.devpilotfml.backend.dto.ChatMessageResponseDto;
import com.devpilotfml.backend.dto.CitationDto;
import com.devpilotfml.backend.entity.ChatMessage;
import com.devpilotfml.backend.mapper.ChatMessageMapper;
import com.devpilotfml.backend.mapper.CitationMapper;
import com.devpilotfml.backend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatStreamHandler {
    private final ChatModel chatModel;
    private final ChatMessageRepository chatMessageRepository;
    private final CitationMapper citationMapper;
    private final ChatMessageMapper chatMessageMapper;

    public SseEmitter stream(
            UUID sessionId,
            ChatMessageResponseDto savedUserMessage,
            List<CitationDto> citations,
            String systemPrompt,
            String userPrompt
    ) {
        // SSE (Server-Sent Events)
        SseEmitter emitter = new SseEmitter(RagSettings.STREAM_TIMEOUT_MS);
        StringBuilder fullReply = new StringBuilder();

        try {
            emitter.send(SseEmitter.event()
                    .name("user message")
                    .data(savedUserMessage)
            );

            ChatClient.builder(chatModel)
                    .build()
                    .prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .stream()
                    .content()
                    .doOnNext(token -> appendToken(emitter, fullReply, token))
                    .doOnError(err -> {
                        log.error("Chat stream error", err);
                        emitter.completeWithError(err);
                    })
                    .doOnComplete(() -> completeStream(emitter, sessionId, fullReply, citations))
                    .subscribe();
        } catch (Exception e) {
            log.error("Chat stream error {}", e.getMessage());
            emitter.completeWithError(e);
        }

        return emitter;
    }

    private void appendToken(SseEmitter emitter, StringBuilder fullReply, String token) {
        fullReply.append(token);

        try {
            emitter.send(SseEmitter.event()
                    .name("token")
                    .data(token, MediaType.APPLICATION_JSON)
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void completeStream(SseEmitter emitter, UUID sessionId, StringBuilder fullReply, List<CitationDto> citations) {
        try {
            ChatMessage assistantMessage = chatMessageRepository.save(chatMessageMapper.toAssistantMessage(sessionId, fullReply.toString(), citations));

            emitter.send(SseEmitter.event()
                    .name("assistant_message")
                    .data(chatMessageMapper.toDto(assistantMessage))
            );

            emitter.send(SseEmitter.event()
                    .name("done")
                    .data("[DONE]")
            );

            emitter.complete();
        } catch (Exception e) {
            log.error("Chat stream error {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }
}
