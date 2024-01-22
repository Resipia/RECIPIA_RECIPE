package com.recipia.recipe.adapter.in.listener.aws;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.listener.aws.dto.MessageMemberIdDto;
import com.recipia.recipe.adapter.in.listener.aws.dto.SnsNotificationDto;
import com.recipia.recipe.adapter.in.listener.aws.dto.TraceIdDto;
import com.recipia.recipe.common.event.NicknameChangeEvent;
import com.recipia.recipe.common.event.SignUpEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SqsListenerService {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Tracer tracer;


    /**
     * [SQS 리스너]
     *  유저 닉네임 변경 시 동작
     */
    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveNicknameChangeMessage(String messageJson) throws JsonProcessingException {

        // 1. SNS 메타데이터 정보를 추출해서 dto 객체로 만든다.
        SnsNotificationDto snsNotificationDto = objectMapper.readValue(messageJson, SnsNotificationDto.class);
        TraceIdDto traceIdDto = snsNotificationDto.MessageAttributes().traceId();
        String traceId = traceIdDto.Value();

        // 2. SNS 토픽 이름을 추출하고 span 메시지를 생성한다.
        String topicName = extractSnsTopicName(snsNotificationDto.TopicArn());
        String spanName = String.format("[RECIPE] TopicName: %s SQS Received", topicName);

        // 3. SNS 내부의 메시지를 Dto로 만든다. (이벤트 발행할 때 사용)
        MessageMemberIdDto snsMessageDto = objectMapper.readValue(snsNotificationDto.Message(), MessageMemberIdDto.class);

        // 4. 이전 서버에서 보낸 traceId를 사용하여 새로운 TraceContext를 생성한다.
        TraceContext context = buildTraceContext(traceId);
        Span span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context))
                .name(spanName)
                .start();

        // 5. SNS 메시지에 담겨있던 memberId로 이벤트 발행 및 로깅 진행
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            eventPublisher.publishEvent(new NicknameChangeEvent(snsMessageDto.memberId()));
        } catch (Exception e) {
            span.tag("error", e.toString());
            log.error("Error processing SQS message: ", e);
        } finally {
            span.finish();
        }

    }

    /**
     *
     * [SQS 리스너]
     *  유저 회원가입 시 동작
     */
    @SqsListener(value = "${spring.cloud.aws.sqs.signup-sqs-name}")
    public void receiveNicknameInsertMessage(String messageJson) throws JsonProcessingException {

        // 1. SNS 메타데이터 정보를 추출해서 dto 객체로 만든다.
        SnsNotificationDto snsNotificationDto = objectMapper.readValue(messageJson, SnsNotificationDto.class);
        TraceIdDto traceIdDto = snsNotificationDto.MessageAttributes().traceId();
        String traceId = traceIdDto.Value();

        // 2. SNS 토픽 이름을 추출하고 span 메시지를 생성한다.
        String topicName = extractSnsTopicName(snsNotificationDto.TopicArn());
        String spanName = String.format("[RECIPE] TopicName: %s SQS Received", topicName);

        // 3. SNS 내부의 메시지를 Dto로 만든다. (이벤트 발행할 때 사용)
        MessageMemberIdDto snsMessageDto = objectMapper.readValue(snsNotificationDto.Message(), MessageMemberIdDto.class);

        // 4. 이전 서버에서 보낸 traceId를 사용하여 새로운 TraceContext를 생성한다.
        TraceContext context = buildTraceContext(traceId);
        Span span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context))
                .name(spanName)
                .start();

        // 5. SNS 메시지에 담겨있던 memberId로 이벤트 발행 및 로깅 진행
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            eventPublisher.publishEvent(new SignUpEvent(snsMessageDto.memberId()));
        } catch (Exception e) {
            span.tag("error", e.toString());
            log.error("Error processing SQS message: ", e);
        } finally {
            span.finish();
        }

    }


    /**
     * [Extract Method] - TraceContext 생성
     * 멤버 서버에서 받은 traceId를 TraceContext안에 세팅해 준다.
     */
    private TraceContext buildTraceContext(String traceId) {
        if (traceId == null) {
            // 새로운 Span 생성
            Span newSpan = tracer.newTrace();
            // 새로운 traceId 추출
            traceId = newSpan.context().traceIdString();
        }

        TraceContext.Builder contextBuilder = TraceContext.newBuilder();

        // traceId의 길이가 32자리인 경우 128비트 traceId로 처리하고, 그렇지 않은 경우 64비트 traceId로 처리
        if (traceId.length() == 32) {
            long traceIdHigh = Long.parseUnsignedLong(traceId.substring(0, 16), 16);
            long traceIdLow = Long.parseUnsignedLong(traceId.substring(16), 16);
            contextBuilder.traceIdHigh(traceIdHigh).traceId(traceIdLow);
        } else {
            long traceIdLow = Long.parseUnsignedLong(traceId, 16);
            contextBuilder.traceId(traceIdLow);
        }

        // 새로운 Span ID를 생성 하고 TraceContext 객체를 빌드해서 반환한다.
        contextBuilder.spanId(tracer.nextSpan().context().spanId());
        return contextBuilder.build();
    }

    /**
     * SNS의 ARN을 받아서 맨 뒤의 Topic 이름만 추출한다.
     */
    public String extractSnsTopicName(String input) {
        int lastIndex = input.lastIndexOf(":");
        if (lastIndex != -1) {
            return input.substring(lastIndex + 1);
        } else {
            return "";  // ':'이 없으면 빈 문자열 반환
        }
    }

}