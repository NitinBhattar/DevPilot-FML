package com.devpilotfml.backend.entity;

import com.devpilotfml.backend.enums.IndexStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "repositories", indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "github_repo_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "github_repo_id", nullable = false)
    private Long githubRepoId;

    @Column(nullable = false, length = 200)
    private String owner;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "default_branch", nullable = false)
    private String defaultBranch;

    private String language;

    @Column(name = "html_url")
    private String htmlUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "index_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IndexStatus indexStatus = IndexStatus.PENDING;

    @Column(name = "chunk_count", nullable = false)
    @Builder.Default
    private int chunkCount = 0;

    @Column(name = "files_total", nullable = false)
    @Builder.Default
    private long filesTotal = 0L;

    @Column(name = "files_processed", nullable = false)
    @Builder.Default
    private long filesProcessed = 0L;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "indexed_at", nullable = false)
    private Instant indexedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
