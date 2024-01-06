package com.recipia.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Recipe 엔티티에 대한 도메인 객체이다.
 */
@NoArgsConstructor
@Getter
@Setter
public class Recipe {

    private Long id;             // recipe pk
    private Long memberId;       // 회원 ID
    private String recipeName;   // 레시피명
    private String recipeDesc;   // 레시피 설명
    private Integer timeTaken;   // 레시피 따라하는데 필요한 시간
    private String ingredient;   // 재료
    private String hashtag;      // 해시태그
    private NutritionalInfo nutritionalInfo;   // 영양소 도메인
    private List<SubCategory> subCategory; // 맵핑된 서브 카테고리 도메인
    private String nickname;     // 회원 닉네임
    private List<RecipeFile> recipeFileList; // 레시피와 연관된 파일 리스트 도메인
    private String delYn;        // 레시피 삭제여부



    @Builder
    private Recipe(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn) {
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
    }


    /**
     * 파일이 존재할때 도메인 객체 생성
     */
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, recipeFileList, delYn);
    }

    /**
     * 파일이 없을때 도메인 객체 생성
     */
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, String delYn) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, Collections.emptyList(), delYn);
    }

    /**
     * 레시피 생성할때 컨버터에서 사용
     */
    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, List<RecipeFile> recipeFileList, String delYn) {
        return new Recipe(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, recipeFileList, delYn);
    }

    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, List<SubCategory> subCategory, String nickname, String delYn) {
        return new Recipe(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, subCategory, nickname, Collections.emptyList(), delYn);
    }


}
