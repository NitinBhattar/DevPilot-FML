package com.devpilotfml.backend.service.impl;

import com.devpilotfml.backend.dto.RepoResponseDto;
import com.devpilotfml.backend.entity.User;
import com.devpilotfml.backend.exception.NotFoundException;
import com.devpilotfml.backend.repository.RepoRepository;
import com.devpilotfml.backend.repository.UserRepository;
import com.devpilotfml.backend.service.RepoService;
import com.devpilotfml.backend.service.UserService;
import com.devpilotfml.backend.service.github.GithubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {
    private final UserRepository userRepository;
    private final RepoRepository repoRepository;
    private final UserService userService;
    private final GithubApiClient githubApiClient;

    public List<RepoResponseDto> syncAndListRepo(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found for id: " + userId));

        String accessToken = userService.decryptAccessToken(user.getAccessToken());

        List<Map<String, Object>> remoteRepos= githubApiClient.listUserRepo(accessToken);


    }
}
