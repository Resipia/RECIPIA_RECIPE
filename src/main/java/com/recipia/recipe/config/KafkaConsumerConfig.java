package com.recipia.recipe.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 소비자 설정 클래스.
 *
 * @version 1.0
 * @since 2023-10-02
 */
@EnableKafka
@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfig {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * ConsumerFactory 설정
     * <p>
     * Kafka 소비자에 필요한 설정을 설정한다.
     * </p>
     *
     * @return 제공된 설정으로 구성된 ConsumerFactory를 반환합니다.
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka 부트스트랩 서버
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 키 역직렬화 클래스
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 값 역직렬화 클래스
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "recipia"); // 소비자 그룹 ID
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // 수동 ACK 모드 활성화
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // "latest" 설정은 소비자가 시작되었을 때 가장 최신의 메시지부터 메시지를 소비하게 된다.
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // 한 번의 poll()에서 가져올 레코드의 최대 개수
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);  // 서버로부터 가져올 최소 데이터 크기
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // 데이터가 충분하지 않을 때 최대 얼마나 기다릴지(ms)
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000); // 세션 타임아웃
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 하트비트 간격
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka 리스너 컨테이너 팩토리 설정
     * <p>
     * Kafka 리스너 컨테이너를 생성하기 위한 팩토리를 설정합니다. 수동 승인, 재시도 정책, 그리고 Dead Letter Queue (DLQ) 설정을 포함합니다.
     * </p>
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // ExponentialBackOff 설정
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L);  // 초기 간격을 1초로 설정
        backOff.setMultiplier(2.0);        // 지수를 2.0으로 설정
        backOff.setMaxInterval(10000L);    // 최대 간격을 10초로 설정
        backOff.setMaxElapsedTime(30000L); // 최대 재시도 시간을 30초로 설정

        // DeadLetterPublishingRecoverer 설정 (개별 DLQ)
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (cr, e) -> new TopicPartition("dlq_for_" + cr.topic() + "_" + cr.partition(), 0)
        );

        // CommonErrorHandler 설정
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                deadLetterPublishingRecoverer,
                backOff
        ));

        return factory;
    }

}

