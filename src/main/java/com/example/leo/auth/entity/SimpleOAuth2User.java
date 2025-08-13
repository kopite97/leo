package com.example.leo.auth.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleOAuth2User implements OAuth2User {

    private final OAuth2UserInfo info;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "id", info.getId(),
                "email", info.getEmail(),
                "name", info.getName(),
                "provider", info.getProvider()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * return Id
     * @return
     */
    @Override
    public String getName() {
        return info.getId();
    }

    public OAuth2UserInfo getUserInfo() {
        return info;
    }
}
