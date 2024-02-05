package com.recipia.recipe.adapter.in.web.dto.response;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레시피 상세보기 응답 dto
 */
@NoArgsConstructor
@Data
public class RecipeDetailViewResponseDto {
    private Long id;                                    // 레시피 id
    private Long memberId;                              // 멤버 id
    private String recipeName;                          // 레시피명
    private String recipeDesc;                          // 레시피 설명
    private Integer timeTaken;                          // 레시피 따라하는데 필요한 시간
    private String ingredient;                          // 재료
    private String hashtag;                             // 해시태그
    private NutritionalInfoDto nutritionalInfoDto;      // 영양소 dto
    private List<SubCategoryDto> subCategoryDtoList;    // 서브 카테고리
    private String nickname;                            // 레시피 작성자 닉네임
    private List<RecipeFileResponseDto> recipeFileUrlList;             // 레시피와 연관된 파일(이미지) 리스트
    private Long bookmarkId;                            // 북마크 id
    private Long recipeLikeId;                          // 좋아요 id
    private LocalDateTime createDate;                   // 레시피 생성일
    private Integer likeCount;                          // 레시피 좋아요 갯수
//    private int viewCount; //todo: 조회수 나중에 추가


    /**
     * 모든 필드를 다 생성자에 넣으면 querydsl Projection에서 오류가 나서 필요한 데이터는 setter로 넣도록 함
     */
    @Builder
    private RecipeDetailViewResponseDto(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, Long bookmarkId, Long recipeLikeId, LocalDateTime createDate, Integer likeCount) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nickname = nickname;
        this.bookmarkId = bookmarkId;
        this.recipeLikeId = recipeLikeId;
        this.createDate = createDate;
        this.likeCount = likeCount;
    }

    public static RecipeDetailViewResponseDto of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, Long bookmarkId, Long recipeLikeId, LocalDateTime createDate, Integer likeCount) {
        return new RecipeDetailViewResponseDto(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nickname, bookmarkId, recipeLikeId, createDate, likeCount);
    }

}
