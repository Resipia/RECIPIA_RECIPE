package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

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


    @DisplayName("[happy] 유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateRecipesNicknames() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(1L, "changedNickname");

        //when
        Long updatedCount = sut.updateRecipesNicknames(nicknameDto);

        //then
        String nickname = recipeRepository.findById(nicknameDto.memberId()).get().getNickname();
        assertThat(nickname).isEqualTo(nicknameDto.nickname());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[bad] 존재하지 않는 유저(memberId)가 닉네임 변경을 시도하면 예외가 발생한다.")
    @Test
    void updateRecipesNicknamesFail() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(100L, "NotValidNickname");

        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateRecipesNicknames(nicknameDto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("[happy] 유저가 레시피 저장에 성공하면 생성된 레시피의 id가 반환된다.")
    @Transactional
    @Test
    public void createRecipe() {
        //given
        Recipe domain = createRecipeDomain(1L);

        //when
        Long recipeId = sut.createRecipe(domain);

        //then
        assertThat(recipeId).isNotNull();
        Optional<RecipeEntity> savedRecipe = recipeRepository.findById(recipeId);
        assertThat(savedRecipe.isPresent()).isTrue();
    }

    @DisplayName("[happy] 새롭게 저장할 레시피의 영양소 정보 저장에 성공하면, 저장된 정보의 ID를 반환한다.")
    @Test
    public void createNutritionalInfo() {
        // given
        Recipe domain = createRecipeDomain(1L);
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

    private Recipe createRecipeDomain(long memberId) {
        return Recipe.of(
                memberId,
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