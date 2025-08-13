package com.example.leo.auth.entity;

import java.util.HashMap;
import java.util.Map;

public record KakaoUserInfo(String id, String email, String name) implements OAuth2UserInfo {

    public static KakaoUserInfo from(Map<String,Object> attrs){

        String id = String.valueOf(attrs.get("id"));

        Map<String, Object> account = (Map<String, Object>) attrs.getOrDefault("kakao_account", Map.of());
        String email = (String) account.get("email");

        Map<String,Object> profile = (Map<String, Object>) account.getOrDefault("profile", Map.of());
        String nickname = (String) profile.get("nickname");

        return new KakaoUserInfo(id, email, nickname);
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
        return "kakao";
    }
}
