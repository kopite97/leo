package com.example.leo.auth.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public record KakaoUserInfo(String id,String nickname) implements OAuth2UserInfo {

    public static KakaoUserInfo from(Map<String,Object> attrs){
        // 1) id
        String id = String.valueOf(attrs.get("id"));

        // 2) kakao_account 안전 추출
        Map<String, Object> account =
                (Map<String, Object>) attrs.getOrDefault("kakao_account", Map.of());

        // 3) profile 안전 추출
        Map<String, Object> profile =
                (Map<String, Object>) account.getOrDefault("profile", Map.of());

        // 4) 닉네임 우선순위: kakao_account.profile.nickname → (fallback) properties.nickname
        String nickname = (String) profile.get("nickname");
        if (nickname == null) {
            Map<String, Object> properties =
                    (Map<String, Object>) attrs.getOrDefault("properties", Map.of());
            nickname = (String) properties.get("nickname");
        }

        // 5) (선택) 이메일 필요시: account_email을 scope/콘솔에서 허용해야 응답에 존재
        // String email = (String) account.get("email");

        return new KakaoUserInfo(id, nickname /*, email*/);
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
        return "kakao";
    }
}
