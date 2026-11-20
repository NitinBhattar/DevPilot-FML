package com.devpilotfml.backend.security;

import com.devpilotfml.backend.entity.User;
import com.devpilotfml.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Helps in fetching data from GitHub and extracting info
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Value("${spring.security.oauth2.client.registration.github.scope}")
    private String defaultScope;

    @Override
    public @Nullable OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User githubUser = delegate.loadUser(userRequest);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        String scopes = (userRequest.getAccessToken().getScopes() != null)
                        ? String.join(",", userRequest.getAccessToken().getScopes())
                        : defaultScope;

        User user = userService.upsertFromGithub(githubUser.getAttributes(), accessToken, scopes);

        // Our own implementation of OAuth2User is AppUserPrincipal
        return new AppUserPrincipal(user, githubUser.getAttributes());
    }
}
