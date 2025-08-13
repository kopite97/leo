package com.example.leo.auth.entity;

public interface OAuth2UserInfo {
    String getId();
    String getEmail();
    String getName();
    String getProvider(); // "Kakao" or "Naver"
}
