package com.recipia.recipe.application.port.out;

public interface RedisPort {

    void syncLikesAndViewsWithDatabase();

    Integer getLikes(Long recipeId);

    Integer getViews(Long recipeId);

    void incrementLikeCount(Long recipeId);

    void decreaseLikeCount(Long recipeId);
}
