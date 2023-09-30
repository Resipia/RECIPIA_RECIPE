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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;

    /**
     * 문자열 리터럴은 상수로 선언하여 사용하는 것이 좋다. 이렇게 하면 코드의 가독성이 향상되며, 나중에 변경이 필요할 때 한 곳에서만 수정하면 된다.
     */
    private static final String ACCESS_TOKEN_TYPE = "access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix

//            if (validateToken(token)) {
//                UsernamePasswordAuthenticationToken authenticationToken = extractUserDetails(token);
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
            if (validateToken(token)) {
                // Extract claims from the token
                Map<String, Object> mapClaims = TokenUtils.getClaimsMapFromToken(token);  // Replace this with your token extraction logic

                // Create Jwt object
                String finalToken = token;
                Jwt jwt = Jwt.withTokenValue(token)
                        .header("typ", "JWT")
                        .claims(claims -> claims.putAll(TokenUtils.getClaimsMapFromToken(finalToken)))
                        .build();

                // Create UsernamePasswordAuthenticationToken
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwt, null, extractAuthorities(token));

                // Set it in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 토큰의 유효성을 검증하는 메서드
    private boolean validateToken(String token) {
        return tokenValidator.isValidToken(token, ACCESS_TOKEN_TYPE);
    }

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
