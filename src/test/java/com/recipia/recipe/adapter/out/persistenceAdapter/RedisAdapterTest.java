package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
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
    private RecipeRepository recipeRepository;

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

    @DisplayName("[happy] RDB와 레디스의 좋아요 수를 동기화한다.")
    @Test
    void testSyncLikesAndViewsWithDatabase() {
        Long recipeId = 1L;
        String likeKey = "recipe:like:" + recipeId;
        Integer likesInRedis = 3;
        valueOperations.set(likeKey, likesInRedis);


        sut.syncLikesAndViewsWithDatabase();

        // 검증
        RecipeEntity recipeEntity = recipeRepository.findById(recipeId).orElseThrow();
        Integer likeCount = recipeEntity.getLikeCount();
        Assertions.assertThat(likeCount).isEqualTo(3);
    }

    @DisplayName("[bad] 레디스 키 포맷이 잘못되었을 때, RecipeApplicationException 예외를 발생시킨다.")
    @Test
    void testSyncLikesAndViewsWithDatabaseWithInvalidKey() {
        // 잘못된 형식의 레디스 키 설정
        String invalidKey = "recipe:like:invalid";
        redisTemplate.opsForValue().set(invalidKey, 1);

        // 예외가 발생하는지 검증
        Assertions.assertThatThrownBy(() -> sut.syncLikesAndViewsWithDatabase())
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레디스 내부에서 레시피id를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REDIS_RECIPE_ID_NOT_FOUND);
    }


    @DisplayName("[happy] 레시피의 좋아요 수를 정확히 가져온다.")
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

    @DisplayName("[happy] 레시피의 조회수를 정확히 가져온다.")
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

}