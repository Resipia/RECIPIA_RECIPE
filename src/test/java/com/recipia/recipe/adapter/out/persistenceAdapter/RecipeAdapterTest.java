package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[통합] 레시피 Adapter 테스트")
class RecipeAdapterTest extends TotalTestSupport {

    @Autowired
    private RecipeAdapter sut;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NutritionalInfoRepository nutritionalInfoRepository;

//    @MockBean
//    private ReactiveMongoTemplate mongoTemplate;


    @DisplayName("[happy] 유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateRecipesNicknames() {

        //given
        NicknameDto mockDto = NicknameDto.of(1L, "changedNickname");

        //when
        Long updatedCount = sut.updateRecipesNicknames(mockDto);

        //then
        String nickname = recipeRepository.findById(mockDto.memberId()).get().getNickname();
        assertThat(nickname).isEqualTo(mockDto.nickname());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[happy] 유저가 레시피 저장에 성공하면 생성된 레시피의 id가 반환된다.")
    @Transactional
    @Test
    public void createRecipeSuccessReturnRecipeId() {

        //given
        Recipe domain = createRecipeDomain();

        //when
        Long recipeId = sut.createRecipe(domain);

        //then
        assertThat(recipeId).isNotNull();
        Optional<RecipeEntity> savedRecipe = recipeRepository.findById(recipeId);
        assertThat(savedRecipe.isPresent()).isTrue();
    }

    @DisplayName("[happy] 새롭게 저장할 레시피의 영양소 정보 저장에 성공하면, 저장된 정보의 ID를 반환한다.")
    @Test
    public void createNutritionalInfoSuccess() {
        // given
        Recipe domain = createRecipeDomain();
        Long recipeId = sut.createRecipe(domain);

        // when
        Long nutritionalInfoId = sut.createNutritionalInfo(domain, recipeId);

        // then
        assertThat(nutritionalInfoId).isNotNull();
        Optional<NutritionalInfoEntity> savedNutritionalInfo = nutritionalInfoRepository.findById(nutritionalInfoId);
        assertThat(savedNutritionalInfo.isPresent()).isTrue();
    }



    private RecipeEntity createRecipeEntity() {
        return RecipeEntity.of(
                1L,
                "레시피",
                "레시피 설명",
                20,
                "김치, 감자",
                "#고구마",
                "진안",
                "N"
        );
    }

    private NutritionalInfoEntity createNutritionalInfoEntity() {

        RecipeEntity recipeEntity = createRecipeEntity();
        return NutritionalInfoEntity.of(
                10,
                10,
                10,
                10,
                10,
                recipeEntity
        );
    }

    private Recipe createRecipeDomain() {
        return Recipe.of(
                1L,
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                NutritionalInfo.of(10,10,10,10,10),
                "진안",
                "N"
        );
    }


}