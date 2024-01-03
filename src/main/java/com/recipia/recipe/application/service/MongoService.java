package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
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
        // List는 Optional보다는 if문으로 분기처리하는게 낫다. Optional은 단일 객체에 대한 null을 다룰때 주로 사용한다.
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.INVALID_INGREDIENTS);
        }
        mongoPort.saveIngredientsIntoMongo(ingredients);
    }

    @Override
    public void saveHashtagsIntoMongo(List<String> hashtags) {
        // List는 Optional보다는 if문으로 분기처리하는게 낫다. Optional은 단일 객체에 대한 null을 다룰때 주로 사용한다.
        if (hashtags == null || hashtags.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.INVALID_HASHTAGS);
        }
        mongoPort.saveHashTagsIntoMongo(hashtags);
    }

    /**
     * mongoDB에서 재료 검색을 하는 기능
     */
    @Override
    public List<String> findIngredientsByPrefix(SearchRequestDto searchRequestDto) {
        String condition = searchRequestDto.getCondition();
        if (condition == "all") {
            // todo: 재료에서 5개 검색
            return mongoPort.findIngredientsByPrefix(searchRequestDto);
            // todo: 해시태그에서 5개 검색

            // todo: 두개의 응답을 합치기
        } else if (condition == "ingredients") {
            // todo: 이걸 추후 재료 검색으로 변경
            return mongoPort.findIngredientsByPrefix(searchRequestDto);
        } else {
            // todo: 이걸 추후 해시태그 검색으로 변경
            return mongoPort.findIngredientsByPrefix(searchRequestDto);
        }
    }


}
