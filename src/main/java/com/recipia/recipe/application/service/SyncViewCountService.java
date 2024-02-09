package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.SyncViewCountUseCase;
import com.recipia.recipe.application.port.out.RecipeViewCountPort;
import com.recipia.recipe.application.port.out.RedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Redis의 조회수와 RDBMS의 조회수 테이블을 동기화하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SyncViewCountService implements SyncViewCountUseCase {

    private final RedisPort redisPort;
    private final RecipeViewCountPort recipeViewCountPort;

    /**
     * Redis에 저장된 조회수를 RDB에 저장한다.
     * 받아온 조회수 정보 Map을 그대로 Adapter에 넘겨서 배치 처리한다.
     */
    @Transactional
    @Override
    public void syncViewCountsBatch() {
        Map<Long, Integer> viewCounts = redisPort.fetchAllViewCounts();
        recipeViewCountPort.batchUpdateViewCounts(viewCounts);
    }

}
