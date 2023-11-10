package com.recipia.recipe.aws;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SqsListenerService {

    private final ObjectMapper objectMapper;

    @SqsListener(value = "${spring.cloud.aws.sqs.nickname-sqs-name}")
    public void receiveMessage(String messageJson) {

        Subsegment subsegment = AWSXRay.beginSubsegment("publishNicknameToTopic");

        try {
            JsonNode messageNode = objectMapper.readTree(messageJson);
            String topicArn = messageNode.get("TopicArn").asText();
            String messageContent = messageNode.get("Message").asText();

            // Assuming the "Message" is also a JSON string, we parse it to print as JSON object
            JsonNode message = objectMapper.readTree(messageContent);

            
            log.info("Topic ARN: {}", topicArn);
            log.info("Message:  {}", message.toString());
        } catch (IOException e) {
            subsegment.addException(e);
            throw new RuntimeException("Error parsing message JSON", e);
        } finally {
            AWSXRay.endSubsegment();
        }

    }

}
