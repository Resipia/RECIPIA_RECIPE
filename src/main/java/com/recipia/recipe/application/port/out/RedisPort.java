package com.recipia.recipe.application.port.out;

public interface RedisPort {

    void syncLikesAndViewsWithDatabase();

    Integer getLikes(Long recipeId);

    void incrementLikeCount(Long recipeId);

    void decreaseLikeCount(Long recipeId);

    Integer getViews(Long recipeId);

    void incrementViewCount(Long recipeId);

    void syncViewCountWithDatabase();
}
