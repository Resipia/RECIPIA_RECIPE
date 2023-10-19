package com.recipia.recipe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.repository.RecipeRepository;
import com.recipia.recipe.dto.member.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

/**
 * 레시피 관련 Kafka 메시지를 처리하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
public class RecipeConsumerService implements AcknowledgingMessageListener<String, String> {

    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate; // 트랜잭션을 프로그래밍적으로 제어하기 위한 TransactionTemplate

    /**
     * Kafka로부터 'member-updated' 토픽에서 메시지를 받아서 소비하고 처리하는 메서드입니다.
     *
     * <p>동작 순서:</p>
     * <ol>
     *   <li>메시지 페이로드를 MemberDto 객체로 역직렬화합니다.</li>
     *   <li>트랜잭션을 시작하고 레시피를 업데이트를 시도합니다.</li>
     *   <li>트랜잭션 성공 시, 메시지 처리를 완료하기 위해 확인(Acknowledgment)을 전송합니다.</li>
     * </ol>
     *
     * <p>재시도 로직:</p>
     * <ul>
     *   <li>메시지 처리 중 예외가 발생하면, 설정된 고정 간격(1000ms)으로 최대 3번까지 재시도합니다.</li>
     *   <li>3번의 재시도가 실패하면, 메시지는 Dead Letter Queue(DLQ)로 이동합니다.</li>
     * </ul>
     *
     * @param consumerRecord 카프카에서 수신한 메시지
     * @param acknowledgment 메시지 처리 확인을 위한 객체
     */
    @KafkaListener(topics = "member-updated", groupId = "recipia")
    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        // 메시지 페이로드 (실제 데이터)
        String payload = consumerRecord.value();
        log.debug("Received message: {}", payload);

        try {
            // 메시지의 JSON 데이터를 MemberDto 객체로 변환
            MemberDto memberDto = objectMapper.readValue(payload, MemberDto.class);

            // 트랜잭션을 시작하여 레시피 업데이트 로직을 수행
            boolean transactionStatus = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                try {
                    // 레시피 업데이트 시도
                    Optional<Recipe> recipe = recipeRepository.updateRecipeByMemberIdAndNickname(memberDto.getMemberId(), memberDto.getNickname());
                    if (recipe.isPresent()) {
                        log.info("레시피 업데이트 성공, ID: {}, 사용자 ID: {}", recipe.get().getId(), recipe.get().getNickname());
                        return true;
                    } else {
                        log.warn("사용자 ID에 대한 레시피 업데이트 실패: {}", memberDto.getNickname());
                        return false;
                    }
                } catch (Exception e) {  // 예상되는 예외에 따라 SpecificException을 적절히 변경하세요
                    log.error("특정 예외 발생: {}", e.getMessage());
                    status.setRollbackOnly();
                    return false;
                }
            }));

            if (transactionStatus) {
                acknowledgment.acknowledge();  // 메시지 처리 성공, acknowledgment 발송
            }

        } catch (Exception e) {
            // 메시지 처리 실패 로깅 -> 이 경우 Acknowledgment를 보내지 않는다. 따라서 Kafka에서는 메시지가 처리되지 않았다고 판단하고 재처리할 것이다.
            log.error("메시지 처리 실패: {} (파티션: {}, 키: {})", payload, consumerRecord.partition(), consumerRecord.key(), e);
        }
    }
}
