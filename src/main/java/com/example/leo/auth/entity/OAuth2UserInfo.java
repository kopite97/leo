package com.example.leo.auth.entity;

public interface OAuth2UserInfo {
    String getId();
    String getNickname();
    String getProvider(); // "Kakao" or "Naver"
}
