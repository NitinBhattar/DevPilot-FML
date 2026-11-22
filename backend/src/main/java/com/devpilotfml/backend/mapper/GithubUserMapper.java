package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.entity.User;
import org.mapstruct.Mapper;

import java.util.Map;

import static com.devpilotfml.backend.util.Conversions.toLong;

@Mapper
public interface GithubUserMapper {
    default void updateEntityFromAttributes(Map<String, Object> attributes, User user) {
        // Extracting from attributes
        Long githubId = toLong(attributes.get("id"));
        String githubUsername = String.valueOf(attributes.get("login"));
        String displayName = (attributes.get("login") != null) ? String.valueOf(attributes.get("login")) : "login";
        String avatarUrl = (attributes.get("avatar_url") != null) ? String.valueOf(attributes.get("avatar_url")) : null;

        // Changing in user
        user.setGithubId(githubId);
        user.setGithubUsername(githubUsername);
        user.setDisplayName(displayName);
        user.setAvatarUrl(avatarUrl);
    }
}
