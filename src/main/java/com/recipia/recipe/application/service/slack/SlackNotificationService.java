package com.recipia.recipe.application.service.slack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class SlackNotificationService {

    @Value("${slack.web-hook-url}")
    private String slackWebhookUrl;
    private final RestTemplate restTemplate;

    /**
     * 카프카 리스너에서 호출해서 slack에 에러 메시지를 보낸다.
     */
    public void sendMessageToSlack(String message) {
        String payload = "{\"text\": \"" + message + "\"}";
        restTemplate.postForEntity(slackWebhookUrl, payload, String.class);
        log.info("slack에 에러 메시지 발행 완료");
    }

}


