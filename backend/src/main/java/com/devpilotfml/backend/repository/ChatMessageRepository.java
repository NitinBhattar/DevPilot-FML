package com.devpilotfml.backend.repository;

import com.devpilotfml.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Optional<List<ChatMessage>> findBySessionIdOrderByCreatedAt(UUID sessionId);
}
