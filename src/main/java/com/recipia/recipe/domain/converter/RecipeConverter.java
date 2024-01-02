package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * domain, entity, dto 서로간의 의존성을 제거하기 위해 Converter 클래스를 작성
 */
@RequiredArgsConstructor
@Component
public class RecipeConverter {

    private final SecurityUtil securityUtil;

    /**
     * Recipe 엔티티를 받아서 Recipe 도메인으로 변환
     */
    // entity to domain
    public Recipe entityToDomain(RecipeEntity entity) {
        return Recipe.of(
                entity.getId(),
                entity.getMemberId(),
                entity.getRecipeName(),
                entity.getRecipeDesc(),
                entity.getTimeTaken(),
                entity.getIngredient(),
                entity.getHashtag(),
                null, // fixme: 확인해보기
                entity.getNickname(),
                entity.getDelYn()
        );
    }

    /**
     * Recipe 도메인을 받아서 RecipeEntity 엔티티로 변환
     */
    public RecipeEntity domainToRecipeEntity(Recipe domain) {
        return RecipeEntity.of(
                domain.getId(),
                domain.getMemberId(),
                domain.getRecipeName(),
                domain.getRecipeDesc(),
                domain.getTimeTaken(),
                domain.getIngredient(),
                domain.getHashtag(),
                domain.getNickname(),
                domain.getDelYn()
        );
    }

    /**
     * Recipe 도메인 내부의 영양소 도메인을 엔티티로 변환하는 로직
     * 레시피 엔티티에는 저장할때 꼭 필요한 pk값인 id만 필드로 하여 저장해 준다.(최적화)
     */
    public NutritionalInfoEntity domainToNutritionalInfoEntity(Recipe domain) {

        NutritionalInfo nutritionalInfo = domain.getNutritionalInfo();
        return NutritionalInfoEntity.of(
                nutritionalInfo.getCarbohydrates(),
                nutritionalInfo.getProtein(),
                nutritionalInfo.getFat(),
                nutritionalInfo.getVitamins(),
                nutritionalInfo.getMinerals(),
                RecipeEntity.of(domain.getId())
        );
    }

    /**
     * 레시시 생성을 요청하는 RecipeCreateRequestDto객체를 도메인으로 변환
     * jwt 클레임으로부터 memberId, nickname을 꺼내서 주입한다.
     */
    // entity to domain
    public Recipe requestDtoToDomain(RecipeCreateRequestDto dto) {
        return Recipe.of(
                securityUtil.getCurrentMemberId(),
                dto.getRecipeName(),
                dto.getRecipeDesc(),
                dto.getTimeTaken(),
                dto.getIngredient(),
                dto.getHashtag(),
                NutritionalInfo.of(
                        dto.getNutritionalInfo().getCarbohydrates(),
                        dto.getNutritionalInfo().getProtein(),
                        dto.getNutritionalInfo().getFat(),
                        dto.getNutritionalInfo().getVitamins(),
                        dto.getNutritionalInfo().getMinerals()
                ),
                securityUtil.getCurrentMemberNickname(),
                "N" // todo: 하드코딩 맞는지 알아보기
        );
    }


}
