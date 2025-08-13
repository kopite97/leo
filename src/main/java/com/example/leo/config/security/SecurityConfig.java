package com.example.leo.config.security;

import com.example.leo.auth.service.OAuth2UserServiceImpl;
import com.example.leo.config.filter.JwtAuthenticationFilter;
import com.example.leo.config.handler.OAuth2SuccessHandler;
import com.example.leo.general.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtProvider jwtProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/health").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        // SecurityConfig
        http.oauth2Login(o -> o
                .userInfoEndpoint(u -> u.userService(oAuth2UserService))   // 연결 OK
                .successHandler(oAuth2SuccessHandler)
                .failureHandler((req, res, ex) -> {
                    ex.printStackTrace();                     // 콘솔에 실패 원인 전체 출력
                    res.sendRedirect("/login?oauth2_error");  // 어디서 실패했는지 UI로도 보이게
                })
        );

        //jwt필터 (OAUth2 성공 후 다음 요청부터 토큰 검증
        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
