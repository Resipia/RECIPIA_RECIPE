package com.recipia.recipe.application.port.in;

import java.util.List;

public interface MongoUseCase {

    // 스프링 이벤트 리스너가 사용: 몽고 db에 재료 저장
    void saveIngredientsIntoMongo(List<String> ingredients);

}