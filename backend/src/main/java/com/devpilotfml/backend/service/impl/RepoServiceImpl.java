package com.devpilotfml.backend.service.impl;

import com.devpilotfml.backend.dto.IndexStatusResponseDto;
import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.Repo;
import com.devpilotfml.backend.entity.User;
import com.devpilotfml.backend.exception.NotFoundException;
import com.devpilotfml.backend.mapper.IndexStatusMapper;
import com.devpilotfml.backend.mapper.RepoMapper;
import com.devpilotfml.backend.repository.RepoRepository;
import com.devpilotfml.backend.service.RepoService;
import com.devpilotfml.backend.service.UserService;
import com.devpilotfml.backend.service.github.GithubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.devpilotfml.backend.util.Conversions.toLong;

@Service
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {
    private final RepoRepository repoRepository;
    private final UserService userService;
    private final GithubApiClient githubApiClient;
    private final RepoMapper repoMapper;
    private final IndexStatusMapper indexStatusMapper;

    @Override
    public List<RepoResponseDto> syncAndListRepos(UUID userId) {
        // Find user
        User user = userService.requiredUser(userId);

        // Access token
        String accessToken = userService.decryptAccessToken(user.getAccessToken());
        // Fetch repos
        List<Map<String, Object>> remoteRepos = githubApiClient.listUserRepo(accessToken);

        List<RepoResponseDto> repoResponseDtoList = new ArrayList<>(remoteRepos.size());

        for (final Map<String, Object> remote: remoteRepos) {
            // New or updating existing repo
            Long githubRepoId = toLong(remote.get("id"));
            Repo repo = repoRepository.findByUserIdAndGithubRepoId(userId, githubRepoId).orElseGet(Repo::new);

            // Map to Entity
            repoMapper.toEntity(userId, remote, repo);

            // Saving/updating in repo
            Repo savedRepo = repoRepository.save(repo);

            // Entity -> Dto
            repoResponseDtoList.add(repoMapper.toDto(savedRepo));
        }

        return repoResponseDtoList;
    }

    @Override
    public List<RepoResponseDto> repoSortedList(UUID userId) {
        // Find user
        List<Repo> repoList = repoRepository.findByUserIdOrderByFullNameAsc(userId).orElseThrow(() -> new NotFoundException("No repository found for this user"));

        List<RepoResponseDto> repoResponseDtoList = new ArrayList<>(repoList.size());

        for (final Repo repo: repoList) {
            repoResponseDtoList.add(repoMapper.toDto(repo));
        }

        return repoResponseDtoList;
    }

    @Override
    public Repo requiredRepoEntity(UUID repoId, UUID userId) {
        return repoRepository.findByIdAndUserId(repoId, userId).orElseThrow(() -> new NotFoundException("Repositories not found"));
    }

    @Override
    public RepoResponseDto requiredRepoDto(UUID repoId, UUID userId) {
        Repo repo = repoRepository.findByIdAndUserId(repoId, userId).orElseThrow(() -> new NotFoundException("Repositories not found"));
        return repoMapper.toDto(repo);
    }

    @Override
    public IndexStatusResponseDto indexStatus(UUID repoId, UUID userId) {
        Repo repo = repoRepository.findByIdAndUserId(repoId, userId).orElseThrow(() -> new NotFoundException("Repositories not found"));
        return indexStatusMapper.toDto(repo);
    }
}
