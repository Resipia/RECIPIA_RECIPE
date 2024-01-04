package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.out.persistence.document.SearchDocument;
import com.recipia.recipe.common.exception.RecipeApplicationException;
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

/**
 * 몽고DB는 @Transactional이 먹히지 않아서 각 테스트별로 데이터를 insert하고 delete하는 작업을 한다.
 */
@DisplayName("[통합] mongoDB 저장 테스트")
class MongoAdapterTest extends TotalTestSupport {

    @Autowired
    private MongoAdapter sut;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoSearchRepository mongoSearchRepository;

    @Value("${mongo.test.documentId}")
    private String documentId;

    /**
     * 테스트 데이터 추가
     * 각 테스트가 시작되면 3개의 재료를 기본적으로 추가한다.
     */
    @BeforeEach
    void setUp() {
        Query query = new Query(Criteria.where("id").is(documentId));

        List<String> initialIngredients = Arrays.asList("김치", "파", "김", "김가루", "김밥", "김빱", "감자", "ca", "zzz");
        Update update1 = new Update().addToSet("ingredients").each(initialIngredients.toArray());
        mongoTemplate.upsert(query, update1, SearchDocument.class);

        List<String> initialHashtags = Arrays.asList("해시태그1", "해시태그2", "해시태그3");
        Update update2 = new Update().addToSet("hashtags").each(initialHashtags.toArray());
        mongoTemplate.upsert(query, update2, SearchDocument.class);
    }

    /**
     * 테스트 클랜징 메서드
     * 각 테스트가 종료되면 이 클랜징 메서드가 실행되어 테스트에서 몽고db에 추가한 재료를 삭제한다.
     */
    @AfterEach
    void tearDown() {
        Query query = new Query(Criteria.where("id").is(documentId));

        List<String> newIngredients = Arrays.asList("김치", "파", "김", "김가루", "김밥", "김빱", "감자", "소고기", "돼지고기", "고구마", "양상추", "호박", "ca", "zzz");
        Update update1 = new Update().pullAll("ingredients", newIngredients.toArray());
        mongoTemplate.updateFirst(query, update1, SearchDocument.class);

        List<String> newHashtags = Arrays.asList("해시태그1", "해시태그2", "해시태그3", "해시태그4", "해시태그5", "해시태그6", "해시태그7", "해시태그8");
        Update update2 = new Update().pullAll("hashtags", newHashtags.toArray());
        mongoTemplate.updateFirst(query, update2, SearchDocument.class);
    }

    @DisplayName("[happy] 저장된적 없는 재료를 저장 시도하면 mongoDB에 저장된다.")
    @Test
    void testSaveIngredientsIntoMongo() {

        // given
        List<String> newIngredients = Arrays.asList("소고기", "돼지고기");

        // when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument searchDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(searchDocument); // null이 아님을 검증
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(searchDocument.getIngredients().containsAll(newIngredients));
    }


    @DisplayName("[happy] 이미 저장된 재료(김치)와 저장된적 없는 재료(고구마)를 저장 시도하면 새로운 재료(고구마)만 저장된다.")
    @Test
    void whenAlreadyExistIngredientIfUserInsertIngredientNotSave2() {
        // given
        List<String> newIngIngredients = Arrays.asList("김치", "고구마");

        // when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(updatedDocument.getIngredients().containsAll(Arrays.asList("김치", "고구마")));
    }


