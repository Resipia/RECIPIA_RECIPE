package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 레시피 저장할때 사용하는 DTO
 */
@Data
@NoArgsConstructor
public class RecipeCreateRequestDto {

    @NotBlank(message = "레시피 이름은 필수입니다.")
    @Size(max = 100, message = "레시피 이름은 100자를 초과할 수 없습니다.")
    private String recipeName;   // 레시피명

    @NotBlank(message = "레시피 설명은 필수입니다.")
    @Size(max = 3000, message = "레시피 설명은 3000자를 초과할 수 없습니다.")
    private String recipeDesc;   // 레시피 설명

    private Integer timeTaken;   // 레시피 따라하는데 필요한 시간

    @Pattern(regexp = "^([가-힣a-zA-Z0-9]+)(, [가-힣a-zA-Z0-9]+)*$", message = "재료는 '재료1, 재료2, 재료3' 형식으로 입력해야 합니다.")
    private String ingredient;   // 재료

    @Pattern(regexp = "^([가-힣a-zA-Z0-9]+)(, [가-힣a-zA-Z0-9]+)*$", message = "해시태그는 '#해시태그1, #해시태그2, #해시태그3' 형식으로 입력해야 합니다.")
    private String hashtag;      // 해시태그

    private NutritionalInfoDto nutritionalInfo;   // 영양소 dto

    @Builder
    private RecipeCreateRequestDto(String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfoDto nutritionalInfo) {
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nutritionalInfo = nutritionalInfo;
    }

    public static RecipeCreateRequestDto of(String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfoDto nutritionalInfo) {
        return new RecipeCreateRequestDto(recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo);
    }

    /**
     * 테스트용
     */
    public static RecipeCreateRequestDto of(String recipeName, String recipeDesc) {
        return new RecipeCreateRequestDto(recipeName, recipeDesc, null, null, null, null);
    }

}
