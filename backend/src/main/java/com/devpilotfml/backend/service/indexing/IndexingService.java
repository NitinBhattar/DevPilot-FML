package com.devpilotfml.backend.service.indexing;

import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.Repo;
import com.devpilotfml.backend.enums.IndexStatus;
import com.devpilotfml.backend.exception.BadRequestException;
import com.devpilotfml.backend.exception.NotFoundException;
import com.devpilotfml.backend.mapper.IndexStatusMapper;
import com.devpilotfml.backend.mapper.RepoMapper;
import com.devpilotfml.backend.repository.RepoRepository;
import com.devpilotfml.backend.service.UserService;
import com.devpilotfml.backend.service.ai.RagSettings;
import com.devpilotfml.backend.service.github.GithubApiClient;
import com.devpilotfml.backend.service.github.GithubRateLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.devpilotfml.backend.util.Conversions.toLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingService {
    private static final int VECTOR_BATCH_SIZE = 32;
    private static final int PROGRESS_EVERY_N_FILES = 5;

    @Value("${app.indexing.max-file-bytes}")
    private long maxFileBytes;

    private final RepoRepository repoRepository;
    private final UserService userService;
    private final RepoMapper repoMapper;
    private final GithubApiClient githubApiClient;
    private final CodeFileFilter codeFileFilter;
    private final CodeChunker codeChunker;
    private final GithubRateLimiter githubRateLimiter;
    private final VectorStore vectorStore;

    public RepoResponseDto startIndexing(UUID repoId, UUID userId) {
        Repo repo = repoRepository.findByIdAndUserId(repoId, userId).orElseThrow(() -> new NotFoundException("Repository not found"));

        if (repo.getIndexStatus() == IndexStatus.INDEXING) {
            throw new BadRequestException("Repository is already being indexed");
        }

        // Resetting if failed
        repo.setFilesTotal(0);
        repo.setFilesProcessed(0);
        repo.setChunkCount(0);
        repo.setIndexStatus(IndexStatus.INDEXING);
        repo.setErrorMessage(null);

        Repo savedRepo = repoRepository.save(repo);

        return repoMapper.toDto(repo);
    }

    @Async("indexingExecutor")
    public void indexAsync(UUID repoId, UUID userId) {
        try {
            doIndex(repoId, userId);
        } catch (Exception e) {
            markFailed(repoId, e.getMessage());
        }
    }

    private void doIndex(UUID repoId, UUID userId) {
        Repo repo = repoRepository.findByIdAndUserId(repoId, userId).orElseThrow(() -> new NotFoundException("Repository not found"));

        String accessToken = userService.decryptAccessToken(userService.requiredUser(userId).getAccessToken());

        // Remove old data
        deleteExistingVectors(repoId.toString());

        // Repo tree fetching
        Map<String, Object> tree = githubApiClient.getRepoTree(accessToken, repo.getOwner(), repo.getName(), repo.getDefaultBranch());

        // All file paths
        List<String> filePaths = listIndexableFiles(tree);

        // Progress update
        updateProgress(repoId, filePaths.size(), 0, 0, IndexStatus.INDEXING, null);

        // Batch to be processed
        List<Document> batch = new ArrayList<>();
        int processed = 0;
        int totalChunks = 0;

        for (final String path: filePaths) {
            try {
                // Converting into chunks and document
                String content = githubApiClient.getFileContent(accessToken, repo.getOwner(), repo.getName(), path);

                List<Document> chunks = codeChunker.chunkFile(repoId.toString(), path, content);

                totalChunks += chunks.size();
                batch.addAll(chunks);

                // Processing batch if full
                if (batch.size() >= VECTOR_BATCH_SIZE) {
                    vectorStore.add(batch);
                    batch.clear();
                }
            } catch (Exception e) {
                log.warn("Skipping files {} in {}: {}", path, repo.getFullName(), e.getMessage());
            }

            processed++;

            // Updating progress
            if (processed % PROGRESS_EVERY_N_FILES == 0 || processed == filePaths.size()) {
                updateProgress(repoId, filePaths.size(), processed, totalChunks, IndexStatus.INDEXING, null);
            }

            githubRateLimiter.pause();
        }

        // Last batch
        if (!batch.isEmpty()) {
            vectorStore.add(batch);
        }

        // Marking ready
        markReady(repoId, filePaths.size(), processed, totalChunks, repo.getFullName());
    }

    // Only indexable files
    @SuppressWarnings("unchecked")
    private List<String> listIndexableFiles(Map<String, Object> tree) {
        if (tree == null || tree.get("tree") == null) {
            return List.of();
        }

        List<Map<String, Object>> entries = (List<Map<String, Object>>) tree.get("tree");

        return entries.stream()
                .filter(entry -> "blob".equals(String.valueOf(entry.get("type"))))
                .filter(entry -> {
                    String path = String.valueOf(entry.get("path"));
                    long size = toLong(entry.get("size"));

                    return codeFileFilter.isEligible(path, size, maxFileBytes);
                })
                .map(entry ->String.valueOf(entry.get("path")))
                .toList();
    }

    @Transactional
    private void markReady(UUID repoId, int totalFiles, int processedFiles, int totalChunks, String fullName) {
        Repo repo = repoRepository.findById(repoId).orElseThrow(() -> new NotFoundException("Repository not found"));

        repo.setFilesTotal(totalFiles);
        repo.setFilesProcessed(processedFiles);
        repo.setChunkCount(totalChunks);
        repo.setIndexStatus(IndexStatus.READY);
        repo.setIndexedAt(Instant.now());
        repo.setErrorMessage(null);

        repoRepository.save(repo);

        // Log
        log.info("indexed {} files ({} chunks) for {}", processedFiles, totalChunks, fullName);
    }

    @Transactional
    private void markFailed(UUID repoId, String message) {
        Repo repo = repoRepository.findById(repoId).orElseThrow(() -> new NotFoundException("Repository not found"));

        repo.setIndexStatus(IndexStatus.FAILED);
        repo.setErrorMessage((message != null) ? (message.length() > 2000) ? message.substring(0, 2000) : message : "Indexing failed");

        repoRepository.save(repo);

        // Log
        log.error("Indexing failed for repo {}: {}", repoId, message);
    }

    private void deleteExistingVectors(String repoId) {
        try {
            Filter.Expression filter = new FilterExpressionBuilder().eq(RagSettings.METADATA_REPO_ID, repoId).build();
            vectorStore.delete(filter);
        } catch (Exception e) {
            log.warn("Couldn't delete existing vectors for repo {}: {}", repoId, e.getMessage());
        }
    }

    @Transactional
    private void updateProgress(UUID repoId, int totalFiles, int processedFiles, int chunks, IndexStatus indexStatus, String errorMessage) {
        Repo repo = repoRepository.findById(repoId).orElseThrow(() -> new NotFoundException("Repository not found"));

        repo.setFilesTotal(totalFiles);
        repo.setFilesProcessed(processedFiles);
        repo.setChunkCount(chunks);
        repo.setIndexStatus(indexStatus);
        repo.setErrorMessage(errorMessage);

        repoRepository.save(repo);
    }
}
