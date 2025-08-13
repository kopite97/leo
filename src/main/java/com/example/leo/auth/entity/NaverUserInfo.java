package com.example.leo.auth.entity;

import java.util.HashMap;
import java.util.Map;

public record NaverUserInfo(String id, String email, String name) implements OAuth2UserInfo {

    public static NaverUserInfo from(Map<String,Object> attrs) {

        Map<String, Object> response = (Map<String, Object>) attrs.getOrDefault("response", Map.of());
        String id = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        return new NaverUserInfo(id, email, name);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}
