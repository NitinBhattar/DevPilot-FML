package com.devpilotfml.backend.service.impl;

import com.devpilotfml.backend.entity.User;
import com.devpilotfml.backend.mapper.GithubUserMapper;
import com.devpilotfml.backend.repository.UserRepository;
import com.devpilotfml.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.devpilotfml.backend.util.Conversions.toLong;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final GithubUserMapper githubUserMapper;
    private final UserRepository userRepository;
    private final TextEncryptor textEncryptor;

    @Override
    public User upsertFromGithub(Map<String, Object> attributes, String accessToken, String scopes) {
        Long githubId = toLong(attributes.get("id"));
        User user = userRepository.findByGithubId(githubId).orElseGet(User::new);

        // Mapping attributes
        githubUserMapper.updateEntityFromAttributes(attributes, user);

        // Setting other fields
        user.setAccessToken(textEncryptor.encrypt(accessToken));
        user.setTokenScope(scopes);

        return userRepository.save(user);
    }

    @Override
    public String decryptAccessToken(User user) {
        return textEncryptor.decrypt(user.getAccessToken());
    }
}
