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

        // 이전 서버에서 보낸 traceId를 사용하여 새로운 TraceContext를 생성한다.
        TraceContext context = buildTraceContext(traceId);

        // 이 Span은 이전 서버에서 생성된 traceId를 사용하고, 새로운 spanId를 갖게 된다.
        Span span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context))
                .name("[RECIPE] nickname-change SQS Received") // Span 이름 지정
                .start();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            processNicknameMessage(message);
        } catch (Exception e) {
            span.tag("error", e.toString());
            log.error("Error processing SQS message: ", e);
        } finally {
            span.finish();
        }

    }

    private String extractTraceIdFromMessage(JsonNode message) {
        // 메시지로부터 traceId를 추출합니다.
        return message.get("traceId").asText();
    }

    private TraceContext buildTraceContext(String traceId) {
        // 여기서 contextBuilder를 만들고 이 builder안에 멤버 서버에서 받은 traceId를 세팅해 준다.
        TraceContext.Builder contextBuilder = TraceContext.newBuilder();

        // traceId의 길이에 따라 처리
        // 32자리인 경우 128비트 traceId로 처리하고, 그렇지 않은 경우 64비트 traceId로 처리
        if (traceId.length() == 32) {
            long traceIdHigh = Long.parseUnsignedLong(traceId.substring(0, 16), 16);
            long traceIdLow = Long.parseUnsignedLong(traceId.substring(16), 16);
            contextBuilder.traceIdHigh(traceIdHigh).traceId(traceIdLow);
        } else {
            long traceIdLow = Long.parseUnsignedLong(traceId, 16);
            contextBuilder.traceId(traceIdLow);
        }

        // 새로운 Span ID를 생성
        contextBuilder.spanId(tracer.nextSpan().context().spanId());

        // TraceContext 객체를 빌드하고 반환
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