    @DisplayName("[happy] 이미 저장된 재료와 완전히 동일한 재료를 저장 시도하면 저장되지 않는다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave() {
        //given
        List<String> newIngredients = Arrays.asList("김치", "파", "감자");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(0); // 업데이트는 진행되지 않는다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoSearchRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> ingredientsCount = document.getIngredients().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Assertions.assertThat(ingredientsCount).containsEntry("김치", 1L);
            Assertions.assertThat(ingredientsCount).containsEntry("파", 1L);
            Assertions.assertThat(ingredientsCount).containsEntry("감자", 1L);
        });
    }

    @DisplayName("[happy] 저장된 적 없는 새로운 재료를 2번(ex: 고구마, 고구마)저장 시도하면 하나의 재료(고구마)만 저장된다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave2() {
        //given
        List<String> newIngredients = Arrays.asList("고구마", "고구마");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 진행된다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoSearchRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> ingredientsCount = document.getIngredients().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(ingredientsCount).containsEntry("고구마", 1L);
        });
    }

    @DisplayName("[happy] 이미 저장된 쟤료(ex: 김치, 김치)를 2번 저장 시도하면 저장되지 않는다.")
    @Test
    void ifAlreadyExistIngredient_whenSaveSameIngredients_thenNotSave3() {
        //given
        List<String> newIngredients = Arrays.asList("김치", "김치");

        //when
        Long savedCount = sut.saveIngredientsIntoMongo(newIngredients);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(0); // 업데이트가 진행된다.

        // 김치, 파, 감자가 각각 1개씩만 저장되어 있는지 검증한다.
        mongoSearchRepository.findById(documentId).ifPresent(document -> {
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
        SearchDocument document = mongoTemplate.findOne(query, SearchDocument.class); // 쿼리를 사용하여 문서를 찾음
        System.out.println("저장전 재료: " + document.getIngredients());

        int originalSize = document.getIngredients().size(); // 테스트 시작 전, 원래 문서에 저장된 재료의 개수

        List<String> newIngredients = Arrays.asList("양상추", "호박"); // 저장하려는 새로운 재료 목록

        // when (중복 저장)
        int duplicateCount = 2; // 중복 저장을 위한 횟수 설정
        for (int i = 0; i < duplicateCount; i++) {
            sut.saveIngredientsIntoMongo(newIngredients); // 지정된 횟수만큼 재료를 저장하는 메소드 호출
        }

        // then
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class); // 업데이트된 문서를 다시 조회
        System.out.println("저장후 재료: " + updatedDocument.getIngredients());
        int updatedSize = updatedDocument.getIngredients().size(); // 업데이트 후의 재료 개수 측정
        int expectedSize = originalSize + (newIngredients.stream().anyMatch(ingredient -> !document.getIngredients().contains(ingredient)) ? newIngredients.size() : 0);

        assertNotNull(updatedDocument); // 문서가 실제로 존재하는지 확인
        assertTrue(updatedDocument.getIngredients().containsAll(newIngredients)); // 업데이트된 문서에 새로운 재료가 포함되었는지 확인
        assertEquals(expectedSize, updatedSize);
    }

    @DisplayName("[happy] 저장된적 없는 해시태그를 저장하면 문제없이 mongoDB에 저장된다.")
    @Test
    void testSaveHashtagsIntoMongo() {

        // given
        List<String> newHashtags = Arrays.asList("해시태그4", "해시태그5");

        // when
        Long savedCount = sut.saveHashTagsIntoMongo(newHashtags);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument hashtagDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(hashtagDocument); // null이 아님을 검증
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(hashtagDocument.getHashtags().containsAll(newHashtags));
    }


    @DisplayName("[happy] 이미 저장된 해시태그와 저장된적 없는 해시태그를 저장 시도하면 저장된적 없는 해시태그만 저장된다.")
    @Test
    void whenAlreadyExistHashtagsIfUserInsertHashtagNotSave2() {
        // given
        List<String> newHashtags = Arrays.asList("해시태그1", "해시태그4");

        // when
        Long savedCount = sut.saveHashTagsIntoMongo(newHashtags);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 성공했는지 확인 1이면 성공
        assertTrue(updatedDocument.getHashtags().contains("해시태그4")); // 새로운 해시태그만 저장됨
    }


    @DisplayName("[happy] 이미 저장된 해시태그들을 저장 시도하면 저장되지 않는다.")
    @Test
    void ifAlreadyExistHashtags_whenSaveSameHashtags_thenNotSave() {
        //given
        List<String> newHashtags = Arrays.asList("해시태그1", "해시태그2", "해시태그3");

        //when
        Long savedCount = sut.saveHashTagsIntoMongo(newHashtags);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(0); // 업데이트는 진행되지 않는다.

        // 이미 저장된 해시태그가 중복저장되었는지 검증: 1L이면 정상이다.
        mongoSearchRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> HashtagsCount = document.getHashtags().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(HashtagsCount).containsEntry("해시태그1", 1L);
            Assertions.assertThat(HashtagsCount).containsEntry("해시태그2", 1L);
            Assertions.assertThat(HashtagsCount).containsEntry("해시태그3", 1L);
        });
    }

    @DisplayName("[happy] 저장된적 없지만 똑같은 해시태그 2개를(ex: 해시태그5, 해시태그5)저장 시도하면 하나만 저장된다.")
    @Test
    void ifAlreadyExistHashtags_whenSaveSameHashtags_thenNotSave2() {
        //given
        List<String> newHashtags = Arrays.asList("해시태그5", "해시태그5");

        //when
        Long savedCount = sut.saveHashTagsIntoMongo(newHashtags);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument); // null 체크
        Assertions.assertThat(savedCount).isEqualTo(1); // 업데이트가 진행된다.

        mongoSearchRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> hashtagsCount = document.getHashtags().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(hashtagsCount).containsEntry("해시태그5", 1L);
        });
    }

    @DisplayName("[happy] 이미 저장된 똑같은 해시태그 2개를(ex: 해시태그1, 해시태그1)저장 시도하면 저장되지 않는다.")
    @Test
    void ifAlreadyExistHashtags_whenSaveSameHashtags_thenNotSave3() {
        //given
        List<String> newHashtags = Arrays.asList("해시태그1", "해시태그1");

        //when
        Long savedCount = sut.saveHashTagsIntoMongo(newHashtags);

        // then
        Query query = new Query(Criteria.where("id").is(documentId));
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class);

        assertNotNull(updatedDocument);
        Assertions.assertThat(savedCount).isEqualTo(0);

        mongoSearchRepository.findById(documentId).ifPresent(document -> {
            Map<String, Long> hashtagsCount = document.getHashtags().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Assertions.assertThat(hashtagsCount).containsEntry("해시태그1", 1L);
        });
    }

    @DisplayName("[happy] 오류로 인해 내부적으로 해시태그 저장 메서드를 2번 호출해도 문제가 발생하지 않는다.(중복이면 알아서 한번만 저장된다.)")
    @Test
    void testSaveDuplicateHashtagsIntoMongo() {

        // given
        Query query = new Query(Criteria.where("id").is(documentId)); // 해당 ID로 문서를 찾기 위한 쿼리 생성
        SearchDocument document = mongoTemplate.findOne(query, SearchDocument.class); // 쿼리를 사용하여 문서를 찾음
        System.out.println("저장전 해시태그: " + document.getHashtags());

        int originalSize = document.getHashtags().size(); // 테스트 시작 전, 원래 문서에 저장된 재료의 개수

        List<String> newHashtags = Arrays.asList("해시태그5", "해시태그6"); // 저장하려는 새로운 재료 목록

        // when (중복 저장)
        int duplicateCount = 2; // 중복 저장을 위한 횟수 설정
        for (int i = 0; i < duplicateCount; i++) {
            sut.saveHashTagsIntoMongo(newHashtags); // 지정된 횟수만큼 재료를 저장하는 메소드 호출
        }

        // then
        SearchDocument updatedDocument = mongoTemplate.findOne(query, SearchDocument.class); // 업데이트된 문서를 다시 조회
        System.out.println("저장후 해시태그: " + updatedDocument.getHashtags());
        int updatedSize = updatedDocument.getHashtags().size(); // 업데이트 후의 재료 개수 측정
        int expectedSize = originalSize + (newHashtags.stream().anyMatch(hashtag -> !document.getHashtags().contains(hashtag)) ? newHashtags.size() : 0);

        assertNotNull(updatedDocument); // 문서가 실제로 존재하는지 확인
        assertTrue(updatedDocument.getHashtags().containsAll(newHashtags)); // 업데이트된 문서에 새로운 재료가 포함되었는지 확인
        assertEquals(expectedSize, updatedSize);
    }

    @DisplayName("dataType이 재료나 해시태그가 아닐 경우 예외가 발생한다.")
    @Test
    void saveInvalidDataTypeThrowsException() {
        // 유효하지 않은 dataType
        String dataType = "invalidType";
        List<String> newData = Arrays.asList("무언가");

        assertThrows(RecipeApplicationException.class, () -> {
            sut.saveDataIntoMongo(dataType, newData);
        });
    }

    @DisplayName("[happy] 한글 접두사로 재료를 검색하면 해당하는 재료가 반환된다.")
    @Test
    void testFindIngredientsWithKoreanPrefix() {
        // given
        String fieldName = "ingredients";
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "김치", 10);

        // when
        List<String> result = sut.searchData(dto, fieldName);

        // then
        assertNotNull(result);
        List<String> validIngredients = Arrays.asList("김치", "김", "김가루", "김밥", "김빱");

        assertTrue(result.stream().allMatch(ingredient ->
                validIngredients.stream().anyMatch(ingredient::contains)));

    }

    @DisplayName("[happy] 영문 접두사로 재료를 검색하면 해당하는 재료가 반환된다.")
    @Test
    void testFindIngredientsWithEnglishPrefix() {
        // given
        String fieldName = "ingredients";
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "ca", 10);

        // when
        List<String> result = sut.searchData(dto, fieldName);

        // then
        assertNotNull(result);
        assertTrue(result.stream().allMatch(ingredient -> ingredient.toLowerCase().contains(dto.getSearchWord().toLowerCase())));
    }

    @DisplayName("[happy] 존재하지 않는 접두사로 재료를 검색하면 빈 목록이 반환된다.")
    @Test
    void testFindIngredientsWithNonexistentPrefix() {
        // given
        String fieldName = "ingredients";
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "zzzk", 10);

        // when
        List<String> result = sut.searchData(dto, fieldName);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("[bad] 접두사가 없는 경우 빈 목록이 반환된다.")
    @Test
    void testFindIngredientsWithNoPrefix() {
        // given
        String fieldName = "ingredients";
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "", 10);

        // when
        List<String> result = sut.searchData(dto, fieldName);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



}