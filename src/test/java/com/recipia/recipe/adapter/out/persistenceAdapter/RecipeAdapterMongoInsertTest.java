package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@Rollback
class RecipeAdapterMongoInsertTest extends TotalTestSupport{

    @Autowired
    private RecipeAdapter sut;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @DisplayName("[happy] 재료를 저장하는 어댑터 메서드가 동작하면 MongoDB에 재료를 저장한다.")
    @Test
    void testSaveIngredientsIntoMongo() {

        // given
        String documentId = "65892a7c0ea2136585038542";
        List<String> newIngredients = Arrays.asList("진안", "최진안");

        // when
        StepVerifier.create(sut.saveIngredientsIntoMongo(documentId, newIngredients))
                .verifyComplete();

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        StepVerifier.create(mongoTemplate.findOne(query, IngredientDocument.class))
                .assertNext(ingredientDocument -> {
                    // 1. 저장된 재료 출력하기
                    System.out.println("저장된 재료: " + ingredientDocument.getIngredients());

                    // 2. 저장된 문서가 null이 아님을 확인한다.
                    assertNotNull(ingredientDocument);

                    // 3. 저장된 재료 목록에 새로운 재료(newIngredients)가 모두 포함되어 있는지 확인한다.
                    assertTrue(ingredientDocument.getIngredients().containsAll(newIngredients));
                })
                .verifyComplete();
    }

    @DisplayName("[happy] 재료 중복 저장 테스트")
    @Test
    void testSaveDuplicateIngredientsIntoMongo() {

        // given
        String documentId = "65892a7c0ea2136585038542";
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument document = mongoTemplate.findOne(query, IngredientDocument.class).block();
        int size = document.getIngredients().size();


        List<String> newIngredients = Arrays.asList("감자", "양상추");

        // when (중복 저장)
        int duplicateCount = 2; // 중복 저장 횟수

        for (int i = 0; i < duplicateCount; i++) {
            StepVerifier.create(sut.saveIngredientsIntoMongo(documentId, newIngredients))
                    .verifyComplete();
        }

        // then
        StepVerifier.create(mongoTemplate.findOne(query, IngredientDocument.class))
                .assertNext(ingredientDocument -> {
                    // 1. 저장된 재료 출력하고 저장된 문서가 null이 아님을 확인한다.
                    System.out.println("저장된 재료: " + ingredientDocument.getIngredients());
                    assertNotNull(ingredientDocument);

                    // todo: 만약 리스트의 값이 이미 db에 존재하면 그 값만큼 개수는 빼야함

                    // list의 첫번째 요소값의 count를 가져와서


                    // 2. 중복 재료가 추가되지 않았음을 확인
                    assertTrue(ingredientDocument.getIngredients().containsAll(newIngredients));
                    System.out.println("ingredientDocument: " + ingredientDocument.getIngredients());
                    System.out.println("newIngredients: " + newIngredients);

                    // 3. 중복 저장된 재료 수를 확인 (원본 재료 수와 같아야 함)
                    int savedIngredientsCount = ingredientDocument.getIngredients().size();
                    int addIngredientsCount = newIngredients.size();


//                    10 = 기존 + 새로운거 추가
                    assertTrue(savedIngredientsCount == size + addIngredientsCount);

                })
                .verifyComplete();
    }

}