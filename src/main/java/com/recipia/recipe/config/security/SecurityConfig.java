package com.recipia.recipe.config.security;

import com.recipia.recipe.config.security.filter.JwtAuthorizationFilter;
import com.recipia.recipe.config.security.jwt.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final TokenValidator tokenValidator;

    // JWT 토큰 생성을 위한 비밀키 선언
    private static final String jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits";
    SecretKeySpec hmacSecretKey = new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), "HMACSHA256");


    /**
     * 이 메서드는 정적 자원에 대해 보안을 적용하지 않도록 설정한다.
     * 정적 자원은 보통 HTML, CSS, JavaScript, 이미지 파일 등을 의미하며, 이들에 대해 보안을 적용하지 않음으로써 성능을 향상시킬 수 있다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/resources/**"),
                                new AntPathRequestMatcher("/health"),        // ALB에서 상태 검사용으로 들어온 '/health' 경로에 대한 접근을 허용
                                new AntPathRequestMatcher("/actuator/*"),    // actuator 하위의 prometheus 접속 경로
                                new AntPathRequestMatcher("/actuator")       // spring actuator 수집 경로
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    /**
     * "JWT 토큰을 통해 사용자를 인증한다." -> 이 메서드는 JWT 인증 필터를 생성한다.
     * JWT 인증 필터는 요청 헤더의 JWT 토큰을 검증하고, 토큰이 유효하면 토큰에서 사용자의 정보와 권한을 추출하여 SecurityContext에 저장한다.
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(tokenValidator);
    }

    /**
     * JWT 디코더를 생성하는 메서드.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(hmacSecretKey).build();
    }
}
