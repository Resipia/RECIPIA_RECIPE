package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        SearchType condition = searchRequestDto.getCondition();
        if (condition == null) {
            throw new RecipeApplicationException(ErrorCode.CONDITION_NOT_FOUND);
        }

        return switch (condition) {
//            case ALL -> {
                // 전체 검색 로직 (5개씩 받으면 5개씩 가져옴)
//                List<String> ingredientResults = mongoPort.findIngredientsByPrefix(searchRequestDto);
//                List<String> hashtagResults = mongoPort.findHashtagsByPrefix(searchRequestDto);
                // 두 결과 합치기
//                yield Stream.concat(ingredientResults.stream(), hashtagResults.stream())
//                        .collect(Collectors.toList());
//            }
            case INGREDIENTS ->
                // 재료 검색 로직
                    mongoPort.findIngredientsByPrefix(searchRequestDto);

//            case HASHTAGS ->
                // 해시태그 검색 로직
//                    mongoPort.findHashtagsByPrefix(searchRequestDto);

            default ->
                // 정의되지 않은 조건에 대한 처리 로직 (예: 예외 처리)
                    Collections.emptyList(); // 예시: 빈 목록 반환
        };
    }



}
