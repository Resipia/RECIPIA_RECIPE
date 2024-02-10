package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeViewCountEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 좋아요, 조회수 등 Redis를 활용하는 어댑터
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisAdapter implements RedisPort {

    private final RecipeRepository recipeRepository;
    private final RecipeViewCountRepository recipeViewCountRepository;
    private final RecipeQueryRepository querydslJpaRepository;
    private final RedisTemplate<String, Integer> redisTemplate;


    // 좋아요 수를 가져온다.
    @Override
    public Integer getLikes(Long recipeId) {
        String key = "recipe:like:" + recipeId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(0);
    }

    /**
     * 메서드는 Redis에 저장된 특정 키에 대한 값(좋아요 수)을 증가시킨다.
     * 이 메서드는 Redis에 해당 키가 이미 존재한다고 가정한다.
     */
    @Override
    public void incrementLikeCount(Long recipeId) {
        String key = "recipe:like:" + recipeId;
        redisTemplate.opsForValue().increment(key);
    }

    // 좋아요 횟수를 감소시킨다.
    @Override
    public void decreaseLikeCount(Long recipeId) {
        String key = "recipe:like:" + recipeId;
        redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 조회수를 가져온다.
     */
    @Override
    public Integer getViews(Long recipeId) {
        String key = "recipe:view:" + recipeId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(0);
    }

    /**
     * 레시피의 조회수를 증가시킨다.
     */
    @Override
    public void incrementViewCount(Long recipeId) {
        String key = "recipe:view:" + recipeId;
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * redis에 저장된 조회수를 RDB로 이동시킨다.
     */
    @Override
    public void syncViewCountWithDatabase() {
        try {
            Set<String> keys = redisTemplate.keys("recipe:view:*");
            if (keys != null) {
                keys.forEach(key -> {
                    try {
                        Integer viewCount = redisTemplate.opsForValue().get(key);
                        Long recipeId = extractRecipeIdFromKey(key);
                        updateViewCountInDatabase(recipeId, viewCount);
                    } catch (Exception e) {
                        log.error("Error updating view count from Redis for key: {}", key, e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RecipeApplicationException(ErrorCode.REDIS_ERROR_OCCUR);
        }
    }

    /**
     * Redis에서 "recipe:view:*" 패턴에 일치하는 모든 키를 검색하고
     * 각 키에 대한 조회수 값을 가져온 다음, recipeId와 조회수 값을 매핑하여 반환한다.
     */
    @Override
    public Map<Long, Integer> fetchAllViewCounts() {
        Set<String> keys = redisTemplate.keys("recipe:view:*");
        if (keys == null) {
            return new HashMap<>();
        }

        return keys.stream()
                .collect(Collectors.toMap(
                        this::extractRecipeIdFromKey,
                        key -> Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(0)
                ));
    }

    /**
     * [Extract Method] -  RDB에서 레시피의 조회수를 업데이트한다.
     * <p>
     * 이 메서드는 레시피의 조회수 엔티티(RecipeViewCntEntity)를 레시피 ID를 기반으로 조회한다.
     * 조회된 엔티티가 존재하면 주어진 조회수(viewCount)로 엔티티의 조회수를 업데이트한다.
     * 만약 조회된 엔티티가 없다면, 새로운 RecipeViewCntEntity를 생성하고 주어진 조회수로 초기화한 뒤 저장한다.
     * <p>
     */
    public void updateViewCountInDatabase(Long recipeId, Integer viewCount) {
        Optional<RecipeViewCountEntity> viewCountEntityOptional = recipeViewCountRepository.findByRecipeEntityId(recipeId);

        viewCountEntityOptional.ifPresentOrElse(
                viewCntEntity -> {
                    // 조회된 엔티티가 존재한다면 변경감지로 업데이트 처리
                    viewCntEntity.changeViewCount(viewCount);
                },
                () -> {
                    // 조회된 엔티티가 없다면 새로운 엔티티 생성해서 저장
                    RecipeEntity recipeEntity = recipeRepository.findById(recipeId)
                            .orElseThrow(() -> new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

                    RecipeViewCountEntity recipeViewCountEntity = RecipeViewCountEntity.of(recipeEntity, viewCount);
                    recipeViewCountRepository.save(recipeViewCountEntity);
                }
        );
    }

    /**
     * [Extract Method]
     * redis에 있던 조회수 count를 rdb에 저장
     */
    public Long updateLikesInDatabase(Long recipeId, Integer likes) {
        return querydslJpaRepository.updateLikesInDatabase(recipeId, likes);
    }

    /**
     * 레시피 id를 추출한다. (이 id를 통해 레시피 내부의 like, view 갯수를 업데이트 한다.)
     */
    public Long extractRecipeIdFromKey(String key) {
        try {
            // "recipe:like:123" 형식의 키에서 "123" 부분을 추출
            return Long.parseLong(key.split(":")[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // 숫자 형식의 오류 또는 배열 인덱스 관련 오류 처리
            log.error("Invalid key format for Redis key: {}", key, e);
            throw new RecipeApplicationException(ErrorCode.REDIS_RECIPE_ID_NOT_FOUND);
        }
    }


}
