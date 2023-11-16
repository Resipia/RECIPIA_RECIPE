package com.recipia.recipe.config;

import brave.Span;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import brave.Tracer;

@RequiredArgsConstructor
@Configuration
public class FeignClientConfig {

    private final Tracer tracer;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    String traceId = currentSpan.context().traceIdString();
                    // TraceID를 HTTP 요청 헤더에 추가
                    template.header("X-Trace-Id", traceId);
                }
            }
        };
    }

}
