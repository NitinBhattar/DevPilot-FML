package com.devpilotfml.backend.repository;

import com.devpilotfml.backend.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepoRepository extends JpaRepository<Repo, UUID> {
    Optional<List<Repo>> findByUserIdOrderByFullNameAsc(UUID userId);
    Optional<Repo> findByIdAndUserId(UUID id, UUID userId);
    Optional<Repo> findByUserIdAndGithubRepoId(UUID userId, Long githubRepoId);
}
