package com.recipia.recipe.adapter.out.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.config.aws.SnsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class SnsService {

    private final SnsClient snsClient;
    private final SnsConfig snsConfig;
    private final ObjectMapper objectMapper;


    public PublishResponse publishNicknameToTopic(String message) {

        // SNS 발행 요청 생성
        PublishRequest publishRequest = PublishRequest.builder()
                .message(message)
                .topicArn(snsConfig.getSnsTopicNicknameChangeARN())
                .build();

        // SNS 클라이언트를 통해 메시지 발행
        PublishResponse response = snsClient.publish(publishRequest);

        // messageId 로깅
        log.info("[RECIPE] Published message to SNS with recipeId: {}", response.messageId());

        return response;
    }

    private String convertMapToJson(Map<String, Object> messageMap) {
        try {
            return objectMapper.writeValueAsString(messageMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting message map to JSON", e);
        }
    }

}
