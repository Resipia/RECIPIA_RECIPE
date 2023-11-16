package com.recipia.recipe.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.event.springevent.NicknameChangeEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSqsListenerService {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveMessage(String messageJson) throws JsonProcessingException {

        JsonNode messageNode = objectMapper.readTree(messageJson);
        String messageId = messageNode.get("MessageId").asText();  // 메시지 ID 추출

        // SQS 메시지 처리 로직
        String messageContent = messageNode.get("Message").asText();

        log.info("[RECIPE] Received message from SQS with messageId: {}", messageId);

        JsonNode message = objectMapper.readTree(messageContent);
        log.info("Message:  {}", message.toString());

        // memberId 추출후 이벤트 발행
        JsonNode node = objectMapper.readTree(message.toString());
        Long memberId = Long.valueOf(node.get("memberId").asText());
        eventPublisher.publishEvent(new NicknameChangeEvent(memberId));
    }

}
