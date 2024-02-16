package com.recipia.recipe.application.port.out;

import java.util.Map;

public interface RedisPort {

    Integer getLikes(Long recipeId);

    void incrementLikeCount(Long recipeId);

    void decreaseLikeCount(Long recipeId);

    Integer getViews(Long recipeId);

    void incrementViewCount(Long recipeId);

    void syncViewCountWithDatabase();

    Map<Long, Integer> fetchAllViewCounts();
}
