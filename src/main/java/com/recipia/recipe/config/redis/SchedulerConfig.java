package com.recipia.recipe.config.redis;

import com.recipia.recipe.application.port.in.SyncViewCountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Redis의 데이터를 RDBMS와 동기화 하기 위해 사용한다.
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final SyncViewCountUseCase syncViewCountUseCase;

    /**
     * 이 스케쥴러는 6시간마다 실행되며, syncViewCounts() 메서드를 호출하여 Redis의 데이터를 RDBMS와 동기화한다.
     * 만약 동기화 과정에서 예외가 발생하면, 최대 5회까지 재시도한다.
     * 재시도 중 성공하면 반복문에서 탈출하고, 최대 재시도 횟수에 도달하면 관련 오류 로그를 출력한다.
     */
    @Scheduled(fixedRate = 21600000) // 6시간마다 실행
    public void syncViewCountsTask() {
        final int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                // Redis의 조회수 데이터를 동기화 하는 작업 실행
                syncViewCountUseCase.syncViewCountsBatch();
                // todo: 좋아요 로직 추가하기
                break; // 성공한다면 반복문을 탈출한다.
            } catch (Exception e) {
                attempts++;
                log.info("Redis 데이터 동기화 재시도: {}회, 오류: {}", attempts, e.getMessage());
                if (attempts >= maxAttempts) {
                    log.info("최대 재시도 횟수에 도달했습니다. Redis 데이터 동기화 작업에 실패했습니다.");
                    //todo: 재시도 다시 로직
                }
            }
        }
    }

}
