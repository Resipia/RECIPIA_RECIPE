package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.application.port.out.MongoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoService implements MongoUseCase {

    private final MongoPort mongoPort;

    /**
     * mongoDB에 데이터 저장을 담당하는 메서드
     * 주관심사: 레시피 생성 (mongoDB에 재료 데이터를 저장한다.)
     */
    @Override
    public void saveIngredientsIntoMongo(List<String> ingredients) {
        mongoPort.saveIngredientsIntoMongo(ingredients);
    }

}
