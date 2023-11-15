package com.recipia.recipe.aws;

import brave.Span;
import brave.Tracer;
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
    private final Tracer tracer;
    private final ApplicationEventPublisher eventPublisher;

    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveMessage(String messageJson) throws JsonProcessingException {

        JsonNode messageNode = objectMapper.readTree(messageJson);
        String messageId = messageNode.get("MessageId").asText();  // 메시지 ID 추출

        Span newSpan = tracer.nextSpan().name(messageId).start(); // Span 이름을 메시지 ID로 설정
        newSpan.tag("messageId", messageId); // messageId 태그 추가
        newSpan.tag("consumer", "RECIPE"); // consumer 태그 추가

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan)) {
            // SQS 메시지 처리 로직
            String topicArn = messageNode.get("TopicArn").asText();
            String messageContent = messageNode.get("Message").asText();

            log.info("[RECIPE] Received message from SQS with messageId: {}", messageId);

            // Assuming the "Message" is also a JSON string, we parse it to print as JSON object
            JsonNode message = objectMapper.readTree(messageContent);
            log.info("Message:  {}", message.toString());

            // memberId 추출후 이벤트 발행
            JsonNode node = objectMapper.readTree(message.toString());
            Long memberId = Long.valueOf(node.get("memberId").asText());
            eventPublisher.publishEvent(new NicknameChangeEvent(memberId));

        } finally {
            newSpan.finish();
        }

    }

}
