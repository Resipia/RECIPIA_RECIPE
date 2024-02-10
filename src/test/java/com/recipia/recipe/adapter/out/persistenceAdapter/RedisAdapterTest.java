package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeCountEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeViewCountEntity;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[통합] 레디스 어댑터 테스트")
class RedisAdapterTest extends TotalTestSupport {

    @Autowired
    private RedisAdapter sut;

    @Autowired
    private RecipeLikeCountRepository recipeLikeCountRepository;

    @Autowired
    private RecipeViewCountRepository recipeViewCountRepository;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    private ValueOperations<String, Integer> valueOperations;

    @BeforeEach
    void setUp() {
        valueOperations = redisTemplate.opsForValue();
    }

    @AfterEach
    void cleanUp() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @DisplayName("[happy] 레디스에서 레시피의 좋아요 수를 정확히 가져온다.")
    @Test
    void testGetLikes() {
        Long recipeId = 1L;
        String key = "recipe:like:" + recipeId;
        Integer expectedLikes = 5;
        valueOperations.set(key, expectedLikes);

        Integer actualLikes = sut.getLikes(recipeId);

        assertEquals(expectedLikes, actualLikes);
    }

    @DisplayName("[bad] 레시피의 좋아요 키가 Redis에 존재하지 않을 때, 0을 반환한다.")
    @Test
    void testGetLikesWhenKeyDoesNotExist() {
        Long recipeId = 1L;
        String key = "recipe:like:" + recipeId;

        Integer actualLikes = sut.getLikes(recipeId);

        assertEquals(0, actualLikes);
    }

    @DisplayName("[happy] 레디스에서 레시피의 조회수를 정확히 가져온다.")
    @Test
    void testGetViews() {
        Long recipeId = 1L;
        String key = "recipe:view:" + recipeId;
        Integer expectedViews = 10;
        valueOperations.set(key, expectedViews);

        Integer actualViews = sut.getViews(recipeId);

        assertEquals(expectedViews, actualViews);
    }

    @DisplayName("[bad] 레시피의 조회수 키가 Redis에 존재하지 않을 때, 0을 반환한다.")
    @Test
    void testGetViewsWhenKeyDoesNotExist() {
        Long recipeId = 1L;
        String key = "recipe:view:" + recipeId;

        Integer actualViews = sut.getViews(recipeId);

        assertEquals(0, actualViews);
    }

    @DisplayName("[happy] 레시피가 존재할때 누군가 좋아요를 누르면 redis에 좋아요 횟수가 1 증가한다.")
    @Test
    void testIncrementLikeCount() {
        Long recipeId = 1L;
        String key = "recipe:like:" + recipeId;
        valueOperations.set(key, 0);

        sut.incrementLikeCount(recipeId);

        Integer likes = valueOperations.get(key);
        assertEquals(1, likes);
    }

    @DisplayName("[happy] 레시피가 이미 좋아요가 되어있을 때 다시 좋아요를 누르면 redis에 좋아요 횟수가 1 감소한다.")
    @Test
    void testDecrementLikeCount() {
        Long recipeId = 1L;
        String key = "recipe:like:" + recipeId;
        valueOperations.set(key, 2);

        sut.decreaseLikeCount(recipeId);

        Integer likes = valueOperations.get(key);
        assertEquals(1, likes);
    }

    @DisplayName("[happy] 누군가 레시피를 조회하면 레디스에서 조회수를 증가시킨다.")
    @Test
    void incrementViewCountHappy() {
        //given
        Long recipeId = 1L;
        String key = "recipe:view:" + recipeId;
        valueOperations.set(key, 0);

        //when
        sut.incrementViewCount(recipeId);

        //then
        Integer viewCounts = valueOperations.get(key);
        Assertions.assertThat(viewCounts).isEqualTo(1);
    }

    @DisplayName("Redis에 저장되어 있던 조회수를 RDB로 동기화시킨다.")
    @Test
    void test() {
        //given
        Long recipeId = 1L;
        String key = "recipe:view:" + recipeId;
        valueOperations.set(key, 4);

        //when
        sut.syncViewCountWithDatabase();

        //then
        RecipeViewCountEntity entity = recipeViewCountRepository.findByRecipeEntityId(recipeId).orElseThrow();
        Integer viewCount = entity.getViewCount();

        Assertions.assertThat(viewCount).isEqualTo(4);
    }

}