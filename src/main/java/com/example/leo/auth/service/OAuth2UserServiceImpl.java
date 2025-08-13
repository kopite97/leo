package com.example.leo.auth.service;

import com.example.leo.auth.entity.KakaoUserInfo;
import com.example.leo.auth.entity.NaverUserInfo;
import com.example.leo.auth.entity.OAuth2UserInfo;
import com.example.leo.auth.entity.SimpleOAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        log.info("OAuth2UserServiceImpl.loadUser: registrationId={}",
                req.getClientRegistration().getRegistrationId());  // ★ 호출 확인

        var delegate = new DefaultOAuth2UserService();
        var oAuth2User = delegate.loadUser(req);

        String registrationId = req.getClientRegistration().getRegistrationId();
        Map<String,Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = switch (registrationId) {
            case "kakao" -> KakaoUserInfo.from(attributes);
            case "naver" -> NaverUserInfo.from(attributes);
            default -> throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"), "Unsupported provider");
        };

        return new SimpleOAuth2User(userInfo);
    }
}
