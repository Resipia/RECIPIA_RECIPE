package com.recipia.recipe.adapter.in.web.dto.response;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeFileDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 레시피 상세보기 응답 dto
 */
@NoArgsConstructor
@Data
public class RecipeDetailViewDto {
    private Long id;                    // 레시피 id
    private String recipeName;          // 레시피명
    private String recipeDesc;          // 레시피 설명
    private Integer timeTaken;          // 레시피 따라하는데 필요한 시간
    private String ingredient;          // 재료
    private String hashtag;             // 해시태그
    private NutritionalInfoDto nutritionalInfoDto;   // 영양소 dto
    private List<SubCategoryDto> subCategoryDtoList; // 서브 카테고리
    private String nickname;            // 회원 닉네임
    private List<RecipeFileDto> recipeFileDtoList; // 레시피와 연관된 파일(이미지) 리스트
    private String delYn;               // 레시피 삭제여부
    private boolean isBookmarked;       // 북마크 여부

//    private int like; //todo: 좋아요 나중에 추가
//    private int viewCount; //todo: 조회수 나중에 추가


    /**
     * 모든 필드를 다 생성자에 넣으면 querydsl Projection에서 오류가 나서 필요한 데이터는 setter로 넣도록 함
     */
    @Builder
    public RecipeDetailViewDto(Long id, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, String delYn, boolean isBookmarked) {
        this.id = id;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nickname = nickname;
        this.delYn = delYn;
        this.isBookmarked = isBookmarked;
    }

    public static RecipeDetailViewDto of(Long id, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, String delYn, boolean isBookmarked) {
        return new RecipeDetailViewDto(id, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nickname, delYn, isBookmarked);
    }

}
