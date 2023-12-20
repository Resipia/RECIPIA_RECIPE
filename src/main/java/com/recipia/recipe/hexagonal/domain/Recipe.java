package com.recipia.recipe.hexagonal.domain;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Recipe 엔티티에 대한 도메인 객체이다.
 */
@NoArgsConstructor
@Getter
public class Recipe {

    private Long id;             // recipe pk
    private Long memberId;       // 회원 ID
    private String recipeName;   // 레시피명
    private String recipeDesc;   // 레시피 설명
    private Integer timeTaken;   // 레시피 따라하는데 필요한 시간
    private String ingredient;   // 재료
    private String hashtag;      // 해시태그
    private String nutritionalInfo;   // 영양소 정보
    private String nickname;     // 회원 닉네임
    private String delYn;        // 레시피 삭제여부

    @Builder
    private Recipe(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nutritionalInfo, String nickname, String delYn) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nutritionalInfo = nutritionalInfo;
        this.nickname = nickname;
        this.delYn = delYn;
    }

    // factory method 선언
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nutritionalInfo, String nickname, String delYn) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, nickname, delYn);
    }


}
