package com.recipia.recipe.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUsername(String username) {
        kafkaTemplate.send("send-username", username);
    }
}
