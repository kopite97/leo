package com.example.leo.config.handler;

import com.example.leo.auth.entity.OAuth2UserInfo;
import com.example.leo.auth.entity.SimpleOAuth2User;
import com.example.leo.general.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object p = authentication.getPrincipal();
        log.info("principal type = {}", p.getClass().getName());  // ★ 타입 확인

        String id=null, nickname=null, provider=null;

        if (p instanceof SimpleOAuth2User u) {                    // 우리가 만든 타입
            OAuth2UserInfo info = u.getUserInfo();
            id = info.getId(); nickname = info.getNickname(); provider = info.getProvider();
        } else if (p instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User u) { // 혹시 기본 타입이면
            var attrs = u.getAttributes();
            if (attrs.get("id") != null) { // kakao
                id = String.valueOf(attrs.get("id"));
                var account = (Map<String,Object>) attrs.getOrDefault("kakao_account", Map.of());
                var profile = (Map<String,Object>) account.getOrDefault("profile", Map.of());
                nickname = (String) profile.get("nickname");
                provider = "kakao";
            } else if (attrs.get("response") instanceof Map<?,?> resp) { // naver
                var r = (Map<?,?>) resp;
                id = (String) r.get("id");
                nickname = (String) r.get("name");
                provider = "naver";
            }
        } else {
            log.error("Unsupported principal type: {}", p.getClass());
            response.sendRedirect("/login?error=unsupported_principal");
            return;
        }

        String jwt = jwtProvider.createToken(id, authentication.getAuthorities(),
                Map.of("name", nickname, "provider", provider));

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("""
      {"accessToken":"%s","tokenType":"Bearer","user":{"id":"%s","nickname":"%s","provider":"%s"}}
    """.formatted(jwt, id, nickname, provider));
    }
}
