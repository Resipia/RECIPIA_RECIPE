package com.recipia.recipe.config;

import brave.Tracer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public  class TestZipkinConfig {

    @Bean
    public Tracer tracer() {
        // Tracer의 목 객체 또는 테스트용 구현을 반환
        return Mockito.mock(Tracer.class);
    }
}
