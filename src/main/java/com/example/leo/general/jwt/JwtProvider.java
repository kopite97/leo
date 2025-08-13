package com.example.leo.general.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private String secret;

    private long validityInSeconds;

    private String issuer;

    private Key key;

    @PostConstruct
    void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String subject, Collection<? extends GrantedAuthority> authorities, Map<String, Object> claims) {

        Date now = new Date();
        Date exp = new Date(now.getTime()+(validityInSeconds*1000));

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .addClaims(claims == null ? Map.of() : claims)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> auth = roles == null ? List.of() :
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(subject, token, auth);
    }

    public boolean validate(String token){
        try{
            parseClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


}
