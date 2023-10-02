package com.recipia.recipe.config.filter;

import com.recipia.recipe.config.jwt.TokenUtils;
import com.recipia.recipe.config.jwt.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;

    // 상수로 선언된 액세스 토큰 유형
    private static final String ACCESS_TOKEN_TYPE = "access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 클라이언트로부터 전달받은 Authorization 헤더값을 추출
        String token = request.getHeader("Authorization");

        // 토큰이 Bearer 스키마를 따르는지 확인
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두어 제거

            // 토큰 유효성 검사
            if (validateToken(token)) {
                // 토큰에서 클레임 추출
                Map<String, Object> claimsMap = TokenUtils.getClaimsMapFromToken(token);  // Replace this with your token extraction logic
                Instant expiration = TokenUtils.getExpirationFromToken(token);
                Long memberId = TokenUtils.getMemberIdFromToken(token);

                // Jwt 객체 생성
                Jwt jwt = Jwt.withTokenValue(token)
                        .header("typ", "JWT")
                        .claims(claims -> claims.putAll(claimsMap))
                        .claim("exp", expiration)
                        .claim("memberId", memberId)
                        .build();

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwt, null, extractAuthorities(token));

                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    // 토큰의 유효성을 검증하는 메서드
    private boolean validateToken(String token) {
        return tokenValidator.isValidToken(token, ACCESS_TOKEN_TYPE);
    }

    // 토큰에서 권한 정보를 추출
    private List<SimpleGrantedAuthority> extractAuthorities(String token) {
        String role = TokenUtils.getRoleFromToken(token);  // Replace this with your token extraction logic
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    // 토큰에서 사용자 정보를 추출하여 UsernamePasswordAuthenticationToken 객체로 반환하는 메서드
//    private UsernamePasswordAuthenticationToken extractUserDetails(String token) {
//        // JWT 토큰에서 사용자 정보를 추출
//        String username = TokenUtils.getUsernameFromToken(token);
//        // 사용자의 권한을 토큰에서 직접 추출
//        String role = TokenUtils.getRoleFromToken(token);
//        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
//
//        return new UsernamePasswordAuthenticationToken(username, null, authorities);
//    }

}
