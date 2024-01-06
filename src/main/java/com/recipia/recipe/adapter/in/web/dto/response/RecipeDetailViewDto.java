package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 레시피 상세보기 응답 dto
 */
@NoArgsConstructor
@Data
public class RecipeDetailViewDto {
    private Long id; // 레시피 id
    private String recipeName;   // 레시피명
    private String nickname;     // 회원 닉네임
    private String recipeDesc;   // 레시피 설명
    private List<String> subCategoryList; // 카테고리
    private boolean isBookmarked; // 북마크 여부
//    private int like; //todo: 좋아요 나중에 추가
//    private int viewCount; //todo: 조회수 나중에 추가
    // todo: 추후 이미지 추가

    public RecipeDetailViewDto(Long id, String recipeName, String nickname, String recipeDesc, boolean isBookmarked) {
        this.id = id;
        this.recipeName = recipeName;
        this.nickname = nickname;
        this.recipeDesc = recipeDesc;
        this.isBookmarked = isBookmarked;
    }

    public static RecipeDetailViewDto of(String recipeName, String nickname, String recipeDesc, boolean isBookmarked) {
        return new RecipeDetailViewDto(null, recipeName, nickname, recipeDesc, isBookmarked);
    }

}
