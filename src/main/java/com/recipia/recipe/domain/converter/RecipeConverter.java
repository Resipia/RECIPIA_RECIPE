package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.*;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
                null, // fixme: 영양소
                null, // subCategory
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
     * Recipe 도메인 내부의 영양소 도메인을 엔티티로 변환
     * 영양소를 업데이트 할때는 이 컨버터를 사용해야 한다.
     * 왜냐하면 저장할때는 id(pk)가 필요없지만 이미 저장된 영양소 엔티티가 존재하면 그 엔티티의 id(pk)를 가져와서 조건문에서 사용해야 하기 때문이다.
     */
    public NutritionalInfoEntity domainToNutritionalInfoEntityUpdate(Recipe domain) {

        NutritionalInfo nutritionalInfo = domain.getNutritionalInfo();
        return NutritionalInfoEntity.of(
                nutritionalInfo.getId(),
                nutritionalInfo.getCarbohydrates(),
                nutritionalInfo.getProtein(),
                nutritionalInfo.getFat(),
                nutritionalInfo.getVitamins(),
                nutritionalInfo.getMinerals()
        );
    }

    /**
     * Recipe 도메인 내부의 영양소 도메인을 엔티티로 변환하는 로직
     * 레시피 엔티티에는 저장할때 꼭 필요한 pk값인 id만 필드로 하여 저장해 준다.(최적화)
     */
    public RecipeCategoryMapEntity domainToRecipeCategoryMapEntity(Recipe recipe, SubCategory subCategory) {
        return RecipeCategoryMapEntity.of(
                RecipeEntity.of(recipe.getId()),
                SubCategoryEntity.of(subCategory.getId())
        );
    }

    /**
     * [entity to domain]
     * 레시피를 생성할때 컨트롤러에 들어온 요청 dto객체를 도메인으로 변환시키는 메서드
     * 여기서 도메인 객체를 만들어서 서비스 레이어에 보낸다.
     */
    public Recipe recipeCreateDtoToDomain(RecipeCreateUpdateRequestDto dto) {
        NutritionalInfo nutritionalInfo = getNutritionalInfo(dto);
        List<SubCategory> subCategories = getSubCategories(dto);

        // jwt 클레임으로부터 memberId, nickname을 꺼내서 주입한다.
        return Recipe.of(
                securityUtil.getCurrentMemberId(),
                dto.getRecipeName(),
                dto.getRecipeDesc(),
                dto.getTimeTaken(),
                dto.getIngredient(),
                dto.getHashtag(),
                nutritionalInfo,
                subCategories,
                securityUtil.getCurrentMemberNickname(),
                "N"
        );
    }

    /**
     * 레시피 파일 도메인을 엔티티로 변환
     */
    public RecipeFileEntity domainToRecipeFileEntity(RecipeFile recipeFile) {
        return RecipeFileEntity.of(
                RecipeEntity.of(recipeFile.getRecipe().getId()),
                recipeFile.getFileOrder(),
                recipeFile.getStoredFilePath(),
                recipeFile.getObjectUrl(),
                recipeFile.getOriginFileNm(),
                recipeFile.getStoredFileNm(),
                recipeFile.getFileExtension(),
                recipeFile.getFileSize(),
                recipeFile.getDelYn()
        );
    }

    /**
     * 레시피를 생성할때 요청 dto에서 영양소 정보를 뽑아낸 후 영양소 도메인 객체로 변환해주는 메서드
     */
    public NutritionalInfo getNutritionalInfo(RecipeCreateUpdateRequestDto dto) {
        return NutritionalInfo.of(
                dto.getNutritionalInfo().getCarbohydrates(),
                dto.getNutritionalInfo().getProtein(),
                dto.getNutritionalInfo().getFat(),
                dto.getNutritionalInfo().getVitamins(),
                dto.getNutritionalInfo().getMinerals()
        );
    }

    /**
     * 레시피를 생성할때 요청 dto에서 서브 카테고리 정보를 뽑아낸 후 저장할때 필요한 서브 카테고리 도메인 객체로 변환
     */
    public List<SubCategory> getSubCategories(RecipeCreateUpdateRequestDto dto) {
        return dto.getSubCategoryList()
                .stream()
                .map(SubCategory::of)
                .collect(Collectors.toList());
    }
}
