package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.out.persistence.document.HashtagDocument;
import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MongoAdapter implements MongoPort {

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.test.documentId}")
    private String documentId;


    /**
     * 재료값을 몽고DB에 저장한다.
     */
    @Override
    public Long saveIngredientsIntoMongo(List<String> newIngredients) {
        return saveDataIntoMongo("ingredients", newIngredients);
    }

    /**
     * 해시태그를 몽고DB에 저장한다.
     */
    @Override
    public Long saveHashTagsIntoMongo(List<String> newHashtags) {
        return saveDataIntoMongo("hashtags", newHashtags);
    }

    /**
     * [통합검색을 위한 데이터 저장]
     * 사용자가 해시태그를 입력하면 중복된 데이터가 아니라면 MongoDB 문서에 추가된다.
     * 업데이트에 성공하면 업데이트된 문서(documet)의 갯수인 1을 반환한다.
     */
    public Long saveDataIntoMongo(String dataType, List<String> newData) {
        // 1. documentId로 지정된 문서를 찾는다.
        Query query = new Query(Criteria.where("id").is(documentId));

        // 2. $addToSet 연산자를 사용하여 중복을 방지하고 데이터를 추가한다.
        Update update = new Update().addToSet(dataType).each(newData);

        // 3. dataType에 따라 적절한 문서 클래스를 선택하여 업데이트한다.
        if ("ingredients".equals(dataType)) {
            // IngredientDocument에 대한 업데이트
            return mongoTemplate.updateFirst(query, update, IngredientDocument.class).getModifiedCount();
        } else if ("hashtags".equals(dataType)) {
            // HashtagDocument에 대한 업데이트
            return mongoTemplate.updateFirst(query, update, HashtagDocument.class).getModifiedCount();
        }

        // 적절한 dataType이 없는 경우
        throw new RecipeApplicationException(ErrorCode.MONGO_DB_UPDATED_FAIL);
    }

    /**
     * 유저가 검색에 입력한 접두사를 기준으로 10개의 단어를 반환한다.
     */
    public List<String> searchData(SearchRequestDto searchRequestDto) {
        return getSearchResults(searchRequestDto);
    }

    private List<String> getSearchResults(SearchRequestDto searchRequestDto) {
        String searchWord = searchRequestDto.getSearchWord();
        String condition = searchRequestDto.getCondition().toString();
        // 접두사가 비어 있는 경우 빈 목록 반환
        if (searchWord == null || searchWord.isEmpty()) {
            return Collections.emptyList();
        }

        String regexPattern = (containsKorean(searchWord) ? "^" + searchWord : "^" + searchWord + "[A-Za-z]*");

        // Aggregation pipeline 구성
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").is(documentId)),
                Aggregation.unwind(condition),
                Aggregation.match(Criteria.where(condition).regex(regexPattern, "i")),
                Aggregation.project(condition),
                Aggregation.limit(searchRequestDto.getResultSize())
        );

        // Aggregation 실행
        AggregationResults<String> results = mongoTemplate.aggregate(aggregation, IngredientDocument.class, String.class);

        // 결과 반환
        return results.getMappedResults();
    }


    /**
     * 만약 사용자가 입력한 문자열이 짧다면, 반복문의 비용은 무시할 수 있을 정도로 작으며,
     * 이는 MongoDB 검색 최적화를 통해 얻는 성능 이점에 비하면 미미한 수준이다.
     */
    private boolean containsKorean(String searchWord) {
        // 한글 문자 범위를 확인한다 (가-힣).
        for (int i = 0; i < searchWord.length(); i++) {
            char ch = searchWord.charAt(i);
            if (ch >= '\uAC00' && ch <= '\uD7AF') {
                return true;
            }
        }
        return false;
    }


}
