package com.recipia.recipe.domain;

import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String nickname;     // 회원 닉네임
    private String delYn;        // 레시피 삭제여부

    @Builder
    private Recipe(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, String nickname, String delYn) {
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
    public static Recipe of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, String nickname, String delYn) {
        return new Recipe(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, nickname, delYn);
    }

    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, NutritionalInfo nutritionalInfo, String nickname, String delYn) {
        return new Recipe(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, nickname, delYn);
    }

    public void change(Long memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

}
