package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles(value = "test")
@SpringBootTest
class RecipeAdapterTest {

    @Autowired
    private RecipeQueryRepository queryRepository;

    @Autowired
    private RecipeRepository recipeRepository;


    @DisplayName("유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void test() {

        //given
        Long memberId = 1L;
        String changedNickname = "ChangedNickname";
        NicknameDto mockDto = NicknameDto.of(memberId, changedNickname);

        //when
        queryRepository.updateRecipesNicknamesForMemberId(mockDto);
        String nickname = recipeRepository.findById(memberId).get().getNickname();

        //then
        Assertions.assertThat(nickname).isEqualTo(changedNickname);
    }


}