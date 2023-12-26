package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@Rollback
class RecipeAdapterMongoInsertTest extends TotalTestSupport {

    @Autowired
    private RecipeAdapter sut;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${mongo.test.documentId}")
    private String documentId;

    @AfterEach
    void tearDown() {
        List<String> newIngredients = Arrays.asList("감자", "양상추", "진안", "최진안"); // 삭제할 재료 목록

        Query query = new Query(Criteria.where("id").is(documentId));
        Update update = new Update().pullAll("ingredients", newIngredients.toArray());
        mongoTemplate.updateFirst(query, update, IngredientDocument.class);
    }


    @DisplayName("[happy] 재료를 저장하는 어댑터 메서드가 동작하면 MongoDB에 재료를 저장한다.")
    @Test
    void testSaveIngredientsIntoMongo() {

        // given
        List<String> newIngredients = Arrays.asList("진안", "최진안");

        // when
        sut.saveIngredientsIntoMongo(documentId, newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument ingredientDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(ingredientDocument);
        assertTrue(ingredientDocument.getIngredients().containsAll(newIngredients));
        System.out.println("저장된 재료: " + ingredientDocument.getIngredients());
    }

    @DisplayName("[happy] 재료 중복 저장 테스트")
    @Test
    void testSaveDuplicateIngredientsIntoMongo() {

        // given
        Query query = new Query(Criteria.where("id").is(documentId)); // 해당 ID로 문서를 찾기 위한 쿼리 생성
        IngredientDocument document = mongoTemplate.findOne(query, IngredientDocument.class); // 쿼리를 사용하여 문서를 찾음
        System.out.println("저장전 재료: " + document.getIngredients());

        int originalSize = document.getIngredients().size(); // 테스트 시작 전, 원래 문서에 저장된 재료의 개수

        List<String> newIngredients = Arrays.asList("감자", "양상추"); // 저장하려는 새로운 재료 목록

        // when (중복 저장)
        int duplicateCount = 2; // 중복 저장을 위한 횟수 설정
        for (int i = 0; i < duplicateCount; i++) {
            sut.saveIngredientsIntoMongo(documentId, newIngredients); // 지정된 횟수만큼 재료를 저장하는 메소드 호출
        }

        // then
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class); // 업데이트된 문서를 다시 조회
        System.out.println("저장후 재료: " + updatedDocument.getIngredients());
        int updatedSize = updatedDocument.getIngredients().size(); // 업데이트 후의 재료 개수 측정
        int expectedSize = originalSize + (newIngredients.stream().anyMatch(ingredient -> !document.getIngredients().contains(ingredient)) ? newIngredients.size() : 0);

        assertNotNull(updatedDocument); // 문서가 실제로 존재하는지 확인
        assertTrue(updatedDocument.getIngredients().containsAll(newIngredients)); // 업데이트된 문서에 새로운 재료가 포함되었는지 확인
        assertEquals(expectedSize, updatedSize);
    }

}
