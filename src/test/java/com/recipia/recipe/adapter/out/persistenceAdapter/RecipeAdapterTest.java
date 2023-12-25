package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.document.IngredientDocument;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.config.TotalTestSupport;
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

@DisplayName("[통합] 레시피 Adapter 테스트")
class RecipeAdapterTest extends TotalTestSupport {

    @Autowired
    private RecipeAdapter sut;

    @Autowired
    private RecipeRepository recipeRepository;

//    @MockBean
//    private ReactiveMongoTemplate mongoTemplate;


    @DisplayName("[happy] 유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateRecipesNicknames() {

        //given
        Long memberId = 1L;
        String changedNickname = "ChangedNickname";
        NicknameDto mockDto = NicknameDto.of(memberId, changedNickname);

        //when
        Long updatedCount = sut.updateRecipesNicknames(mockDto);
        String nickname = recipeRepository.findById(memberId).get().getNickname();

        //then
        Assertions.assertThat(nickname).isEqualTo(changedNickname);
        Assertions.assertThat(updatedCount).isNotNull();
    }

    @DisplayName("[happy] 유저가 레시피 저장에 성공하면 생성된 레시피의 id가 반환된다.")
    @Transactional
    @Test
    public void createRecipeSuccessReturnRecipeId() {

        //given
        Recipe domain = createRecipeDomain();

        //when
        Long sutRecipe = sut.createRecipe(domain);

        //then
        Assertions.assertThat(sutRecipe).isEqualTo(10L);
    }

    private RecipeEntity createRecipeEntity() {
        return RecipeEntity.of(1L, "레시피", "레시피 설명", 20, "닭", "#닭발", "{당류: 많음}", "진안", "N");
    }

    private Recipe createRecipeDomain() {
        return Recipe.of(10L, 1L, "레시피", "레시피 설명", 20, "닭", "#닭발", "{당류: 많음}", "진안", "N");
    }


}