package com.recipia.recipe.application.port.out;

import java.util.Map;

public interface RecipeViewCountPort {

    void batchUpdateViewCounts(Map<Long, Integer> viewCounts);
}
