package com.recipia.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Recipe 엔티티에 대한 도메인 객체이다.
 */
@NoArgsConstructor
@Getter
@Setter
public class Recipe {

    private Long id;                            // recipe pk
    private Long memberId;                      // 회원 ID
    private String recipeName;                  // 레시피명
    private String recipeDesc;                  // 레시피 설명
    private Integer timeTaken;                  // 레시피 따라하는데 필요한 시간
    private String ingredient;                  // 재료
    private String hashtag;                     // 해시태그
    private NutritionalInfo nutritionalInfo;    // 영양소 도메인
    private List<SubCategory> subCategory;      // 맵핑된 서브 카테고리 도메인
    private String nickname;                    // 회원 닉네임
    private List<RecipeFile> recipeFileList;    // 레시피와 연관된 파일 리스트 도메인
    private String delYn;                       // 레시피 삭제여부
    private Long recipeLikeId;                  // 좋아요 id
    private Integer likeCount;                  // 레시피 좋아유 개수
    private Long bookmarkId;                    // 북마크 id
    private Integer viewCount;                  // 레시피 조회수
    private List<Integer> deleteFileOrder;      // 삭제할 이미지의 order
    private LocalDateTime createDate;           // 레시피 생성일

    @Builder
    public Recipe(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn, Long recipeLikeId, Integer likeCount, Long bookmarkId, Integer viewCount, List<Integer> deleteFileOrder, LocalDateTime createDate) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nutritionalInfo = nutritionalInfo;
        this.subCategory = subCategory;
        this.nickname = nickname;
        this.recipeFileList = recipeFileList;
        this.delYn = delYn;
        this.recipeLikeId = recipeLikeId;
        this.likeCount = likeCount;
        this.bookmarkId = bookmarkId;
        this.viewCount = viewCount;
        this.deleteFileOrder = deleteFileOrder;
        this.createDate = createDate;
    }

    /**
     * 파일이 존재할때 도메인 객체 생성
     */
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn, Long recipeLikeId, Integer likeCount, Long bookmarkId, Integer viewCount, List<Integer> deleteFileOrder, LocalDateTime createDate) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, recipeFileList, delYn, recipeLikeId, likeCount, bookmarkId, viewCount, deleteFileOrder, createDate);
    }

    /**
     * 파일이 없을때 도메인 객체 생성
     * 레시피를 업데이트 할때는 DB에 좋아요 수, 조회수를 넣지 않기에 null로 세팅한다.
     */
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, String delYn, Long recipeLikeId, Long bookmarkId, List<Integer> deleteFileOrder, LocalDateTime createDate) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, Collections.emptyList(), delYn, recipeLikeId, null, bookmarkId, null, deleteFileOrder, createDate);
    }

    /**
     * 레시피 생성할때 컨버터에서 사용
     */
    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn, Long recipeLikeId, Integer likeCount, Long bookmarkId, Integer viewCount, List<Integer> deleteFileOrder, LocalDateTime createDate) {
        return new Recipe(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, recipeFileList, delYn, recipeLikeId, likeCount, bookmarkId, viewCount, deleteFileOrder, createDate);
    }

    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, String delYn, Long recipeLikeId, Integer likeCount, Long bookmarkId, Integer viewCount, List<Integer> deleteFileOrder, LocalDateTime createDate) {
        return new Recipe(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, Collections.emptyList(), delYn, recipeLikeId, likeCount, bookmarkId, viewCount, deleteFileOrder, createDate);
    }

    /**
     * s3 업로드에 사용
     */
    public static Recipe of(Long id) {
        return new Recipe(id, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 상세조회에서 도메인 객체를 만들때 사용
     */
    public static Recipe of(Long recipeId, Long currentMemberId) {
        return new Recipe(recipeId, currentMemberId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
