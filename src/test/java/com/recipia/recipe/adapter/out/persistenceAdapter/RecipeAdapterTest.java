package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.domain.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("레시피 어뎁터 클래스 테스트")
@ActiveProfiles(value = "test")
@SpringBootTest
class RecipeAdapterTest {

    @Autowired
    private RecipeQueryRepository queryRepository;

    @Autowired
    private RecipeRepository recipeRepository;


    @DisplayName("[happy] - 유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateRecipesNicknames() {

        //given
        Long memberId = 1L;
        String changedNickname = "ChangedNickname";
        NicknameDto mockDto = NicknameDto.of(memberId, changedNickname);

        //when
        queryRepository.updateRecipesNicknames(mockDto);
        String nickname = recipeRepository.findById(memberId).get().getNickname();

        //then
        Assertions.assertThat(nickname).isEqualTo(changedNickname);
    }

    @DisplayName("[happy] - 유저가 레시피 저장에 성공하면 생성된 레시피의 id가 반환된다.")
    @Transactional
    @Test
    public void createRecipeSuccessReturnRecipeId() {

        //given
        RecipeEntity entity = createRecipe();

        //when
        RecipeEntity savedEntity = recipeRepository.save(entity);

        //then
        Assertions.assertThat(savedEntity).isEqualTo(entity);
        Assertions.assertThat(savedEntity.getId()).isEqualTo(entity.getId());
    }

    private RecipeEntity createRecipe() {
        return RecipeEntity.of(1L, "레시피", "레시피 설명", 20, "닭", "#닭발", "{당류: 많음}", "진안", "N");
    }


}