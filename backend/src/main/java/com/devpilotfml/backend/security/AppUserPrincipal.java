package com.devpilotfml.backend.security;

import com.devpilotfml.backend.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class AppUserPrincipal implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    // ctor
    public AppUserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public UUID getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }

    @Override
    public String getName() {
        return user.getId().toString();
    }
}
