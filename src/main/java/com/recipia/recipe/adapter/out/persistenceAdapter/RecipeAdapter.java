package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeAdapter implements RecipePort {

    private final RecipeQueryRepository recipeQueryRepository;
    private final RecipeRepository recipeRepository;

    /**
     * memberId로 유저가 작성한 모든 레시피를 조회한 다음 그 레시피 엔티티가 가진 유저의 닉네임 컬럼을 변경
     */
    @Override
    public Long updateRecipesNicknames(NicknameDto nicknameDto) {
        long updateCount = recipeQueryRepository.updateRecipesNicknames(nicknameDto);
        log.info("Updated {} recipe(s) with new nickname '{}' for memberId {}", updateCount, nicknameDto.nickname(), nicknameDto.memberId());
        return updateCount;
    }

    /**
     * 레시피를 저장하는 메서드
     * 저장에 성공하면 레시피 엔티티의 id값을 반환한다.
     */
    @Override
    public Long createRecipe(Recipe recipe) {
        RecipeEntity recipeEntity = RecipeConverter.domainToEntity(recipe);
        recipeRepository.save(recipeEntity);
        return recipeEntity.getId();
    }


}