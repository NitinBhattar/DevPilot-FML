package com.devpilotfml.backend.service;

import com.devpilotfml.backend.entity.User;

import java.util.Map;

public interface UserService {
    public User upsertFromGithub(Map<String, Object> attributes, String accessToken, String scopes);
    public String decryptAccessToken(User user);
}
