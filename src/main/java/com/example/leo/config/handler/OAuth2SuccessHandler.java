package com.example.leo.config.handler;

import com.example.leo.auth.entity.OAuth2UserInfo;
import com.example.leo.auth.entity.SimpleOAuth2User;
import com.example.leo.general.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        SimpleOAuth2User principal = (SimpleOAuth2User) authentication.getPrincipal();
        OAuth2UserInfo info = principal.getUserInfo();

        String jwt = jwtProvider.createToken(
                info.getId(),
                authentication.getAuthorities(),
                Map.of("email", info.getEmail(), "name", info.getName(), "provider", info.getProvider())
        );

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String body = """
                {"accessToken":"%s","tokenType":"Bearer","user":{"id":"%s","email":"%s","name":"%s","provider":"%s"}}
                """.formatted(jwt, info.getId(), info.getEmail(), info.getName(), info.getProvider());
        response.getWriter().write(body);
    }
}
