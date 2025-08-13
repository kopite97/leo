package com.example.leo.auth.entity;

import java.util.HashMap;
import java.util.Map;

public record NaverUserInfo(String id,String nickname) implements OAuth2UserInfo {

    public static NaverUserInfo from(Map<String,Object> attrs) {

        Map<String, Object> response = (Map<String, Object>) attrs.getOrDefault("response", Map.of());
        String id = (String) response.get("id");
        String nickname = (String) response.get("name");
        return new NaverUserInfo(id,nickname);
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}
