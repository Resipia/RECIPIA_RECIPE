package com.recipia.recipe.hexagonal.config.aop;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // 서울 시간대로 변경하기
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
