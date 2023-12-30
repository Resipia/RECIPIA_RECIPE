package com.recipia.recipe.application.port.out;

import java.util.List;

public interface MongoPort {

    Long saveIngredientsIntoMongo(List<String> newIngredients);

}
