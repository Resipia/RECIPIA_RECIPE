package com.recipia.recipe.application.port.out;

import java.util.List;

public interface MongoPort {

    Long saveIngredientsIntoMongo(List<String> newIngredients);

    Long saveHashTagsIntoMongo(List<String> newHashtags);

    List<String> findIngredientsByPrefix(String prefix);
}
