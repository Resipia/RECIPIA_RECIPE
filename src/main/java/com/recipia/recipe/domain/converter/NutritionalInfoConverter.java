package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NutritionalInfoConverter {

    /**
     * Recipe 도메인 내부의 영양소 도메인을 엔티티로 변환하는 로직
     * 레시피 엔티티에는 저장할때 꼭 필요한 pk값인 id만 필드로 하여 저장해 준다.(최적화)
     */
    public NutritionalInfoEntity domainToEntityCreate(Recipe domain) {

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
     * Recipe 도메인 내부의 영양소 도메인을 엔티티로 변환
     * 영양소를 업데이트 할때는 이 컨버터를 사용해야 한다.
     * 왜냐하면 저장할때는 id(pk)가 필요없지만 이미 저장된 영양소 엔티티가 존재하면 그 엔티티의 id(pk)를 가져와서 조건문에서 사용해야 하기 때문이다.
     */
    public NutritionalInfoEntity domainToEntityUpdate(NutritionalInfo domain) {
        return NutritionalInfoEntity.of(
                domain.getId(),
                domain.getCarbohydrates(),
                domain.getProtein(),
                domain.getFat(),
                domain.getVitamins(),
                domain.getMinerals()
        );
    }

    /**
     * 레시피를 생성할때 요청 dto에서 영양소 정보를 뽑아낸 후 영양소 도메인 객체로 변환해주는 메서드
     */
    public NutritionalInfo dtoToDomain(RecipeCreateUpdateRequestDto dto) {
        return NutritionalInfo.of(
                dto.getNutritionalInfo().getCarbohydrates(),
                dto.getNutritionalInfo().getProtein(),
                dto.getNutritionalInfo().getFat(),
                dto.getNutritionalInfo().getVitamins(),
                dto.getNutritionalInfo().getMinerals()
        );
    }

    /**
     * 영양소 엔티티를 도메인으로 변환한다.
     */
    public NutritionalInfo entityToNutritionalInfo(NutritionalInfoEntity entity) {
        return NutritionalInfo.of(
                entity.getId(),
                entity.getCarbohydrates(),
                entity.getProtein(),
                entity.getFat(),
                entity.getVitamins(),
                entity.getMinerals()
        );
    }

    public NutritionalInfoDto domainToDto(NutritionalInfo domain) {
        return NutritionalInfoDto.of(
                domain.getCarbohydrates(),
                domain.getProtein(),
                domain.getFat(),
                domain.getVitamins(),
                domain.getMinerals()
        );
    }

}
