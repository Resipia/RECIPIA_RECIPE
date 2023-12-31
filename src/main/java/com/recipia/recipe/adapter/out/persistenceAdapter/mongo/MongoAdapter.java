package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.application.port.out.MongoPort;
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
     * 새 재료가 중복되지 않게 MongoDB 문서에 추가되며, 스프링 이벤트 리스너 메서드에서 이 메서드를 호출하여 재료 정보를 업데이트할 수 있다.
     * 실제로 업데이트된 '항목'(item)의 수를 반환하는 게 아니라, 업데이트된 '문서'(document)의 수를 반환한다. 성공하면 무조건 1을 반환한다.
     */
    @Override
    public Long saveIngredientsIntoMongo(List<String> newIngredients) {
        // 1. documentId로 지정된 IngredientDocument를 찾는다.
        Query query = new Query(Criteria.where("id").is(documentId));

        // 2. $addToSet 연산자를 사용하여 중복을 방지하고 재료를 추가한다.
        Update update = new Update().addToSet("ingredients").each(newIngredients);

        // 3. updateFirst는 쿼리 조건과 일치하는 첫 번째 문서를 업데이트한다.
        Long updateResult = mongoTemplate.updateFirst(query, update, IngredientDocument.class).getModifiedCount();

        return updateResult;
    }
}