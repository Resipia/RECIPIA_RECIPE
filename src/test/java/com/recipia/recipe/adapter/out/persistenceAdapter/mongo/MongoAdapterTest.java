package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.config.TotalTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[통합] mongo 재료저장 테스트 Transactional이 먹히지 않아 각 테스트별로 데이터를 insert하고 delete하는 작업을 한다.")
class MongoAdapterTest extends TotalTestSupport {

    @Autowired
    private MongoAdapter sut;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RecipeMongoRepository mongoRepository;

    @Value("${mongo.test.documentId}")
    private String documentId;

    /**
     * 테스트 데이터 추가
     * 각 테스트가 시작되면 3개의 재료를 기본적으로 추가한다.
     */
    @BeforeEach
    void setUp() {
        List<String> initialIngredients = Arrays.asList("김치", "파", "감자"); // 추가할 초기 재료 목록

        Query query = new Query(Criteria.where("id").is(documentId));
        Update update = new Update().addToSet("ingredients").each(initialIngredients.toArray());
        mongoTemplate.upsert(query, update, IngredientDocument.class);
    }

    /**
     * 테스트 클랜징 메서드
     * 각 테스트가 종료되면 이 클랜징 메서드가 실행되어 테스트에서 몽고db에 추가한 재료를 삭제한다.
     */
    @AfterEach
    void tearDown() {
        List<String> newIngredients = Arrays.asList("김치", "파", "감자", "소고기", "돼지고기", "고구마", "양상추", "호박"); // 삭제할 재료 목록

        Query query = new Query(Criteria.where("id").is(documentId));
        Update update = new Update().pullAll("ingredients", newIngredients.toArray());
        mongoTemplate.updateFirst(query, update, IngredientDocument.class);
    }

    @DisplayName("[happy] 사용자가 재료를 입력했을때 입력한 재료가 mongoDB에 저장된다.")
    @Test
    void testSaveIngredientsIntoMongo() {

        // given
        List<String> newIngredients = Arrays.asList("소고기", "돼지고기");

        // when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument ingredientDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(ingredientDocument); // null이 아님을 검증
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(ingredientDocument.getIngredients().containsAll(newIngredients));
    }

    @DisplayName("[happy] 이미 다른 유저가 저장한 재료를 저장 시도하면 mongoDB에 저장되지 않는다.")
    @Test
    void whenAlreadyExistIngredientIfUserInsertIngredientNotSave() {
        // given
        List<String> existingIngredients = Arrays.asList("김치", "파");

        // when
        Long savedCount = sut.saveIngredientsIntoMongo(existingIngredients);// 같은 데이터로 다시 저장 시도

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(updatedDocument); // null이 아님을 검증
        Assertions.assertThat(savedCount).isEqualTo(0);
        assertEquals(3, updatedDocument.getIngredients().size()); // 데이터 개수는 초기 세팅값인 3이어야 한다.
        assertTrue(updatedDocument.getIngredients().containsAll(Arrays.asList("김치", "파")));
    }


    @DisplayName("[happy] 이미 다른 유저가 저장한 재료(김치)와 저장된적 없는 재료(고구마)를 저장 시도하면 새로운 재료(고구마)만 저장된다.")
    @Test
    void whenAlreadyExistIngredientIfUserInsertIngredientNotSave2() {
        // given
        List<String> newIngIngredients = Arrays.asList("김치", "고구마");

        // when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(updatedDocument.getIngredients().containsAll(Arrays.asList("김치", "고구마")));
    }


    @DisplayName("[happy] 이미 다른 유저가 저장한(김치, 파, 감자)랑 완전히 동일한 재료를 저장 시도하면 저장되지 않는다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave() {
        //given
        List<String> newIngredients = Arrays.asList("김치", "파", "감자");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(0); // 업데이트는 진행되지 않는다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> ingredientsCount = document.getIngredients().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Assertions.assertThat(ingredientsCount).containsEntry("김치", 1L);
            Assertions.assertThat(ingredientsCount).containsEntry("파", 1L);
            Assertions.assertThat(ingredientsCount).containsEntry("감자", 1L);
        });
    }

    @DisplayName("[happy] 사용자가 새로운 재료를 2번(ex: 고구마, 고구마) 입력해서 저장을 시도하면 하나의 재료(고구마)만 저장된다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave2() {
        //given
        List<String> newIngredients = Arrays.asList("고구마", "고구마");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 진행된다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> ingredientsCount = document.getIngredients().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(ingredientsCount).containsEntry("고구마", 1L);
        });
    }

    @DisplayName("[happy] 사용자가 이미 저장된 쟤료(ex: 김치, 김치)를 2번 입력해서 저장을 시도하면 이미 존재하기 때문에 저장되지 않는다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave3() {
        //given
        List<String> newIngredients = Arrays.asList("김치", "김치");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        IngredientDocument updatedDocument = mongoTemplate.findOne(query, IngredientDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(0); // 업데이트가 진행된다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> ingredientsCount = document.getIngredients().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(ingredientsCount).containsEntry("김치", 1L);
        });
    }

    @DisplayName("[happy] 오류가 발생하여 재료를 insert하는 메서드가 2번 호출되어되어도 이상없이 저장된다.")
    @Test
    void testSaveDuplicateIngredientsIntoMongo() {

        // given
        Query query = new Query(Criteria.where("id").is(documentId)); // 해당 ID로 문서를 찾기 위한 쿼리 생성
        IngredientDocument document = mongoTemplate.findOne(query, IngredientDocument.class); // 쿼리를 사용하여 문서를 찾음
        System.out.println("저장전 재료: " + document.getIngredients());

        int originalSize = document.getIngredients().size(); // 테스트 시작 전, 원래 문서에 저장된 재료의 개수

        List<String> newIngredients = Arrays.asList("양상추", "호박"); // 저장하려는 새로운 재료 목록

        // when (중복 저장)
        int duplicateCount = 2; // 중복 저장을 위한 횟수 설정
        for (int i = 0; i < duplicateCount; i++) {
            sut.saveIngredientsIntoMongo(newIngredients); // 지정된 횟수만큼 재료를 저장하는 메소드 호출
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