package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.out.persistence.document.HashtagDocument;
import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

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

}
