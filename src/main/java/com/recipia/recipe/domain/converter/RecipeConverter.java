package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeFileDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeCategoryMapEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.adapter.out.persistence.entity.SubCategoryEntity;
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

    private final NutritionalInfoConverter nutritionalInfoConverter;
    private final CategoryConverter categoryConverter;
    private final RecipeFileConverter recipeFileConverter;

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
                entity.getDelYn(),
                false // todo: 이거 북마크도 고민해보자
        );
    }

    /**
     * [entity to domain]
     * 레시피를 생성할때 컨트롤러에 들어온 요청 dto객체를 도메인으로 변환시키는 메서드
     * 여기서 도메인 객체를 만들어서 서비스 레이어에 보낸다.
     */
    public Recipe dtoToDomain(RecipeCreateUpdateRequestDto dto) {
        // 1.영양소 도메인을 받아온다.
        NutritionalInfo nutritionalInfo = nutritionalInfoConverter.dtoToDomain(dto);
        // 2. 서브 카테고리 도메인 리스트를 받아온다.
        List<SubCategory> subCategories = dtoToDomainList(dto);

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
                "N",
                false
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
    public RecipeCategoryMapEntity domainToRecipeCategoryMapEntity(Recipe recipe, SubCategory subCategory) {
        return RecipeCategoryMapEntity.of(
                RecipeEntity.of(recipe.getId()),
                SubCategoryEntity.of(subCategory.getId())
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
     * 레시피를 생성할때 요청 dto에서 서브 카테고리 정보를 뽑아낸 후 저장할때 필요한 서브 카테고리 도메인 객체로 변환
     */
    public List<SubCategory> dtoToDomainList(RecipeCreateUpdateRequestDto dto) {
        return dto.getSubCategoryDtoList()
                .stream()
                .map(categoryConverter::dtoToDomain)
                .collect(Collectors.toList());
    }

    /**
     * 컨트롤러 응답을 위해 도메인 객체를 응답 dto 객체로 변환한다.
     * 이 메서드는 레시피의 상세보기를 위한 전용이기 때문에 다른곳에서 필요해도 사용하면 안된다.
     */
    public RecipeDetailViewDto domainToResponseDto(Recipe domain) {

        // 1. 도메인 객체를 dto로 변환한다.
        NutritionalInfoDto nutritionalInfoDto = nutritionalInfoConverter.domainToDto(domain.getNutritionalInfo());

        List<SubCategoryDto> subCategoryDtoList = domain.getSubCategory().stream()
                .map(categoryConverter::domainToDto).toList();

        List<RecipeFileDto> recipeFileDtoList = domain.getRecipeFileList().stream()
                .map(recipeFileConverter::domainToDto)
                .toList();

        // 2. 변환된 dto 리스트를 추가시켜 준다.
        RecipeDetailViewDto recipeDetailViewDto = RecipeDetailViewDto.of(
                domain.getId(),
                domain.getRecipeName(),
                domain.getRecipeDesc(),
                domain.getTimeTaken(),
                domain.getIngredient(),
                domain.getHashtag(),
                domain.getNickname(),
                domain.getDelYn(),
                domain.isBookmarked()
        );

        recipeDetailViewDto.setNutritionalInfoDto(nutritionalInfoDto);
        recipeDetailViewDto.setSubCategoryDtoList(subCategoryDtoList);
        recipeDetailViewDto.setRecipeFileDtoList(recipeFileDtoList);
        return recipeDetailViewDto;
    }
}
