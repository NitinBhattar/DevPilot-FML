package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.Repo;
import com.devpilotfml.backend.repository.RepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.devpilotfml.backend.util.Conversions.toLong;

@Component
@RequiredArgsConstructor
public class GithubRepoMapper {
    private final RepoRepository repoRepository;
    private final RepoMapper repoMapper;

    public List<RepoResponseDto> toDtoList(UUID userId, List<Map<String, Object>> remoteRepos) {
        List<RepoResponseDto> repoResponseDtoList = new ArrayList<>(remoteRepos.size());

        for (final Map<String, Object> remote: remoteRepos) {
            Long githubRepoId = toLong(remote.get("id"));
            Repo repo = repoRepository.findByUserIdAndGithubRepoId(userId, githubRepoId).orElseGet(Repo::new);

            String fullName = String.valueOf(remote.get("full_name"));
            String[] parts = fullName.split("/", 2);

            repo.setUserId(userId);
            repo.setGithubRepoId(githubRepoId);
            repo.setOwner(parts.length > 0 ? parts[0] : String.valueOf(remote.get("owner")));
            repo.setName(parts.length > 1 ? parts[1] : String.valueOf(remote.get("name")));
            repo.setFullName(fullName);
            repo.setPrivate(Boolean.TRUE.equals(remote.get("private")));
            repo.setDefaultBranch(remote.get("default_branch") != null ? String.valueOf(remote.get("default_branch")) : "main");
            repo.setLanguage(remote.get("language") != null ? String.valueOf(remote.get("language")) : null);
            repo.setHtmlUrl(remote.get("html_url") != null ? String.valueOf(remote.get("html_url")) : null);
            repo.setDescription(remote.get("description") != null ? String.valueOf(remote.get("description")) : null);

            if (repo.getOwner() == null || repo.getOwner().isBlank()) {
                Object ownerObj = remote.get("owner");

                if (ownerObj instanceof Map<?, ?> ownerMap && ownerMap.get("login") != null) {
                    repo.setOwner(String.valueOf(ownerMap.get("login")));
                }
            }

            Repo savedRepo = repoRepository.save(repo);

            repoResponseDtoList.add(repoMapper.toDto(savedRepo));
        }

        return repoResponseDtoList;
    }
}
