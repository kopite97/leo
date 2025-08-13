package com.example.leo.config.filter;

import com.example.leo.general.jwt.JwtProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String auth = request.getHeader("Authorization");
        if(StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if(jwtProvider.validate(token)){
                var authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}
