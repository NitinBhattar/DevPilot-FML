package com.devpilotfml.backend.controller;

import com.devpilotfml.backend.dto.IndexStatusResponseDto;
import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.security.CurrentUser;
import com.devpilotfml.backend.service.RepoService;
import com.devpilotfml.backend.service.indexing.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
public class RepoController {
    private final CurrentUser currentUser;
    private final RepoService repoService;
    private final IndexingService indexingService;

    @GetMapping
    public ResponseEntity<List<RepoResponseDto>> listRepos(@RequestParam(name = "refresh", defaultValue = "true") boolean refresh) {
        UUID userId = currentUser.require().getId();
        List<RepoResponseDto> repos;

        if (refresh) {
            repos = repoService.syncAndListRepos(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(repos);
        }

        repos = repoService.repoSortedList(userId);

        return ResponseEntity.ok(repos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepoResponseDto> getRepo(@PathVariable UUID id) {
        UUID userId = currentUser.require().getId();
        RepoResponseDto repoResponseDto = repoService.requiredRepo(id, userId);

        return ResponseEntity.ok(repoResponseDto);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<IndexStatusResponseDto> getIndexStatus(@PathVariable UUID id) {
        UUID userId = currentUser.require().getId();
        IndexStatusResponseDto indexStatusResponseDto = repoService.indexStatus(id, userId);

        return ResponseEntity.ok(indexStatusResponseDto);
    }

    @PostMapping("/{id}/index")
    public ResponseEntity<RepoResponseDto> index(@PathVariable UUID id) {
        UUID userId = currentUser.require().getId();

        // For frontend message
        RepoResponseDto repoResponseDto = indexingService.startIndexing(id, userId);

        // Background
        indexingService.indexAsync(id, userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repoResponseDto);
    }
}
