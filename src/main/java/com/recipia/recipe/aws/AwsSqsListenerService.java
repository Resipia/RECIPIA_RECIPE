package com.recipia.recipe.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.dto.SnsInformationDto;
import com.recipia.recipe.dto.SnsMessageDto;
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


    /**
     * 아래의 Sqs리스너는 멤버 서버로 FeignClient 요청을 하는 스프링 이벤트를 발행한다.
     *
     * @apiNote
     * 만약 traceId가 들어있지 않은 메시지를 받았다면 여기서는 error에 대한 로깅만 하고 메서드를 바로 return시킨다.
     * SQS메시지 안에 traceId가 없으면 이 메시지를 동시에 받아 동작하는 멤버 서버의 SQS리스너에서는 이벤트 발행여부를 false로 둘 것이다.
     * 그럼 배치서버에서는 이 메시지의 발행여부가 false여서 다시 이벤트를 발행한다. (이때 traceId를 만들어서 넣어준다.)
     */
    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveMessage(String messageJson) throws JsonProcessingException {

        SnsInformationDto snsInformationDto = objectMapper.readValue(messageJson, SnsInformationDto.class);
        SnsMessageDto snsMessageDto = objectMapper.readValue(snsInformationDto.Message(), SnsMessageDto.class);

        if (snsMessageDto.traceId() == null) {
//            log.error("No traceId found in the message. memberId: {}, skipping processing.", snsMessageDto.memberId());
            return;
        }

        // 이전 서버에서 보낸 traceId를 사용하여 새로운 TraceContext를 생성
        TraceContext context = buildTraceContext(snsMessageDto.traceId());
        Span span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context))
                .name("[RECIPE] nickname-change SQS Received")
                .start();

        // 추출된 memberId로 이벤트 발행 및 로깅
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
     * [Extract Method] - TraceContext 생성
     * 멤버 서버에서 받은 traceId를 TraceContext안에 세팅해 준다.
     */
    private TraceContext buildTraceContext(String traceId) {
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

}