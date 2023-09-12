package com.recipia.recipe.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RecipeKafkaConsumer {

    @KafkaListener(topics = "receive-response", groupId = "recipia")
    public void listen(String message) {
        System.out.println("Received Response: " + message);
    }


}
