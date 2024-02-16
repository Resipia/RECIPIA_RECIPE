package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeViewCountEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeViewCountQueryDslRepository;
import com.recipia.recipe.application.port.out.RecipeViewCountPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 레시피 조회수 관련 어댑터
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeViewCountAdapter implements RecipeViewCountPort {

    private final RecipeViewCountRepository recipeViewCountRepository;
    private final EntityManager entityManager;

    /**
     * RDB에 레시피의 조회수를 모두 일괄 업데이트 한다.
     * 20개마다 배치 처리를 한다.
     */
    @Override
    public void batchUpdateViewCounts(Map<Long, Integer> viewCounts) {
        ArrayList<Map.Entry<Long, Integer>> entries = new ArrayList<>(viewCounts.entrySet());

        // 20개씩 분할처리
        for (int i = 0; i < entries.size(); i += 20) {
            // 서브 리스트를 생성하여 현재 청크를 가져온다. 이때 리스트의 범위를 초과하지 않도록 min함수를 사용한다.
            List<Map.Entry<Long, Integer>> chunk = entries.subList(i, Math.min(entries.size(), i + 20));

            // 현재 청크를 처리
            chunk.forEach(entry -> {
                recipeViewCountRepository.findByRecipeEntityId(entry.getKey()).ifPresentOrElse(
                        entity -> {
                            // 조회된 엔티티가 있으면, 조회수를 변경한다.
                            entity.changeViewCount(entry.getValue());
                        },
                        () -> {
                            // 조회된 엔티티가 없으면, 새 엔티티를 생성하고 저장한다.
                            RecipeViewCountEntity newEntity = RecipeViewCountEntity.of(RecipeEntity.of(entry.getKey()), entry.getValue());
                            recipeViewCountRepository.save(newEntity);
                        }
                );
            });

            // 청크 처리 후 flush와 clear를 호출
            entityManager.flush();
            entityManager.clear();
        }
    }

}
