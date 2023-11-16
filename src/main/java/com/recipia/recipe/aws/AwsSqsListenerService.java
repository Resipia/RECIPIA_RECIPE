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
import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSqsListenerService {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Tracer tracer;

    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveMessage(String messageJson) throws JsonProcessingException {
        JsonNode messageNode = objectMapper.readTree(messageJson);
        String messageId = messageNode.get("MessageId").asText();
        String messageContent = messageNode.get("Message").asText();

        log.info("[RECIPE] Received message from SQS with messageId: {}", messageId);

        JsonNode message = objectMapper.readTree(messageContent);
        String traceId = extractTraceIdFromMessage(message);

        TraceContext context = buildTraceContext(traceId);

        Span span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context))
                .name("Process SQS Message") // Span 이름 지정
                .start();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            processNicknameMessage(message);
        } finally {
            span.finish();
        }
    }

    private String extractTraceIdFromMessage(JsonNode message) {
        return message.get("traceId").asText();
    }

    private TraceContext buildTraceContext(String traceId) {
        TraceContext.Builder contextBuilder = TraceContext.newBuilder();
        if (traceId.length() == 32) {
            long traceIdHigh = Long.parseUnsignedLong(traceId.substring(0, 16), 16);
            long traceIdLow = Long.parseUnsignedLong(traceId.substring(16), 16);
            contextBuilder.traceIdHigh(traceIdHigh).traceId(traceIdLow);
        } else {
            long traceIdLow = Long.parseUnsignedLong(traceId, 16);
            contextBuilder.traceId(traceIdLow);
        }
        // 새로운 Span ID 생성 (tracer 인스턴스를 사용)
        contextBuilder.spanId(tracer.nextSpan().context().spanId());

        return contextBuilder.build();
    }

    private void processNicknameMessage(JsonNode message) throws JsonProcessingException {
        // 'message' 필드 내의 JSON 문자열을 추출
        String messageContent = message.get("message").asText();

        // 추출된 JSON 문자열을 파싱하여 memberId를 얻음
        JsonNode innerMessageNode = objectMapper.readTree(messageContent);
        Long memberId = Long.valueOf(innerMessageNode.get("memberId").asText());

        // 추출된 memberId로 이벤트 발행 및 로깅
        eventPublisher.publishEvent(new NicknameChangeEvent(memberId));
        log.info("Processed NicknameChangeEvent for memberId: {}", memberId);
    }
}
