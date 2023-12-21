package com.recipia.recipe.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomJsonBuilder {

    private Map<String, Object> values = new HashMap<>();
    private final ObjectMapper objectMapper;

    public CustomJsonBuilder add(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public String build() throws JsonProcessingException {
        return objectMapper.writeValueAsString(values);
    }
    
}
