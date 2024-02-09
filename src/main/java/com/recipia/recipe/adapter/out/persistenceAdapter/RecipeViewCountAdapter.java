package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeViewCountQueryDslRepository;
import com.recipia.recipe.application.port.out.RecipeViewCountPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 레시피 조회수 관련 어댑터
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeViewCountAdapter implements RecipeViewCountPort {

    private final RecipeViewCountQueryDslRepository recipeViewCountQueryDslRepository;
    private final EntityManager entityManager;

    /**
     * RDB에 레시피의 조회수를 모두 일괄 업데이트 한다.
     */
    @Override
    public void batchUpdateViewCounts(Map<Long, Integer> viewCounts) {
        int count = 0;
        for (Map.Entry<Long, Integer> entry : viewCounts.entrySet()) {
            recipeViewCountQueryDslRepository.updateViewCountInDatabase(entry.getKey(), entry.getValue());
            if (++count % 20 == 0) { // 20개의 업데이트마다 flush와 clear를 호출
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush(); // 남은 변경 사항 처리
        entityManager.clear();
    }
}
